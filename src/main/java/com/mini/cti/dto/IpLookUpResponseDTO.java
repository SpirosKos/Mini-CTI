package com.mini.cti.dto;


import java.time.Instant;

public record IpLookUpResponseDTO(
        String ipAddress,
        String country,
        String asOwner,
        String reputation,
        Integer malicious,
        Integer suspicious,
        Integer harmless,
        Integer undetected,
        Instant lastAnalysisDate
) {
}
