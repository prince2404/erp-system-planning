package com.hospital.erp.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUhid(String uhid);
    Page<Patient> findByNameContainingIgnoreCaseOrPhoneContainingOrUhidContainingIgnoreCase(String name, String phone, String uhid, Pageable pageable);
    long countByUhidStartingWith(String prefix);
}
