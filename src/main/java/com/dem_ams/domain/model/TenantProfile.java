package com.dem_ams.domain.model;

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
@Table(name = "tenant_profiles")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE tenant_profiles SET deleted_at = NOW() WHERE id = ?")
public class TenantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="tenantProfileId", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Lease> leases;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addLease(Lease lease) {
        leases.add(lease);
    }
}
