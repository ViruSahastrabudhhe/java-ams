package com.dem_ams.repository;

import com.dem_ams.domain.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("SELECT pr FROM Property pr WHERE pr.id = :id and pr.landlordProfile.user.id = :userId and pr.deletedAt is null")
    Optional<Property> findByIdAndLandlordProfileUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT pr FROM Property pr WHERE pr.landlordProfile.user.id = :landlordProfileUserId and pr.deletedAt is null")
    Page<Property> findAllByLandlordProfileUserId(@Param("landlordProfileUserId") Long landlordProfileUserId, PageRequest pageRequest);

    @Query("SELECT pr FROM Property pr WHERE pr.id = :id and pr.deletedAt is null")
    Optional<Property> findById(@Param("id") Long id);

    @Query("SELECT pr FROM Property pr WHERE pr.deletedAt is null")
    Page<Property> findAll(PageRequest pageRequest);
}
