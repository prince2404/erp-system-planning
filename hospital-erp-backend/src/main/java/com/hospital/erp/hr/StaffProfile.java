package com.hospital.erp.hr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "staff_profiles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StaffProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String designation;

    @Column(nullable = false)
    private LocalDate dateOfJoining;

    private BigDecimal baseSalary = BigDecimal.ZERO;

    @JsonIgnore
    @Column(length = 20)
    private String bankAccount;

    @JsonIgnore
    @Column(length = 15)
    private String ifscCode;

    @JsonIgnore
    @Column(length = 15)
    private String panNumber;

    @JsonIgnore
    @Column(length = 12)
    private String aadharNumber;

    @Column(length = 15)
    private String emergencyContact;

    @Column(length = 100)
    private String emergencyName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
