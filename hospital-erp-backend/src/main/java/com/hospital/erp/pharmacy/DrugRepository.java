package com.hospital.erp.pharmacy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    List<Drug> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    List<Drug> findByCategoryAndActiveTrue(String category);
    List<Drug> findByActiveTrue();
}
