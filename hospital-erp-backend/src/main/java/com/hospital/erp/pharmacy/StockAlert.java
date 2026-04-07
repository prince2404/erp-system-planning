package com.hospital.erp.pharmacy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.StockAlertType;
import com.hospital.erp.geographic.entities.Center;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stock_alerts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StockAlert extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockAlertType alertType;

    private Integer currentQuantity;
    private LocalDate expiryDate;

    @Column(nullable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "is_resolved", nullable = false)
    private Boolean resolved = false;

    private LocalDateTime resolvedAt;
}
