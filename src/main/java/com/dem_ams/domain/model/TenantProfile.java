package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="tenantProfileId", referencedColumnName = "id")
    private Set<Lease> leases;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
