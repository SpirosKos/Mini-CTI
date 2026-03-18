package com.mini.cti.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


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
