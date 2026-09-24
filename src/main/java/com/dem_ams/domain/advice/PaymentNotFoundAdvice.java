package com.dem_ams.domain.advice;

import com.dem_ams.domain.exception.PaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentNotFoundAdvice {

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String paymentNotFoundHandler(PaymentNotFoundException ex) {
        return ex.getMessage();
    }
}
