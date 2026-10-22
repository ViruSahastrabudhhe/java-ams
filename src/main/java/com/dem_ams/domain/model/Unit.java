package com.dem_ams.domain.model;

import com.dem_ams.domain.dto.IUnitDetails;
import com.dem_ams.domain.model.enums.UnitStatus;
import com.dem_ams.domain.model.enums.UnitType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "units")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE units SET deleted_at = NOW() WHERE id = ?")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="propertyId", nullable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Property property;

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Lease> leases;

    @OneToOne(optional = false, mappedBy = "unit", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ResidentialUnit residentialUnit;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;
    private Double squareMeters;
    private Double baseRent;
    private String currency;
    @Enumerated(EnumType.STRING)
    private UnitStatus status;
    @Transient
    private IUnitDetails unitDetails;

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
