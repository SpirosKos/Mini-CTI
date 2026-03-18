package com.mini.cti.service;

import com.mini.cti.core.exceptions.CisaApiException;
import com.mini.cti.dto.CisaKevDTO;
import com.mini.cti.dto.CisaKevResponseDTO;
import com.mini.cti.mapper.Mapper;
import com.mini.cti.model.CisaKev;
import com.mini.cti.repository.CisaKevRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CisaKevService {


    @Value("${app.cisakev.base_url}")
    private String cisaKevURL;


//    @Qualifier("cisaRestTemplate")
    private final RestTemplate restTemplate;

    private final Mapper mapper;
    private final CisaKevRepository cisaKevRepository;


//    @PreAuthorize("hasRole('ADMIN)")
    public CisaKevResponseDTO fetchFromCisaApi() {

        try {
            return restTemplate.getForObject(cisaKevURL, CisaKevResponseDTO.class);
        }catch (Exception e){
            log.error("Failed to fetch data from CISA API", e);
            throw new CisaApiException("CISA API unavailable",e.getMessage());
        }
    }

    @Transactional
    public void syncWithDatabase(List<CisaKevDTO> dtos){
        List<CisaKev> existingEntities = cisaKevRepository.findAll();

        Map<String,CisaKev> existingMap = existingEntities.stream()
                .collect(Collectors.toMap(
                        CisaKev::getCveID,
                        entity -> entity
                ));


        Long inserted = 0L;
        Long updated = 0L;
        List<CisaKev> toSave = new ArrayList<>();

        for (CisaKevDTO dto : dtos){
            CisaKev existingEntity = existingMap.get(dto.cveID());

            if (existingEntity != null){
                mapper.updateCisaKevEntity(dto, existingEntity);
                toSave.add(existingEntity);
                updated++;
            }else {
                CisaKev newEntity =  mapper.mapToCisaKevEntity(dto);
                toSave.add(newEntity);
                inserted++;
            }
        }

        // Batch save in the end
        cisaKevRepository.saveAll(toSave);

        log.info("CISA KEV sync completed: {} inserted times, {} updated times.", inserted, updated);
    }

    @Scheduled(cron = "0 0 8 * * MON-FRI")  // 8 AM Weekdays
    @PreAuthorize("hasRole('ADMIN')")
    public void updateDatabase() {

        long startTime = System.currentTimeMillis();

        try {
            log.info("CISA KEV update started...");

            CisaKevResponseDTO response = fetchFromCisaApi();

            if (response == null || response.vulnerabilities() == null || response.vulnerabilities().isEmpty()) {
                log.warn("Empty response from CISA KEV.");
                return;
            }

            syncWithDatabase(response.vulnerabilities());
            long duration = System.currentTimeMillis() - startTime;
            log.info("CISA KEV update completed in {}ms. Vulnerabilities updated = {}",duration, response.count());
        }catch (Exception e){
            long duration = System.currentTimeMillis() - startTime;
            log.error("CISA KEV update failed after {}ms.", duration);
        }
    }

    public List<CisaKev> getAllVulnerabilities() {
        return cisaKevRepository.findAll();
    }
}
