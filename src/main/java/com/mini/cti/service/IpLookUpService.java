package com.mini.cti.service;

import com.mini.cti.core.exceptions.InvalidIpAddressException;
import com.mini.cti.core.exceptions.VirusTotalApiException;
import com.mini.cti.dto.IpLookUpResponseDTO;
import com.mini.cti.dto.VirusTotalResponseDTO;
import com.mini.cti.mapper.Mapper;
import com.mini.cti.model.IpCache;
import com.mini.cti.model.User;
import com.mini.cti.model.UserLookUp;
import com.mini.cti.repository.IpCacheRepository;
import com.mini.cti.repository.UserLookUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Service for IP Lookup operations.
 * Manages IP cache, VirusTotal API integration and user search history.
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class IpLookUpService {

    private final IpCacheRepository ipCacheRepository;
    private final UserLookUpRepository userLookUpRepository;
    private final Mapper mapper;
    private final VirusTotalService virusTotalService;

    private static final int CACHE_HOURS= 24;


    /**
     * Looks up IP Address information.
     * Checks cache first(24h TTL), then fetches from VirusTotal if needed.
     * Records user search history for all lookups.
     *
     * @param ipAddress IP address to lookup
     * @param user      User performing the lookup
     * @return          IP lookup response with threat analysis data
     * @throws InvalidIpAddressException    if  IP format is valid
     * @throws VirusTotalApiException       if VirusTotal API call fails.
     */
    @Transactional(rollbackFor = {InvalidIpAddressException.class, VirusTotalApiException.class})
    public IpLookUpResponseDTO lookUpIp(String ipAddress, User user){

            // IpAddress validation
            ipValidation(ipAddress);

            Optional<IpCache> cachedResult = ipCacheRepository.findByIpAddress(ipAddress);

            if (cachedResult.isPresent()) {
                IpCache cache = cachedResult.get();
                return isCacheStale(cache)
                        ?refreshExistingCache(cache, user)
                        :useExistingCache(cache, user);
            }
            return fetchAndSaveNewIp(ipAddress, user);

    }

    //  ======== PRIVATE HELPER METHODS ======= //


    private boolean isCacheStale(IpCache cache) {
        Instant now = Instant.now();
        Instant cacheTime = cache.getLastUpdate();

        Duration age = Duration.between(cacheTime, now);

        return age.toHours() >= CACHE_HOURS;
    }

    private IpLookUpResponseDTO useExistingCache(IpCache cache, User user) {

        log.info("Cache hit for IP: {} from User: {}.", cache.getIpAddress(), user.getEmail());
        recordUserSearch(user, cache);
        return mapper.mapToIpLookUpResponseDTO(cache);
    }

    private IpLookUpResponseDTO refreshExistingCache(IpCache cache, User user) {

        log.info("Refreshing data for IP: {} .", cache.getIpAddress());
        VirusTotalResponseDTO vtResponse = fetchFromVirusTotal(cache.getIpAddress());
        updateIpCacheFields(cache, vtResponse);
        ipCacheRepository.save(cache);
        recordUserSearch(user,cache);
        return mapper.mapToIpLookUpResponseDTO(cache);
    }

    private IpLookUpResponseDTO fetchAndSaveNewIp(String ipAddress, User user) {

        log.info("Fetching new IP data from VirusTotal : {}", ipAddress);
        VirusTotalResponseDTO vtResponse = fetchFromVirusTotal(ipAddress);
        IpCache newCache = mapper.mapToIpCacheEntity(vtResponse);
        ipCacheRepository.save(newCache);
        recordUserSearch(user,newCache);
        return mapper.mapToIpLookUpResponseDTO(newCache);
    }

    private void recordUserSearch(User user, IpCache ipCache) {
        UserLookUp history = new UserLookUp();
        history.setUser(user);
        history.setIpCache(ipCache);
        history.setSearchedAt(Instant.now());
        userLookUpRepository.save(history);
    }

    private void updateIpCacheFields(IpCache existingCache, VirusTotalResponseDTO virusTotalResponseDTO) {
        existingCache.setCountry(virusTotalResponseDTO.data().attributes().country());
        existingCache.setAsOwner(virusTotalResponseDTO.data().attributes().as_owner());
        existingCache.setReputation(virusTotalResponseDTO.data().attributes().reputation());
        existingCache.setMalicious(virusTotalResponseDTO.data().attributes().last_analysis_stats().malicious());
        existingCache.setSuspicious(virusTotalResponseDTO.data().attributes().last_analysis_stats().suspicious());
        existingCache.setHarmless(virusTotalResponseDTO.data().attributes().last_analysis_stats().harmless());
        existingCache.setUndetected(virusTotalResponseDTO.data().attributes().last_analysis_stats().undetected());
        Long timestamp = virusTotalResponseDTO.data().attributes().last_analysis_date();
        existingCache.setLastAnalysisDate(Instant.ofEpochSecond(timestamp));
        existingCache.setLastUpdate(Instant.now());
    }

    private void ipValidation(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new InvalidIpAddressException(ipAddress);
        }
        InetAddressValidator addressValidator = InetAddressValidator.getInstance();
        if (!addressValidator.isValidInet4Address(ipAddress)) {
            throw new InvalidIpAddressException(ipAddress);
        }
    }

    private VirusTotalResponseDTO fetchFromVirusTotal(String ipAddress) {
        ResponseEntity<VirusTotalResponseDTO> response = virusTotalService.getIpInfo(ipAddress);
        VirusTotalResponseDTO vtResponse = response.getBody();
        if (vtResponse == null) {
            throw new VirusTotalApiException("Fetch data from VirusTotal failed.");
        }
        return vtResponse;
    }

}
