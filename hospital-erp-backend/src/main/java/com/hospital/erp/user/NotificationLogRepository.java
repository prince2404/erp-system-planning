package com.hospital.erp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findTop20ByUser_IdOrderByCreatedAtDesc(Long userId);
}
