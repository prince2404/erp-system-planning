package com.hospital.erp.doctor;

import com.hospital.erp.common.enums.DayOfWeekCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctor_IdOrderByDayOfWeek(Long doctorId);
    Optional<DoctorSchedule> findByDoctor_IdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeekCode dayOfWeek);
    void deleteByDoctor_Id(Long doctorId);
}
