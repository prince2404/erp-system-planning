package com.hospital.erp.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUser_Id(Long userId);
    List<DoctorProfile> findByCenter_IdAndAvailableTrue(Long centerId);
    List<DoctorProfile> findBySpecializationContainingIgnoreCaseAndAvailableTrue(String specialization);
    List<DoctorProfile> findByAvailableTrue();
}
