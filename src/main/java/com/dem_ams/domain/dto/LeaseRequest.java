package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.enums.BillingType;
import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.enums.LeaseStatus;

import java.io.Serializable;
import java.time.LocalDate;

public record LeaseRequest(
    Long unitId,
    Long tenantProfileId,
    LocalDate startDate,
    LocalDate endDate,
    BillingType billingType,
    Double rent,
    Double deposit,
    LeaseStatus status
) implements Serializable {
}
