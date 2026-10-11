package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "property", cascade = CascadeType.ALL)
    private PropertyDetail propertyDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="landlordProfileId", nullable=false)
    private LandlordProfile landlordProfile;

    @OneToOne(mappedBy = "property")
    private Lease lease;

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
