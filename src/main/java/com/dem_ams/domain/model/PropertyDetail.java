package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="property_details")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE properties SET deleted_at = NOW() WHERE id = ?")
public class PropertyDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="propertyId", nullable=false, unique=true)
    private Property property;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String propertyType;

    @Column(nullable=false)
    private Double propertyValue;

    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    private Integer bedroomQuantity;
    private Integer bathroomQuantity;
    private String size;

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
