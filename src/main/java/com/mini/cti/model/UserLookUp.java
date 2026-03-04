package com.mini.cti.model;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_lookups")
public class UserLookUp extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_id", nullable = false)
    private IpCache ipCache;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

}
