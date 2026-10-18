package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.enums.BillingType;

import java.io.Serializable;

public record PropertyRequest(
        String name,
        String propertyType,
        Double propertyValue,
        BillingType billingType,
        Double size,
        String description
) implements Serializable {
}
