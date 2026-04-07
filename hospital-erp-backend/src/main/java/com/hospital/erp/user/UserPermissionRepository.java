package com.hospital.erp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUser_Id(Long userId);
    boolean existsByUser_IdAndPermission_Id(Long userId, Long permissionId);
    void deleteByUser_Id(Long userId);
}
