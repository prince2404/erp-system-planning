package com.hospital.erp.user;

import com.hospital.erp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 20)
    private String gender;

    private LocalDate dateOfBirth;

    @Column(length = 15)
    private String alternatePhone;

    @Column(length = 120)
    private String emergencyContactName;

    @Column(length = 15)
    private String emergencyContactPhone;

    @Lob
    private String address;

    @Column(length = 120)
    private String villageOrLocality;

    @Column(length = 10)
    private String pincode;

    @Column(length = 120)
    private String bankAccountName;

    @Column(length = 120)
    private String bankName;

    @Column(length = 40)
    private String bankAccountNumber;

    @Column(length = 20)
    private String ifscCode;

    @Column(length = 50)
    private String upiId;

    @Column(length = 40)
    private String idProofType;

    @Column(length = 80)
    private String idProofNumber;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
