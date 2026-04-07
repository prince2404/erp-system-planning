package com.hospital.erp.appointment;

import com.hospital.erp.appointment.dto.AppointmentRequest;
import com.hospital.erp.appointment.dto.AppointmentStatusRequest;
import com.hospital.erp.appointment.dto.SlotResponse;
import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.doctor.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppointmentController {
    private final DoctorService doctorService;

    @GetMapping("/appointments/slots")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<SlotResponse>> slots(@RequestParam Long doctorId, @RequestParam LocalDate date) {
        return ApiResponse.ok(doctorService.availableSlots(doctorId, date), "Available slots loaded");
    }

    @PostMapping("/appointments/book")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','RECEPTIONIST')")
    public ApiResponse<Appointment> book(@Valid @RequestBody AppointmentRequest request) {
        return ApiResponse.ok(doctorService.book(request), "Appointment booked");
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<Appointment>> appointments(@RequestParam Long doctorId, @RequestParam(required = false) LocalDate date) {
        return ApiResponse.ok(doctorService.appointments(doctorId, date), "Appointments loaded");
    }

    @PutMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR')")
    public ApiResponse<Appointment> status(@PathVariable Long id, @Valid @RequestBody AppointmentStatusRequest request) {
        return ApiResponse.ok(doctorService.updateAppointmentStatus(id, request), "Appointment status updated");
    }

    @GetMapping("/appointments/patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<Appointment>> patientHistory(@PathVariable Long patientId) {
        return ApiResponse.ok(doctorService.patientHistory(patientId), "Patient appointment history loaded");
    }
}
