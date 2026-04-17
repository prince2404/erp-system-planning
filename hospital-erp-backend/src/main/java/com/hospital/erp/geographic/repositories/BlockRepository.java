package com.hospital.erp.geographic.repositories;

import com.hospital.erp.geographic.entities.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByDistrict_Id(Long districtId);
    boolean existsByDistrict_Id(Long districtId);
}
