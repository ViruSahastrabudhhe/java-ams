package com.dem_ams.controller;

import com.dem_ams.domain.model.assembler.PaymentModelAssembler;
import com.dem_ams.domain.dto.PaymentRequest;
import com.dem_ams.domain.dto.PaymentResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/{paymentId}")
    public ResponseEntity<EntityModel<PaymentResponse>> getPaymentById(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Payment payment = paymentService.getPaymentForUser(paymentId, principal);
        PaymentResponse paymentResponse = PaymentResponse.from(payment);
        return ResponseEntity.ok(paymentAssembler.toModel(paymentResponse));
    }

    @PostMapping
    public ResponseEntity<EntityModel<PaymentResponse>> savePayment(
            @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Payment payment = paymentService.createPayment(paymentRequest, principal.getId());
        return new ResponseEntity<>(paymentAssembler.toModel(PaymentResponse.from(payment)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PaymentResponse>>> getAllPayments(
            Pageable pageable,
            PagedResourcesAssembler<PaymentResponse> pageAssembler,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Page<Payment> page = paymentService.getPaymentsForUser(pageable, principal);
        Page<PaymentResponse> paymentResponsePage = page.map(PaymentResponse::from);
        return ResponseEntity.ok(pageAssembler.toModel(paymentResponsePage));
    }

    @PutMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<PaymentResponse>> updatePayment(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentRequest paymentForm,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Payment payment = paymentService.getPaymentForUser(paymentId, principal);
        paymentService.updatePayment(payment, paymentForm);
        return ResponseEntity.ok(paymentAssembler.toModel(PaymentResponse.from(payment)));
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(
            @PathVariable("paymentId") Long paymentId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Payment payment = paymentService.getPaymentForUser(paymentId, principal);
        paymentService.deletePaymentById(payment.getId());
        return ResponseEntity.noContent().build();
    }
}
