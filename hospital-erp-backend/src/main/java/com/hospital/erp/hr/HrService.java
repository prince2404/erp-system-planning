package com.hospital.erp.hr;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.AttendanceStatus;
import com.hospital.erp.common.enums.LeaveStatus;
import com.hospital.erp.common.enums.LeaveType;
import com.hospital.erp.common.enums.PayrollStatus;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.hr.dto.AttendanceRequest;
import com.hospital.erp.hr.dto.LeaveActionRequest;
import com.hospital.erp.hr.dto.LeaveApplyRequest;
import com.hospital.erp.hr.dto.PayrollGenerateRequest;
import com.hospital.erp.hr.dto.StaffProfileRequest;
import com.hospital.erp.user.CurrentUserService;
import com.hospital.erp.user.User;
import com.hospital.erp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HrService {
    private final StaffProfileRepository staffProfileRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PayrollRepository payrollRepository;
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public StaffProfile createStaff(StaffProfileRequest request) {
        StaffProfile profile = new StaffProfile();
        applyStaff(profile, request);
        return staffProfileRepository.save(profile);
    }

    public List<StaffProfile> staff(Long centerId, String department) {
        if (department != null && !department.isBlank()) {
            return staffProfileRepository.findByDepartmentAndUser_Center_Id(department, centerId);
        }
        return staffProfileRepository.findByUser_Center_Id(centerId);
    }

    @Transactional
    public StaffProfile updateStaff(Long id, StaffProfileRequest request) {
        StaffProfile profile = staffProfileRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Staff profile not found"));
        applyStaff(profile, request);
        return staffProfileRepository.save(profile);
    }

    @Transactional
    public List<Attendance> markAttendance(List<AttendanceRequest> requests) {
        User actor = currentUserService.get();
        return requests.stream().map(request -> {
            Attendance attendance = attendanceRepository.findByUser_IdAndDate(request.userId(), request.date()).orElseGet(Attendance::new);
            attendance.setUser(findUser(request.userId()));
            attendance.setCenter(findCenter(request.centerId()));
            attendance.setDate(request.date());
            attendance.setCheckIn(request.checkIn());
            attendance.setCheckOut(request.checkOut());
            attendance.setStatus(request.status());
            attendance.setMarkedBy(actor);
            attendance.setRemarks(request.remarks());
            return attendanceRepository.save(attendance);
        }).toList();
    }

    public List<Attendance> attendance(Long userId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return attendanceRepository.findByUser_IdAndDateBetween(userId, yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    @Transactional
    public Attendance correctAttendance(Long id, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Attendance not found"));
        attendance.setCheckIn(request.checkIn());
        attendance.setCheckOut(request.checkOut());
        attendance.setStatus(request.status());
        attendance.setRemarks(request.remarks());
        attendance.setMarkedBy(currentUserService.get());
        return attendanceRepository.save(attendance);
    }

    @Transactional
    public LeaveRequest applyLeave(LeaveApplyRequest request) {
        User user = currentUserService.get();
        LeaveRequest leave = new LeaveRequest();
        leave.setUser(user);
        leave.setLeaveType(request.leaveType());
        leave.setFromDate(request.fromDate());
        leave.setToDate(request.toDate());
        leave.setTotalDays((int) ChronoUnit.DAYS.between(request.fromDate(), request.toDate()) + 1);
        leave.setReason(request.reason());
        leave.setStatus(LeaveStatus.PENDING);
        leave.setAppliedAt(LocalDateTime.now());
        return leaveRequestRepository.save(leave);
    }

    @Transactional
    public LeaveRequest actionLeave(Long id, LeaveActionRequest request) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Leave request not found"));
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new AppException(HttpStatus.CONFLICT, "Leave request has already been actioned");
        }
        leave.setStatus(request.status());
        leave.setApprovedBy(currentUserService.get());
        leave.setActionedAt(LocalDateTime.now());
        if (request.status() == LeaveStatus.APPROVED) {
            deductLeaveBalance(leave);
        }
        return leaveRequestRepository.save(leave);
    }

    public List<LeaveRequest> pendingLeaves(Long centerId) {
        return leaveRequestRepository.findByUser_Center_IdAndStatus(centerId, LeaveStatus.PENDING);
    }

    public LeaveBalance leaveBalance(Long userId) {
        return getOrCreateBalance(findUser(userId), LocalDate.now().getYear());
    }

    @Transactional
    public List<Payroll> generatePayroll(PayrollGenerateRequest request) {
        YearMonth yearMonth = YearMonth.of(request.year(), request.month());
        int workingDays = yearMonth.lengthOfMonth();
        return staffProfileRepository.findByUser_Center_Id(request.centerId()).stream().map(profile -> {
            User user = profile.getUser();
            long present = attendanceRepository.countByUser_IdAndDateBetweenAndStatus(user.getId(), yearMonth.atDay(1), yearMonth.atEndOfMonth(), AttendanceStatus.PRESENT);
            long halfDays = attendanceRepository.countByUser_IdAndDateBetweenAndStatus(user.getId(), yearMonth.atDay(1), yearMonth.atEndOfMonth(), AttendanceStatus.HALF_DAY);
            long leaveDays = attendanceRepository.countByUser_IdAndDateBetweenAndStatus(user.getId(), yearMonth.atDay(1), yearMonth.atEndOfMonth(), AttendanceStatus.ON_LEAVE);
            int paidPresentDays = (int) present + (int) (halfDays / 2);
            int absentDays = Math.max(0, workingDays - paidPresentDays - (int) leaveDays);

            Payroll payroll = payrollRepository.findByUser_IdAndMonthAndYear(user.getId(), request.month(), request.year()).orElseGet(Payroll::new);
            payroll.setUser(user);
            payroll.setMonth(request.month());
            payroll.setYear(request.year());
            payroll.setBaseSalary(profile.getBaseSalary());
            payroll.setWorkingDays(workingDays);
            payroll.setPresentDays(paidPresentDays);
            payroll.setLeaveDays((int) leaveDays);
            payroll.setAbsentDays(absentDays);
            BigDecimal gross = profile.getBaseSalary().divide(BigDecimal.valueOf(workingDays), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(paidPresentDays + leaveDays));
            payroll.setGrossSalary(gross);
            payroll.setDeductions(request.deductions() != null ? request.deductions() : BigDecimal.ZERO);
            payroll.setBonus(request.bonus() != null ? request.bonus() : BigDecimal.ZERO);
            payroll.setNetSalary(gross.subtract(payroll.getDeductions()).add(payroll.getBonus()));
            payroll.setStatus(PayrollStatus.PROCESSED);
            payroll.setGeneratedBy(currentUserService.get());
            payroll.setGeneratedAt(LocalDateTime.now());
            return payrollRepository.save(payroll);
        }).toList();
    }

    public Payroll payroll(Long userId, int month, int year) {
        return payrollRepository.findByUser_IdAndMonthAndYear(userId, month, year)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Payroll not found"));
    }

    @Transactional
    public Payroll markPayrollPaid(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Payroll not found"));
        payroll.setStatus(PayrollStatus.PAID);
        payroll.setPaidAt(LocalDateTime.now());
        return payrollRepository.save(payroll);
    }

    private void applyStaff(StaffProfile profile, StaffProfileRequest request) {
        profile.setUser(findUser(request.userId()));
        profile.setDepartment(request.department());
        profile.setDesignation(request.designation());
        profile.setDateOfJoining(request.dateOfJoining());
        profile.setBaseSalary(request.baseSalary() != null ? request.baseSalary() : BigDecimal.ZERO);
        profile.setBankAccount(request.bankAccount());
        profile.setIfscCode(request.ifscCode());
        profile.setPanNumber(request.panNumber());
        profile.setAadharNumber(request.aadharNumber());
        profile.setEmergencyContact(request.emergencyContact());
        profile.setEmergencyName(request.emergencyName());
    }

    private void deductLeaveBalance(LeaveRequest leave) {
        LeaveBalance balance = getOrCreateBalance(leave.getUser(), leave.getFromDate().getYear());
        if (leave.getLeaveType() == LeaveType.SICK) {
            balance.setSickUsed(balance.getSickUsed() + leave.getTotalDays());
        } else if (leave.getLeaveType() == LeaveType.CASUAL) {
            balance.setCasualUsed(balance.getCasualUsed() + leave.getTotalDays());
        } else {
            balance.setEarnedUsed(balance.getEarnedUsed() + leave.getTotalDays());
        }
        leaveBalanceRepository.save(balance);
    }

    private LeaveBalance getOrCreateBalance(User user, int year) {
        return leaveBalanceRepository.findByUser_IdAndYear(user.getId(), year).orElseGet(() -> {
            LeaveBalance balance = new LeaveBalance();
            balance.setUser(user);
            balance.setYear(year);
            return leaveBalanceRepository.save(balance);
        });
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Center findCenter(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }
}
