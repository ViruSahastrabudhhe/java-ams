package com.dem_ams.controller;

import com.dem_ams.domain.assembler.PaymentModelAssembler;
import com.dem_ams.domain.dto.PaymentRequest;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static Logger logger = Logger.getLogger(PaymentController.class.getName());

    private final PaymentService paymentService;
    private final PaymentModelAssembler paymentAssembler;

    public PaymentController(
            PaymentService paymentService,
            PaymentModelAssembler paymentAssembler
    ) {
        this.paymentService = paymentService;
        this.paymentAssembler = paymentAssembler;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<EntityModel<Payment>> getPaymentById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment payment = paymentService.getPaymentByIdAndUserId(requestedId, user.getId());
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(paymentAssembler.toModel(payment));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Payment>> savePayment(
            @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment createdPayment = paymentService.createPayment(paymentRequest, user.getId());
        return new ResponseEntity<>(paymentAssembler.toModel(createdPayment), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Payment>>> getAllPayments(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user,
            PagedResourcesAssembler<Payment> pageAssembler
    ) {
        Page<Payment> page = paymentService.getPayments(user.getId(), pageable);
        return ResponseEntity.ok(pageAssembler.toModel(page));
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<Payment>> updatePayment(
            @PathVariable("requestedId") Long requestedId,
            @RequestBody PaymentRequest paymentForm,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment payment = paymentService.getPaymentByIdAndUserId(requestedId, user.getId());
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        paymentService.updatePayment(requestedId, paymentForm, user.getId());
        return new ResponseEntity<>(paymentAssembler.toModel(payment), HttpStatus.OK);
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable("requestedId") Long requestedId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (!paymentService.existsByIdAndUserId(requestedId, user.getId())) {
            return ResponseEntity.notFound().build();
        }

        paymentService.deletePaymentById(requestedId);
        return ResponseEntity.noContent().build();
    }
}
