package com.mini.cti.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "ip_cache")
public class IpCache extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Basic info
    @Column(unique = true, nullable = false)
    private String ipAddress;       // 8.8.8.8

    private String country;         // "US"
    private String asOwner;         // "Google LLC"


    // Reputation
    private Integer reputation;     // 527 higher = better

    // Metadata
    private Instant lastUpdate;       // When WE last  refresh from VirusTotals
    private Long lastAnalysisDate;  // VirusTotals's last analyzed


    private Integer malicious;      // 0
    private Integer suspicious;     // 0
    private Integer harmless;       // 58
    private Integer undetected;     // 36

}
