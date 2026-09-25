package com.dem_ams.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record PaymentRequest(
        @NotNull Double amountPaid,
        Long user_id)
        implements Serializable {
}
