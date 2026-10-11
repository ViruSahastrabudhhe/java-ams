package com.dem_ams.repository;

import com.dem_ams.domain.model.TenantProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TenantProfileRepository extends JpaRepository<TenantProfile,Long> {
    @Query("SELECT tp FROM TenantProfile tp WHERE tp.user.id = :userId and tp.deletedAt is null")
    Optional<TenantProfile> findByUserId(@Param("userId") Long userId);

    @Query("SELECT tp FROM TenantProfile tp WHERE tp.deletedAt is null")
    Page<TenantProfile> findAll(PageRequest pageRequest);
}
