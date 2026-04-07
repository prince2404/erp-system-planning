package com.hospital.erp.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.AdmissionStatus;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ipd_admissions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class IpdAdmission extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @Column(nullable = false)
    private LocalDateTime admissionDate;

    private LocalDateTime dischargeDate;

    @Lob
    private String diagnosis;

    @Lob
    private String treatmentNotes;

    private BigDecimal dailyCharge = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AdmissionStatus status = AdmissionStatus.ADMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discharged_by")
    private User dischargedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
