package com.hospital.erp.reports;

import com.hospital.erp.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/reports/dashboard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER','HR_MANAGER','DOCTOR','PHARMACIST','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<Map<String, Object>> dashboard(
            @RequestParam(required = false) Long centerId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ApiResponse.ok(reportService.dashboard(centerId, from, to), "Dashboard loaded");
    }

    @GetMapping("/reports/revenue")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<List<Map<String, Object>>> revenue(@RequestParam(required = false) Long centerId, @RequestParam(defaultValue = "DAY") String groupBy) {
        return ApiResponse.ok(reportService.revenue(centerId, groupBy), "Revenue report loaded");
    }

    @GetMapping("/reports/patients")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<Map<String, Object>> patients(
            @RequestParam(required = false) Long centerId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ApiResponse.ok(reportService.patients(centerId, from, to), "Patient report loaded");
    }

    @GetMapping("/reports/inventory")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST')")
    public ApiResponse<Map<String, Object>> inventory(@RequestParam Long centerId) {
        return ApiResponse.ok(reportService.inventory(centerId), "Inventory report loaded");
    }

    @GetMapping("/reports/hr")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<Map<String, Object>> hr(@RequestParam Long centerId, @RequestParam int month, @RequestParam int year) {
        return ApiResponse.ok(reportService.hr(centerId, month, year), "HR report loaded");
    }

    @GetMapping("/reports/wallet-movement")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER')")
    public ApiResponse<List<Map<String, Object>>> walletMovement() {
        return ApiResponse.ok(reportService.walletMovement(), "Wallet movement loaded");
    }
}
