package com.mini.cti.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;



/**
 * Entity representing a Known Exploited Vulnerability from CISA's catalog.
 *
 * <p>This entity stores information about vulnerabilities that are known to be
 * actively exploited in the wild. The data is sourced from CISA's public KEV catalog
 * and updated regularly via scheduled tasks.</p>
 *
 * <p>Each vulnerability is uniquely identified by its CVE ID (e.g., CVE-2021-44228).
 * The cveID field has a unique constraint to prevent duplicate entries.</p>
 *
 * @author Mini-CTI Team
 *
 * @see <a href="https://www.cve.org/">CVE Program</a>
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "cisa_kev")
public class CisaKev extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Column(unique = true, nullable = false, updatable = false)
    private String cveID;

    @Column(length = 1000)
    private String vendorProject;

    @Column(length = 1000)
    private String product;

    @Column(length = 1000)
    private String vulnerabilityName;

    private String dateAdded;

    @Column(length = 1000)
    private String shortDescription;

    @Column(length = 1000)
    private String requiredAction;

    private String dueDate;

    @Column(length = 1000)
    private String knownRansomwareCampaignUse;

    @Column(length = 1000)
    private String notes;


    private List<String> cwes;
}
