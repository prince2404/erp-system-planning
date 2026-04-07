package com.hospital.erp.pharmacy;

import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.DrugUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "drugs")
public class Drug extends BaseEntity {
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String genericName;

    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private DrugUnit unit;

    @Column(length = 20)
    private String hsnCode;

    @Column(length = 150)
    private String manufacturer;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
