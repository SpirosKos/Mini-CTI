package com.mini.cti.repository;

import com.mini.cti.model.User;
import com.mini.cti.model.UserLookUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserLookUpRepository extends JpaRepository<UserLookUp, UUID> {

    List<UserLookUp> findByUserOrderByCreatedAtDesc(User user);

    // Delete first UserLookups then in Ipcache after 10-15 days.
    void deleteBySearchedAtBefore(Instant expiryData);

}
