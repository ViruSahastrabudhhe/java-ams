package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.enums.BillingType;
import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.enums.LeaseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaseResponse(
        Long id,
        Long unit_id,
        LocalDate start_date,
        LocalDate end_date,
        BillingType billing_type,
        Double rent,
        Double deposit,
        LeaseStatus status,
        LocalDateTime created_at,
        LocalDateTime updated_at,
        LocalDateTime deleted_at
) {
    public static LeaseResponse from(Lease lease) {
        return new LeaseResponse(
                lease.getId(),
                lease.getUnit().getId(),
                lease.getStartDate(),
                lease.getEndDate(),
                lease.getBillingType(),
                lease.getRent(),
                lease.getDeposit(),
                lease.getStatus(),
                lease.getCreatedAt(),
                lease.getUpdatedAt(),
                lease.getDeletedAt()
        );
    }
}
