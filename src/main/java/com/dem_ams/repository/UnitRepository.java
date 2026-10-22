package com.dem_ams.repository;

import com.dem_ams.domain.model.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    @Query("SELECT u FROM Unit u WHERE u.id = :id and u.property.landlordProfile.user.id = :userId and u.deletedAt is null")
    Optional<Unit> findByIdAndLandlordProfileUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT u FROM Unit u WHERE u.property.landlordProfile.user.id = :landlordProfileUserId and u.deletedAt is null")
    Page<Unit> findAllByLandlordProfileUserId(@Param("landlordProfileUserId") Long landlordProfileUserId, PageRequest pageRequest);

    @Query("SELECT u FROM Unit u WHERE u.id = :id and u.deletedAt is null")
    Optional<Unit> findById(@Param("id") Long id);

    @Query("SELECT u FROM Unit u WHERE u.deletedAt is null")
    Page<Unit> findAll(PageRequest pageRequest);
}
