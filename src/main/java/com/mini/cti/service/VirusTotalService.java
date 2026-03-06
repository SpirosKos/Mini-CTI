package com.mini.cti.service;


import com.mini.cti.dto.VirusTotalResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirusTotalService {

    @Value("${app.virustotal.api.key}")
    private String apiKey;

    @Value(("${app.virustotal.base_url}"))
    private String baseUrl;


    // TODO continue the logic with restTemplate and the return
    private final RestTemplate restTemplate;

    public ResponseEntity<VirusTotalResponseDTO> getIpInfo(String ip) {

        String url = baseUrl + ip;

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apikey",apiKey);

        // Create an HttpEntity with headers
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET,entity, VirusTotalResponseDTO.class);
    }
}
