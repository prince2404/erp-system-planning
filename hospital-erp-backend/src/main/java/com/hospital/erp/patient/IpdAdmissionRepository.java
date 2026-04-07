package com.hospital.erp.patient;

import com.hospital.erp.common.enums.AdmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IpdAdmissionRepository extends JpaRepository<IpdAdmission, Long> {
    List<IpdAdmission> findByCenter_IdAndStatus(Long centerId, AdmissionStatus status);
    List<IpdAdmission> findByPatient_IdOrderByAdmissionDateDesc(Long patientId);
}
