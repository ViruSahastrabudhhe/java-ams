package com.dem_ams.domain.model;

import com.dem_ams.domain.model.enums.BillingType;
import com.dem_ams.domain.model.enums.LeaseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="leases")
@NoArgsConstructor
public class Lease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="unit_id", unique=true, nullable=false, updatable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Unit unit;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private BillingType billingType;
    private Double rent;
    private Double deposit;
    @Enumerated(EnumType.STRING)
    private LeaseStatus status;

    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP", insertable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
