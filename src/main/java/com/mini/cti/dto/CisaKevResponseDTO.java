package com.mini.cti.dto;

import java.util.List;

public record CisaKevResponseDTO(
        String title,
        String catalogVersion,
        String dateReleased,
        Long count,
        List<CisaKevDTO> vulnerabilities
) {
}
