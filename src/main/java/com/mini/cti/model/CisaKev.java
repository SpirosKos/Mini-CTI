package com.mini.cti.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


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

    private String vendorProject;

    private String product;

    private String vulnerabilityName;

    private String dataAdded;

    private String shortDescription;

    private String requiredAction;

    private String dueDate;

    private String knownRansomwareCampaignUse;

    private String notes;

    private String cwes;
}
