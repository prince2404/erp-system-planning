package com.hospital.erp.geographic.repositories;

import com.hospital.erp.geographic.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByState_Id(Long stateId);
    boolean existsByState_Id(Long stateId);
}
