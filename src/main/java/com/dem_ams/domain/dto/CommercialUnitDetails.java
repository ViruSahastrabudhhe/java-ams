package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.CommercialUnit;

public record CommercialUnitDetails(
) implements IUnitDetails {
    public static CommercialUnitDetails from(CommercialUnit commercialUnit) {
        return new CommercialUnitDetails();
    }
}
