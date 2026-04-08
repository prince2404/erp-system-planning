package com.hospital.erp.patient;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.common.PageResponse;
import com.hospital.erp.patient.dto.PatientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/patients/register")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Patient> register(@Valid @RequestBody PatientRequest request) {
        return ApiResponse.ok(patientService.register(request), "Patient registered");
    }

    @GetMapping("/patients")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<Patient>> patients(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(patientService.search(q, PageRequest.of(page, size)), "Patients loaded");
    }

    @GetMapping("/patients/{uhid}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Patient> profile(@PathVariable String uhid) {
        return ApiResponse.ok(patientService.profile(uhid), "Patient profile loaded");
    }

    @PutMapping("/patients/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Patient> update(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return ApiResponse.ok(patientService.update(id, request), "Patient updated");
    }
}
