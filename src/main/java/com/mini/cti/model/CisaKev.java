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

    @Column(unique = true)
    private String vendorProject;

    @Column(unique = true)
    private String product;

    @Column(unique = true)
    private String vulnerabilityName;

    @Column(unique = true)
    private String dataAdded;

    @Column(unique = true)
    private String shortDescription;

    @Column(unique = true)
    private String requiredAction;
}
