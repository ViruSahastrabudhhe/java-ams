package com.dem_ams.repository;

import com.dem_ams.domain.model.ResidentialUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ResidentialUnitRepository extends JpaRepository<ResidentialUnit, Long> {
    @Query("SELECT ru FROM ResidentialUnit ru WHERE ru.unit.id = :unitId and ru.deletedAt is null")
    Optional<ResidentialUnit> findByUnitId(@Param("unitId") Long unitId);
}
