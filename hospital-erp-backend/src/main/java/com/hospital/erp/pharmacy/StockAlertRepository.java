package com.hospital.erp.pharmacy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByCenter_IdAndResolvedFalse(Long centerId);
}
