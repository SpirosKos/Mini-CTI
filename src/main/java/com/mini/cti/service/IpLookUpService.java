package com.mini.cti.service;

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

    private IpCacheRepository ipCacheRepository;
    private UserLookUpRepository userLookUpRepository;
    private Mapper mapper;
    private VirusTotalService virusTotalService;




}
