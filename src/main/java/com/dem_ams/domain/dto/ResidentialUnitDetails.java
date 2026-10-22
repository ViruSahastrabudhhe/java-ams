package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.ResidentialUnit;

public record ResidentialUnitDetails(
        String unit_number,
        Integer unit_floor,
        Integer bedroom_quantity,
        Integer bathroom_quantity
) implements IUnitDetails {
    public static ResidentialUnitDetails from(ResidentialUnit residentialUnit) {
        return new ResidentialUnitDetails(
                residentialUnit.getUnitNumber(),
                residentialUnit.getFloor(),
                residentialUnit.getBedroomQuantity(),
                residentialUnit.getBathroomQuantity()
        );
    }
}
