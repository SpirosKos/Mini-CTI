package com.mini.cti.dto;


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
        String cwes
) {
}
