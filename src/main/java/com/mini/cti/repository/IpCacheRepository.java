package com.mini.cti.repository;

import com.mini.cti.model.IpCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpCacheRepository  extends JpaRepository<IpCache, String> {

    Optional<IpCache> findByIpAddress(String ipAddress);

}
