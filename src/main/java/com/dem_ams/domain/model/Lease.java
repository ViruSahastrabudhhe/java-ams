package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name="leases")
@NoArgsConstructor
public class Lease {
    public enum LeaseStatus {
        DRAFT, TENANTS_SIGNING, READY_TO_SIGN, FINALIZED, EXPIRED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="propertyId", unique=true, nullable=false, updatable=false)
    private Property property;

    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
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
