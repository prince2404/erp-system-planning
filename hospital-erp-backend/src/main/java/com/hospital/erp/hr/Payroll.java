package com.hospital.erp.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.PayrollStatus;
import com.hospital.erp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payrolls", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "month", "year"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payroll extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    private BigDecimal baseSalary = BigDecimal.ZERO;
    private Integer workingDays;
    private Integer presentDays;
    private Integer absentDays;
    private Integer leaveDays;
    private BigDecimal grossSalary = BigDecimal.ZERO;
    private BigDecimal deductions = BigDecimal.ZERO;
    private BigDecimal bonus = BigDecimal.ZERO;
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PayrollStatus status = PayrollStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    private LocalDateTime paidAt;
}
