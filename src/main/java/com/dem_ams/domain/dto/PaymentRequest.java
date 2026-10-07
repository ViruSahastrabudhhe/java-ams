package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.Payment;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record PaymentRequest(
        @NotNull Double amountPaid,
        Double amountChange,
        Payment.PaymentType paymentType,
        Long invoiceId,
        Long tenantProfileId)
        implements Serializable {
}
