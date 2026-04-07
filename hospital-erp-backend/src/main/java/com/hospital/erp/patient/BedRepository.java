package com.hospital.erp.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByCenter_IdAndOccupiedFalse(Long centerId);
    List<Bed> findByCenter_IdAndWardAndOccupiedFalse(Long centerId, String ward);
}
