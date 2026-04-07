package com.hospital.erp.hr;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.hr.dto.AttendanceRequest;
import com.hospital.erp.hr.dto.LeaveActionRequest;
import com.hospital.erp.hr.dto.LeaveApplyRequest;
import com.hospital.erp.hr.dto.PayrollGenerateRequest;
import com.hospital.erp.hr.dto.StaffProfileRequest;
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
public class HrController {
    private final HrService hrService;

    @PostMapping("/hr/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<StaffProfile> createStaff(@Valid @RequestBody StaffProfileRequest request) {
        return ApiResponse.ok(hrService.createStaff(request), "Staff profile created");
    }

    @GetMapping("/hr/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<List<StaffProfile>> staff(@RequestParam Long centerId, @RequestParam(required = false) String dept) {
        return ApiResponse.ok(hrService.staff(centerId, dept), "Staff loaded");
    }

    @PutMapping("/hr/staff/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<StaffProfile> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffProfileRequest request) {
        return ApiResponse.ok(hrService.updateStaff(id, request), "Staff profile updated");
    }

    @PostMapping("/hr/attendance/mark")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<List<Attendance>> markAttendance(@Valid @RequestBody List<AttendanceRequest> requests) {
        return ApiResponse.ok(hrService.markAttendance(requests), "Attendance marked");
    }

    @GetMapping("/hr/attendance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<List<Attendance>> attendance(@RequestParam Long userId, @RequestParam int month, @RequestParam int year) {
        return ApiResponse.ok(hrService.attendance(userId, month, year), "Attendance loaded");
    }

    @PutMapping("/hr/attendance/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<Attendance> correctAttendance(@PathVariable Long id, @Valid @RequestBody AttendanceRequest request) {
        return ApiResponse.ok(hrService.correctAttendance(id, request), "Attendance corrected");
    }

    @PostMapping("/hr/leave/apply")
    public ApiResponse<LeaveRequest> applyLeave(@Valid @RequestBody LeaveApplyRequest request) {
        return ApiResponse.ok(hrService.applyLeave(request), "Leave request submitted");
    }

    @PutMapping("/hr/leave/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<LeaveRequest> actionLeave(@PathVariable Long id, @Valid @RequestBody LeaveActionRequest request) {
        return ApiResponse.ok(hrService.actionLeave(id, request), "Leave request updated");
    }

    @GetMapping("/hr/leave/pending")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<List<LeaveRequest>> pendingLeaves(@RequestParam Long centerId) {
        return ApiResponse.ok(hrService.pendingLeaves(centerId), "Pending leaves loaded");
    }

    @GetMapping("/hr/leave/balance/{userId}")
    public ApiResponse<LeaveBalance> leaveBalance(@PathVariable Long userId) {
        return ApiResponse.ok(hrService.leaveBalance(userId), "Leave balance loaded");
    }

    @PostMapping("/hr/payroll/generate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<List<Payroll>> generatePayroll(@Valid @RequestBody PayrollGenerateRequest request) {
        return ApiResponse.ok(hrService.generatePayroll(request), "Payroll generated");
    }

    @GetMapping("/hr/payroll/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public ApiResponse<Payroll> payroll(@PathVariable Long userId, @RequestParam int month, @RequestParam int year) {
        return ApiResponse.ok(hrService.payroll(userId, month, year), "Payroll loaded");
    }

    @PutMapping("/hr/payroll/{id}/pay")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<Payroll> markPayrollPaid(@PathVariable Long id) {
        return ApiResponse.ok(hrService.markPayrollPaid(id), "Payroll marked paid");
    }
}
