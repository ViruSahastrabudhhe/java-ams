package com.dem_ams.domain.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super("Could not find payment with id: " + id);
    }
}
