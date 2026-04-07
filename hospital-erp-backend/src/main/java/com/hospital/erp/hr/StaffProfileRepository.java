package com.hospital.erp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    Optional<StaffProfile> findByUser_Id(Long userId);
    List<StaffProfile> findByUser_Center_Id(Long centerId);
    List<StaffProfile> findByDepartmentAndUser_Center_Id(String department, Long centerId);
}
