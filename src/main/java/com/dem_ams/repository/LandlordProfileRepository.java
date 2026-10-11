package com.dem_ams.repository;

import com.dem_ams.domain.model.LandlordProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LandlordProfileRepository extends JpaRepository<LandlordProfile, Long> {
    @Query("SELECT lp FROM LandlordProfile lp WHERE lp.user.id = :userId and lp.deletedAt is null")
    Optional<LandlordProfile> findByUserId(@Param("userId") Long userId);
}
