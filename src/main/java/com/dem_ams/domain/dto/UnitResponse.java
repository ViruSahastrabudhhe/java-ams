package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.Unit;
import com.dem_ams.domain.model.enums.UnitStatus;
import com.dem_ams.domain.model.enums.UnitType;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UnitResponse(
        Long id,
        Long property_id,
        UnitType unit_type,
        Double square_meters,
        Double base_rent,
        String currency,
        UnitStatus status,
        IUnitDetails unit_details,
        LocalDateTime created_at,
        LocalDateTime updated_at,
        LocalDateTime deleted_at
) implements Serializable {
    public static UnitResponse from(Unit unit, IUnitDetails unitDetails) {
        return new UnitResponse(
                unit.getId(),
                unit.getProperty().getId(),
                unit.getUnitType(),
                unit.getSquareMeters(),
                unit.getBaseRent(),
                unit.getCurrency(),
                unit.getStatus(),
                unitDetails,
                unit.getCreatedAt(),
                unit.getUpdatedAt(),
                unit.getDeletedAt()
        );
    }
}
