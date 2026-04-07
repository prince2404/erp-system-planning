package com.hospital.erp.hr;

import com.hospital.erp.common.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUser_Center_IdAndStatus(Long centerId, LeaveStatus status);
    List<LeaveRequest> findByUser_IdOrderByAppliedAtDesc(Long userId);
}
