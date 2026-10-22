package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.enums.UnitStatus;
import com.dem_ams.domain.model.enums.UnitType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public record UnitRequest(
        Long propertyId,
        UnitType unitType,
        Double squareMeters,
        Double baseRent,
        String currency,
        UnitStatus status,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "unitType"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ResidentialUnitDetails.class, name = "RESIDENTIAL"),
                @JsonSubTypes.Type(value = CommercialUnitDetails.class, name = "COMMERCIAL")
        })
        IUnitDetails unitDetails
) {
}
