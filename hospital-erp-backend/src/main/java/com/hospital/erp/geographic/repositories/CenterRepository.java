package com.hospital.erp.geographic.repositories;

import com.hospital.erp.geographic.entities.Center;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long> {
    List<Center> findByState_IdAndActiveTrue(Long stateId);
    List<Center> findByDistrict_IdAndActiveTrue(Long districtId);
    List<Center> findByBlock_IdAndActiveTrue(Long blockId);
    List<Center> findByActiveTrue();
    boolean existsByCode(String code);
    long countByBlock_Id(Long blockId);
}
