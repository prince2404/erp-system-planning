package com.hospital.erp.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.geographic.entities.Center;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "beds", uniqueConstraints = @UniqueConstraint(columnNames = {"center_id", "bed_number"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bed extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Column(length = 50)
    private String ward;

    @Column(nullable = false, length = 20)
    private String bedNumber;

    @Column(name = "is_occupied", nullable = false)
    private Boolean occupied = false;
}
