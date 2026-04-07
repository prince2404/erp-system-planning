package com.hospital.erp.pharmacy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.patient.OpdVisit;
import com.hospital.erp.patient.Patient;
import com.hospital.erp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "drug_dispenses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DrugDispense extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opd_visit_id")
    private OpdVisit opdVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_stock_id")
    private DrugStock drugStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by")
    private User dispensedBy;

    @Column(nullable = false)
    private LocalDateTime dispensedAt;
}
