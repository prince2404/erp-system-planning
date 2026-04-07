package com.hospital.erp.geographic.repositories;

import com.hospital.erp.geographic.entities.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<StateEntity, Long> {
    Optional<StateEntity> findByCode(String code);
}
