package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.Payment;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PaymentRequest(
        @NotNull Double amountPaid,
        Double amountChange,
        Payment.PaymentType paymentType,
        Long invoice_id,
        Long tenant_profile_id, 
        Long user_id)
        implements Serializable {
}
