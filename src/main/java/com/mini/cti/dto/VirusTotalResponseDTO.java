package com.mini.cti.dto;

public record VirusTotalResponseDTO(
        DataDTO data
) {

    public record DataDTO(
            String id,
            AttributesDTO attributes
    ){}

    public record AttributesDTO(
            String country,
            String as_owner,
            Integer reputation,
            Long last_analysis_date,
            LastAnalysisDTO last_analysis_stats
    ){}

    public record LastAnalysisDTO(
            Integer malicious,
            Integer suspicious,
            Integer harmless,
            Integer undetected
    ){}
}
