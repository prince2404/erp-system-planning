package com.hospital.erp.pharmacy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DrugStockRepository extends JpaRepository<DrugStock, Long> {
    List<DrugStock> findByCenter_Id(Long centerId);
    List<DrugStock> findByDrug_IdAndCenter_IdAndQuantityGreaterThanOrderByExpiryDateAsc(Long drugId, Long centerId, Integer quantity);
    List<DrugStock> findByCenter_IdAndExpiryDateBefore(Long centerId, LocalDate expiryDate);
}
