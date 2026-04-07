package com.hospital.erp.appointment;

import com.hospital.erp.common.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByDoctor_IdAndAppointmentDateAndSlotTime(Long doctorId, LocalDate appointmentDate, LocalTime slotTime);
    List<Appointment> findByDoctor_IdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);
    List<Appointment> findByPatient_IdOrderByAppointmentDateDesc(Long patientId);
    long countByDoctor_IdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate appointmentDate, AppointmentStatus status);
}
