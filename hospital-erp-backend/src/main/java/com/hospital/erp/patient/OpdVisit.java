package com.hospital.erp.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.VisitStatus;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "opd_visits", uniqueConstraints = @UniqueConstraint(columnNames = {"center_id", "visit_date", "token_number"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OpdVisit extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @Column(nullable = false)
    private Integer tokenNumber;

    @Column(nullable = false)
    private LocalDate visitDate;

    @Lob
    private String symptoms;

    @Lob
    private String diagnosis;

    @Lob
    private String prescriptionNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitStatus status = VisitStatus.WAITING;

    private BigDecimal fee = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
