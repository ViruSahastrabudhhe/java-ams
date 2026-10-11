package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.BillingType;

import java.io.Serializable;

public record PropertyRequest(
        String name,
        String propertyType,
        Double propertyValue,
        BillingType billingType,
        Integer bedroomQuantity,
        Integer bathroomQuantity,
        String size,
        String description
) implements Serializable {
}
