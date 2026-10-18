package com.dem_ams.domain.model;

import com.dem_ams.domain.model.enums.BillingType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="properties")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE properties SET deleted_at = NOW() WHERE id = ?")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="landlordProfileId", nullable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LandlordProfile landlordProfile;

    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private String propertyType;
    @Column(nullable=false)
    private Double propertyValue;
    @Enumerated(EnumType.STRING)
    private BillingType billingType;
    private Double size;
    @Column(columnDefinition = "TEXT")
    private String description;

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
