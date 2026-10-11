package com.dem_ams.domain.dto;

import com.dem_ams.domain.model.Payment;
import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Double amount_paid,
        Double amount_change,
        Payment.PaymentType payment_type,
        Long invoice_id,
        Long tenant_profile_id,
        Long user_id,
        LocalDateTime created_at,
        @Nullable LocalDateTime updated_at,
        @Nullable LocalDateTime deleted_at
) implements Serializable {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmountPaid(),
                payment.getAmountChange(),
                payment.getPaymentType(),
                payment.getInvoice().getId(),
                payment.getTenantProfile().getId(),
                payment.getUser().getId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }
}
