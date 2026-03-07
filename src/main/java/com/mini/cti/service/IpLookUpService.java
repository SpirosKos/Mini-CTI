package com.mini.cti.service;

import com.mini.cti.dto.IpLookUpResponseDTO;
import com.mini.cti.model.User;
import com.mini.cti.repository.IpCacheRepository;
import com.mini.cti.repository.UserLookUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class IpLookUpService {

    private final IpCacheRepository ipCacheRepository;
    private final UserLookUpRepository userLookUpRepository;
    private final Mapper mapper;
    private final VirusTotalService virusTotalService;

    public IpLookUpResponseDTO lookUpIp(String ipAddress, User user) {

        // TODO  // 1. Check cache in database
        //    // 2. Determine if cache is fresh or stale
        //    // 3. If fresh → return from cache
        //    // 4. If stale/missing → fetch from VirusTotal
        //    // 5. Save/update cache
        //    // 6. Record user search history
        //    // 7. Map and return response


        if (ipCacheRepository.existsById(ipAddress)) {

        }
    }




}
