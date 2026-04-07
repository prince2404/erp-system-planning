package com.hospital.erp.patient;

import com.hospital.erp.common.enums.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OpdVisitRepository extends JpaRepository<OpdVisit, Long> {
    List<OpdVisit> findByCenter_IdAndVisitDateOrderByTokenNumber(Long centerId, LocalDate visitDate);
    List<OpdVisit> findByPatient_IdOrderByVisitDateDesc(Long patientId);
    long countByCenter_IdAndVisitDateAndStatus(Long centerId, LocalDate visitDate, VisitStatus status);

    @Query("select coalesce(max(v.tokenNumber), 0) from OpdVisit v where v.center.id = :centerId and v.visitDate = :visitDate")
    Integer maxToken(Long centerId, LocalDate visitDate);
}
