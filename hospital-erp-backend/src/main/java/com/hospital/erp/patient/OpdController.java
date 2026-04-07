package com.hospital.erp.patient;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.patient.dto.OpdStatusRequest;
import com.hospital.erp.patient.dto.OpdVisitRequest;
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
public class OpdController {
    private final PatientService patientService;

    @PostMapping("/opd/visit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','RECEPTIONIST')")
    public ApiResponse<OpdVisit> createVisit(@Valid @RequestBody OpdVisitRequest request) {
        return ApiResponse.ok(patientService.createOpdVisit(request), "OPD visit created");
    }

    @GetMapping("/opd/queue")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','PHARMACIST','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<OpdVisit>> queue(@RequestParam Long centerId, @RequestParam(required = false) LocalDate date) {
        return ApiResponse.ok(patientService.queue(centerId, date), "OPD queue loaded");
    }

    @PutMapping("/opd/visit/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR')")
    public ApiResponse<OpdVisit> updateStatus(@PathVariable Long id, @Valid @RequestBody OpdStatusRequest request) {
        return ApiResponse.ok(patientService.updateOpdStatus(id, request), "OPD status updated");
    }

    @GetMapping("/opd/visits/{patientId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','PHARMACIST','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<OpdVisit>> history(@PathVariable Long patientId) {
        return ApiResponse.ok(patientService.opdHistory(patientId), "OPD history loaded");
    }
}
