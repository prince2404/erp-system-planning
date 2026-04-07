package com.hospital.erp.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.AttendanceStatus;
import com.hospital.erp.geographic.entities.Center;
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

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attendance extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime checkIn;
    private LocalTime checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private User markedBy;

    @Column(length = 255)
    private String remarks;
}
