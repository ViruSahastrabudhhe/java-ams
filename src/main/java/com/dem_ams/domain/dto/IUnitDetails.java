package com.dem_ams.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "unitType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResidentialUnitDetails.class, name = "RESIDENTIAL"),
        @JsonSubTypes.Type(value = CommercialUnitDetails.class, name = "COMMERCIAL")
})
public interface IUnitDetails {
}
