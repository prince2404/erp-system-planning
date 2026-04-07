package com.hospital.erp.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.Gender;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "patients")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20)
    private String uhid;

    @Column(nullable = false, length = 100)
    private String name;

    private Integer age;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(length = 5)
    private String bloodGroup;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 100)
    private String email;

    @Lob
    private String address;

    @Column(length = 15)
    private String emergencyContact;

    @Column(length = 100)
    private String emergencyName;

    @Lob
    private String allergies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by")
    private User registeredBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
