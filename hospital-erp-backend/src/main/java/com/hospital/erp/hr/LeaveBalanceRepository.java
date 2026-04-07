package com.hospital.erp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUser_IdAndYear(Long userId, Integer year);
}
