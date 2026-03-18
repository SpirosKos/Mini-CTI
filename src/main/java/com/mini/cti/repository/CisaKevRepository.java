package com.mini.cti.repository;

import com.mini.cti.model.CisaKev;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CisaKevRepository extends JpaRepository<CisaKev,Long> {

    Optional<CisaKev> findByCveId(String cveID);
}
