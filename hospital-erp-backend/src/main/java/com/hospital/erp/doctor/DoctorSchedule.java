package com.hospital.erp.doctor;

import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.DayOfWeekCode;
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

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "doctor_schedules", uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"}))
public class DoctorSchedule extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DayOfWeekCode dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer slotDurationMins = 15;

    private Integer maxPatients;

    @Column(nullable = false)
    private Boolean active = true;
}
