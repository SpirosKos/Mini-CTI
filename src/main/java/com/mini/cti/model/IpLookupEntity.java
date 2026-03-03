package com.mini.cti.model;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ip_lookup")
public class IpLookupEntity extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Basic info
    private String ipAddress;       // 8.8.8.8
    private String country;         // "US"
    private String asOwner;         // "Google LLC"


    // Reputation
    private Integer reputation;     // 527 higher = better

    // Metadata
    private Instant analyzedAt;       // When checked
    private Instant lastAnalysisDate;  // VirusTotals's last check

    @ManyToOne
    private User user;

    private Integer malicious;      // 0
    private Integer suspicious;     // 0
    private Integer harmless;       // 58
    private Integer undetected;     // 36


}
