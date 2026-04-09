package com.hospital.erp.user;

import com.hospital.erp.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByCenter_IdAndActiveTrue(Long centerId);
    List<User> findByRoleAndCenter_IdAndActiveTrue(Role role, Long centerId);
    List<User> findByRole(Role role);
}
