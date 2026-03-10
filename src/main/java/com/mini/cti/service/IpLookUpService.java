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

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;


// TODO CONTINUE WITH EXCEPTION HANDLING TO
//  FIND OUT WHATS THE MOST EFFICIENT WITH
//  try catch OR NOT and finish with this Service

@RequiredArgsConstructor
@Service
@Slf4j
public class IpLookUpService {

    private final IpCacheRepository ipCacheRepository;
    private final UserLookUpRepository userLookUpRepository;
    private final Mapper mapper;
    private final VirusTotalService virusTotalService;
    private static final int CACHE_HOURS= 24;

    public IpLookUpResponseDTO lookUpIp(String ipAddress, User user)
            throws InvalidIpAddressException, VirusTotalApiException {

        try {

            // IpAddress validation
            ipValidation(ipAddress);

            Optional<IpCache> cachedResult = ipCacheRepository.findByIpAddress(ipAddress);

            if (cachedResult.isPresent()) {
                IpCache cache = cachedResult.get();

                if (!isCacheStale(cache)){

                    // Hit for fresh cached data return
                    log.info("User = {} searched IP = {} at {}", user.getEmail(),ipAddress,Instant.now());

                    // Record user history
                    recordUserSearch(user, cache);

                    return mapper.mapToIpLookUpResponseDTO(cache);
                }else {

                    ResponseEntity<VirusTotalResponseDTO> response = virusTotalService.getIpInfo(ipAddress);
                    VirusTotalResponseDTO vtResponse = response.getBody();

                    if (vtResponse == null) {
                        throw new VirusTotalApiException("Failed to fetch data. Try again.");
                    }

                    // Update existing ip cache
                    updateIpCacheFields(cache,vtResponse);

                    // Save updated ip cache
                    ipCacheRepository.save(cache);

                    // Record user search
                    recordUserSearch(user, cache);

                    return mapper.mapToIpLookUpResponseDTO(cache);
                }

            }else {

                ResponseEntity<VirusTotalResponseDTO> response = virusTotalService.getIpInfo(ipAddress);
                VirusTotalResponseDTO vtResponse = response.getBody();
                if (vtResponse == null) {
                    throw new VirusTotalApiException ("Failed to fetch data. Try again.");
                }

                IpCache newCache = mapper.mapToIpCacheEntity(vtResponse);

                ipCacheRepository.save(newCache);

                recordUserSearch(user,newCache);
                // Ip not in cache searched from VirusTotal
                log.info("User = {} searched for new  IP = {} at {}", user.getEmail(),ipAddress,Instant.now());

                return mapper.mapToIpLookUpResponseDTO(newCache);
            }

        }catch (InvalidIpAddressException e) {
            log.error("IpAddress = {} is not valid.", ipAddress);
            throw e;
        }catch (VirusTotalApiException e) {
            log.error("API call failed.", e);
            throw e;
        }
    }


    private boolean isCacheStale(IpCache cache) {
        Instant now = Instant.now();
        Instant cacheTime = cache.getLastUpdate();

        Duration age = Duration.between(cacheTime, now);

        return age.toHours() >= CACHE_HOURS;
    }

    private void recordUserSearch(User user, IpCache ipCache) {
        UserLookUp history = new UserLookUp();
        history.setUser(user);
        history.setIpCache(ipCache);
        history.setSearchedAt(Instant.now());
        userLookUpRepository.save(history);
    }

    private void updateIpCacheFields(IpCache existingCache, VirusTotalResponseDTO virusTotalResponseDTO) {
        existingCache.setIpAddress(virusTotalResponseDTO.data().ipAddress());
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

}
