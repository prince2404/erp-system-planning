package com.hospital.erp.user;

import com.hospital.erp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"module", "action"}))
public class Permission extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String module;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 200)
    private String description;
}
