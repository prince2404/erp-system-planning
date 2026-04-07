package com.hospital.erp.hr;

import com.hospital.erp.common.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUser_IdAndDate(Long userId, LocalDate date);
    List<Attendance> findByUser_IdAndDateBetween(Long userId, LocalDate from, LocalDate to);
    long countByUser_IdAndDateBetweenAndStatus(Long userId, LocalDate from, LocalDate to, AttendanceStatus status);
}
