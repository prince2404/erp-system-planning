package com.hospital.erp.patient;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.patient.dto.BedRequest;
import com.hospital.erp.patient.dto.DischargeRequest;
import com.hospital.erp.patient.dto.IpdAdmissionRequest;
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
public class IpdController {
    private final PatientService patientService;

    @PostMapping("/beds")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Bed> createBed(@Valid @RequestBody BedRequest request) {
        return ApiResponse.ok(patientService.createBed(request), "Bed created");
    }

    @GetMapping("/beds/available")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Bed>> availableBeds(@RequestParam Long centerId, @RequestParam(required = false) String ward) {
        return ApiResponse.ok(patientService.availableBeds(centerId, ward), "Available beds loaded");
    }

    @PostMapping("/ipd/admit")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<IpdAdmission> admit(@Valid @RequestBody IpdAdmissionRequest request) {
        return ApiResponse.ok(patientService.admit(request), "Patient admitted");
    }

    @GetMapping("/ipd/active")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<IpdAdmission>> active(@RequestParam Long centerId) {
        return ApiResponse.ok(patientService.activeAdmissions(centerId), "Active admissions loaded");
    }

    @PutMapping("/ipd/discharge/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<IpdAdmission> discharge(@PathVariable Long id, @RequestBody(required = false) DischargeRequest request) {
        return ApiResponse.ok(patientService.discharge(id, request), "Patient discharged and invoice draft created");
    }
}
