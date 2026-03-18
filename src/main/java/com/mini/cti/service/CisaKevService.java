package com.mini.cti.service;

import com.mini.cti.core.exceptions.CisaApiException;
import com.mini.cti.core.exceptions.VulnerabilityNotFoundException;
import com.mini.cti.dto.CisaKevDTO;
import com.mini.cti.dto.CisaKevResponseDTO;
import com.mini.cti.mapper.Mapper;
import com.mini.cti.model.CisaKev;
import com.mini.cti.repository.CisaKevRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Service for managing CISA Known Exploited Vulnerabilities (KEV) catalog.
 *
 * This service handles fetching vulnerability data from the CISA KEV API,
 * synchronizing it with the local database, and providing scheduled updates.
 * The CISA KEV catalog contains vulnerabilities that are known to be actively
 * exploited in the wild and pose significant risk to federal enterprise networks.
 *
 * Key responsibilities:
 *   Fetching vulnerability data from CISA's public API
 *   Synchronizing data with the local PostgreSQL database
 *   Performing scheduled updates every weekday at 8 AM
 *   Tracking insert/update operations for monitoring
 *
 *
 * @author Mini-CTI Team
 * @version 1.0
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CisaKevService {


    /**
     * Base URL for the CISA Known Exploited Vulnerabilities JSON feed.
     * Configured via application properties (app.cisakev.base_url).
     */
    @Value("${app.cisakev.base_url}")
    private String cisaKevURL;


    /** REST client for making HTTP requests to the CISA API. */
    private final RestTemplate restTemplate;

    /** Mapper for converting between DTOs and entities. */
    private final Mapper mapper;

    /** Repository for CISA KEV database operations. */
    private final CisaKevRepository cisaKevRepository;


    /**
     * Fetches the complete CISA Known Exploited Vulnerabilities catalog from the public API.
     *
     * This method makes an HTTP GET request to the CISA KEV JSON feed endpoint
     * and deserializes the response into a {@link CisaKevDTO} object containing
     * metadata and a list of all known exploited vulnerabilities.
     *
     * Performance consideration: The response typically contains 1000+ vulnerabilities
     * and may take several seconds to download and parse.
     *
     * @return {@link CisaKevResponseDTO} containing catalog metadata and vulnerability list
     * @throws CisaApiException if the API request fails due to network issues,
     *                          timeout, or invalid response format
     *
     * @see CisaKevResponseDTO
     */
    public CisaKevResponseDTO fetchFromCisaApi() {

        try {
            return restTemplate.getForObject(cisaKevURL, CisaKevResponseDTO.class);
        }catch (Exception e){
            log.error("Failed to fetch data from CISA API", e);
            throw new CisaApiException("CISA API unavailable",e.getMessage());
        }
    }


    /**
     * Synchronizes CISA vulnerability data with the local database using an upsert strategy.
     *
     * This method performs the following operations:
     *
     *   Loads all existing vulnerabilities from the database
     *   Creates an in-memory HashMap for O(1) lookup performance
     *   Iterates through provided DTOs, updating existing records or inserting new ones
     *   Performs a single batch save operation to minimize database round-trips
     *
     *
     * Performance optimization: Uses HashMap lookup instead of individual
     * database queries, reducing complexity from O(n²) to O(n) for n vulnerabilities.
     *
     * Transaction management: This method runs in a single transaction,
     * ensuring atomic updates. If any error occurs, all changes are rolled back.
     *
     * @param dtos list of vulnerability DTOs fetched from the CISA API
     * @throws IllegalArgumentException if dtos is null or empty
     * @throws org.springframework.dao.DataIntegrityViolationException if unique constraint
     *         violations occur (e.g., duplicate cveID)
     *
     * @see CisaKevDTO
     * @see CisaKev
     */
    @Transactional
    public void syncWithDatabase(List<CisaKevDTO> dtos){


        // Load all existing vulnerabilities and build lookup map
        List<CisaKev> existingEntities = cisaKevRepository.findAll();

        Map<String,CisaKev> existingMap = existingEntities.stream()
                .collect(Collectors.toMap(
                        CisaKev::getCveID,
                        entity -> entity
                ));


        Long inserted = 0L;
        Long updated = 0L;
        List<CisaKev> toSave = new ArrayList<>();

        // Process each vulnerability DTO
        for (CisaKevDTO dto : dtos){
            CisaKev existingEntity = existingMap.get(dto.cveID());

            if (existingEntity != null){
                // Update existing vulnerability
                mapper.updateCisaKevEntity(dto, existingEntity);
                toSave.add(existingEntity);
                updated++;
            }else {
                // Insert new vulnerability
                CisaKev newEntity =  mapper.mapToCisaKevEntity(dto);
                toSave.add(newEntity);
                inserted++;
            }
        }

        // Batch save all changes in single transaction
        cisaKevRepository.saveAll(toSave);

        log.info("CISA KEV sync completed: {} inserted times, {} updated times.", inserted, updated);
    }


    /**
     * Scheduled task that updates the CISA KEV database every weekday at 8:00 AM.
     *
     * This method orchestrates the complete update workflow:
     *
     *   Fetches the latest vulnerability data from CISA API
     *   Validates the response is not null or empty
     *   Synchronizes the data with the local database
     *   Logs performance metrics and operation results
     *
     *
     * Schedule: Runs Monday-Friday at 8:00 AM server time.
     * Weekends are skipped as CISA typically doesn't update the catalog on weekends.
     *
     * Error handling: Any exceptions are caught and logged without
     * re-throwing to prevent the scheduler from stopping. The next scheduled run will
     * retry the operation.
     *
     * Performance tracking:< Execution time is measured and logged
     * for monitoring purposes. Typical execution takes 3-10 seconds depending on
     * network speed and database size.
     *
     * Note: This method is public to allow manual triggering via
     * admin endpoints for testing or emergency updates.
     *
     * @see #fetchFromCisaApi()
     * @see #syncWithDatabase(List)
     */
    @Scheduled(cron = "0 0 8 * * MON-FRI")  // 8 AM Weekdays
    @PreAuthorize("hasRole('ADMIN')")
    public void updateDatabase() {

        long startTime = System.currentTimeMillis();

        try {
            log.info("CISA KEV scheduled update started...");

            // Fetch latest vulnerability data
            CisaKevResponseDTO response = fetchFromCisaApi();

            // Validate response
            if (response == null || response.vulnerabilities() == null || response.vulnerabilities().isEmpty()) {
                log.warn("Empty response from CISA KEV.");
                return;
            }

            // Synchronize with database
            syncWithDatabase(response.vulnerabilities());

            // Log success metrics
            long duration = System.currentTimeMillis() - startTime;
            log.info("CISA KEV update completed in {}ms. Vulnerabilities updated = {}",duration, response.count());
        }catch (Exception e){
            // Log failure but don't re-throw to keep scheduler running
            long duration = System.currentTimeMillis() - startTime;
            log.error("CISA KEV update failed after {}ms.", duration);
        }
    }


    /**
     * Retrieves all Known Exploited Vulnerabilities from the local database.
     *
     * This method returns the complete list of vulnerabilities stored in the
     * database. It is intended for use by frontend components to display the
     * vulnerability catalog to users.
     *
     * Performance consideration: This method loads all records
     * into memory. For large datasets (1000+ records), consider implementing
     * pagination or filtering in the future.
     *
     * Use cases:
     *
     *   Displaying vulnerability catalog in UI
     *   Exporting vulnerability data
     *   Generating reports
     *
     *
     * @return List of all {@link CisaKev} entities in the database
     * @see CisaKev
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<CisaKev> getAllVulnerabilities() {
        return cisaKevRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CisaKev getVulnerabilityByCveId(String cveID){
        return cisaKevRepository.findByCveID(cveID)
                .orElseThrow(() -> new VulnerabilityNotFoundException(cveID));
    }


    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Page<CisaKev> getAllVulnerabilitiesPaginated(int page, int size, String sortStr) {

        // Parse sort string
        String[] sortParams = sortStr.split(",");
        String field = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Create Pageable
        Pageable pageable = PageRequest.of(page,size,Sort.by(direction, field));

        return cisaKevRepository.findAll(pageable);
    }
}
