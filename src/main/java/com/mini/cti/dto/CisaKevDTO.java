package com.mini.cti.dto;


import java.util.List;


/**
 * Data Transfer Object for CISA Known Exploited Vulnerability.
 *
 * <p>This record represents a single vulnerability entry from the CISA KEV JSON feed.
 * Field names match the JSON structure exactly for automatic deserialization.</p>
 *
 * <p>Example JSON structure:</p>
 * <pre>
 * {
 *   "cveID": "CVE-2021-44228",
 *   "vendorProject": "Apache",
 *   "product": "Log4j",
 *   "vulnerabilityName": "Log4Shell",
 *   ...
 * }
 * </pre>
 *
 * @param cveID Unique CVE identifier (e.g., CVE-2021-44228)
 * @param vendorProject Vendor or project name
 * @param product Affected product name
 * @param vulnerabilityName Common name of the vulnerability
 * @param dateAdded Date when CISA added this to the KEV catalog
 * @param shortDescription Brief description of the vulnerability
 * @param requiredAction Remediation action required by CISA
 * @param dueDate Deadline for remediation
 * @param knownRansomwareCampaignUse Whether used in ransomware campaigns
 * @param notes Additional notes
 * @param cwes Common Weakness Enumeration IDs
 *
 * @author Mini-CTI Team
 * @version 1.0
 */
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
