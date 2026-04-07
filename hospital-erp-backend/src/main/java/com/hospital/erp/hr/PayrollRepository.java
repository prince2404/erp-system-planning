package com.hospital.erp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Optional<Payroll> findByUser_IdAndMonthAndYear(Long userId, Integer month, Integer year);
}
