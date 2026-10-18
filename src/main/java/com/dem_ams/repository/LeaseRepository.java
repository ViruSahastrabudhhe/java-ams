package com.dem_ams.repository;

import com.dem_ams.domain.model.Lease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LeaseRepository extends JpaRepository<Lease, Long> {

    @Query("SELECT l FROM Lease l WHERE l.id = :id and l.deletedAt is null")
    Optional<Lease> findById(@Param("id") Long id);

    @Query("SELECT l FROM Lease l WHERE l.id = :id and l.unit.property.landlordProfile.user.id = :landlordUserId and l.deletedAt is null")
    Optional<Lease> findByIdAndLandlordProfileUserId(@Param("id") Long id, @Param("landlordUserId") Long landlordUserId);

    @Query("SELECT l FROM Lease l WHERE l.unit.property.landlordProfile.user.id = :landlordUserId and l.deletedAt is null")
    Page<Lease> findAllByLandlordProfileUserId(@Param("landlordUserId") Long landlordUserId, PageRequest pageRequest);

    @Query("SELECT l FROM Lease l WHERE l.deletedAt is null")
    Page<Lease> findAll(PageRequest pageRequest);

    @Query("SELECT l FROM TenantProfile tp JOIN tp.leases l WHERE l.id = :id and tp.user.id = :userId and tp.deletedAt is null")
    Optional<Lease> findByIdAndTenantProfileUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT l FROM TenantProfile tp JOIN tp.leases l WHERE tp.user.id = :userId and tp.deletedAt is null")
    Page<Lease> findAllByTenantProfileUserId(@Param("userId") Long userId, PageRequest pageRequest);

    @Query("SELECT l FROM Lease l WHERE l.endDate <= CURRENT_DATE and l.deletedAt is null")
    Page<Lease> findExpiredByEndDate(PageRequest pageRequest);
}
