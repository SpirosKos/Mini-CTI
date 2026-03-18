package com.mini.cti.dto;


import java.util.List;

public record CisaKevDTO(
        String cveID,
        String vendorProject,
        String product,
        String vulnerabilityName,
        String dateAdded,
        String shortDescription,
        String requiredAction,
        String dueDate,
        String knownRansomwareCampaignUse,
        String notes,
        List<String> cwes
) {
}
