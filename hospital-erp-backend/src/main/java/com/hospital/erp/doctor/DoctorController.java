package com.hospital.erp.doctor;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.doctor.dto.DoctorProfileRequest;
import com.hospital.erp.doctor.dto.DoctorScheduleRequest;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping("/doctors/profile")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<DoctorProfile> createProfile(@Valid @RequestBody DoctorProfileRequest request) {
        return ApiResponse.ok(doctorService.createProfile(request), "Doctor profile created");
    }

    @GetMapping("/doctors")
    public ApiResponse<List<DoctorProfile>> doctors(
            @RequestParam(required = false) Long centerId,
            @RequestParam(required = false) String specialization
    ) {
        return ApiResponse.ok(doctorService.doctors(centerId, specialization), "Doctors loaded");
    }

    @GetMapping("/doctors/{id}/schedule")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<DoctorSchedule>> schedule(@PathVariable Long id) {
        return ApiResponse.ok(doctorService.schedule(id), "Doctor schedule loaded");
    }

    @PutMapping("/doctors/{id}/schedule")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR')")
    public ApiResponse<List<DoctorSchedule>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody List<DoctorScheduleRequest> requests
    ) {
        return ApiResponse.ok(doctorService.updateSchedule(id, requests), "Doctor schedule updated");
    }
}
