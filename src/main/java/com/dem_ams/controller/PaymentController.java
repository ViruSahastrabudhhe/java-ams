package com.dem_ams.controller;

import com.dem_ams.domain.dto.PaymentFormDTO;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment payment = paymentService.getPaymentByIdAndUserId(requestedId, user.getId());
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<Void> savePayment(
            @RequestBody PaymentFormDTO paymentFormDTO,
            @AuthenticationPrincipal CustomUserDetails user,
            UriComponentsBuilder ucb
    ) {
        Payment createdPayment = paymentService.createPayment(paymentFormDTO, user.getId());
        URI locationOfCreatedPayment = ucb
                .path("/payments/{id}")
                .buildAndExpand(createdPayment.getId())
                .toUri();

        return ResponseEntity.created(locationOfCreatedPayment).build();
    }

    @GetMapping
    public ResponseEntity<Iterable<Payment>> getAllPayments(
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable
    ) {
        Page<Payment> page = paymentService.getPayments(user.getId(), pageable);
        return ResponseEntity.ok(page.getContent());
    }

//    @DeleteMapping("/delete/{id}")
//    public void deletePayment(@PathVariable("id") long id) {
//        paymentService.deletePayment(paymentService.getPaymentById(id));
//    }
}
