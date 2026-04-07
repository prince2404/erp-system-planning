package com.hospital.erp.pharmacy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugDispenseRepository extends JpaRepository<DrugDispense, Long> {
    List<DrugDispense> findByPatient_IdOrderByDispensedAtDesc(Long patientId);
}
