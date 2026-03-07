package com.mini.cti.dto;


import java.time.Instant;

public record IpLookUpResponseDTO(
        String ipAddress,
        String country,
        String asOwner,
        Integer reputation,
        Integer malicious,
        Integer suspicious,
        Integer harmless,
        Integer undetected,
        Instant lastAnalysisDate,
        Instant lastUpdate
) {
}
