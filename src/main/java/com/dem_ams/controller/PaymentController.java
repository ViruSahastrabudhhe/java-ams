package com.dem_ams.controller;

import com.dem_ams.domain.model.Role;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment payment;
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            payment = paymentService.getPaymentById(paymentId);
        } else {
            payment = paymentService.getPaymentByIdAndUserId(paymentId, user.getId());
        }

        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        PaymentResponse paymentResponse = Payment.toJson(payment);
        return ResponseEntity.ok(paymentAssembler.toModel(paymentResponse));
    }

    @PostMapping
    public ResponseEntity<EntityModel<PaymentResponse>> savePayment(
            @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment createdPayment = paymentService.createPayment(paymentRequest, user.getId());
        PaymentResponse paymentResponse = Payment.toJson(createdPayment);
        return new ResponseEntity<>(paymentAssembler.toModel(paymentResponse), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PaymentResponse>>> getAllPayments(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user,
            PagedResourcesAssembler<PaymentResponse> pageAssembler
    ) {
        Page<Payment> page;
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            page = paymentService.getPaymentsNoUserId(pageable);
        } else {
            page = paymentService.getPayments(user.getId(), pageable);
        }

        Page<PaymentResponse> paymentResponsePage = page.map(Payment::toJson);
        return ResponseEntity.ok(pageAssembler.toModel(paymentResponsePage));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<EntityModel<PaymentResponse>> updatePayment(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentRequest paymentForm,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment payment;
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            payment = paymentService.getPaymentById(paymentId);
        } else {
            payment = paymentService.getPaymentByIdAndUserId(paymentId, user.getId());
        }

        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        paymentService.updatePayment(payment, paymentForm);
        PaymentResponse paymentResponse = Payment.toJson(payment);
        return new ResponseEntity<>(paymentAssembler.toModel(paymentResponse), HttpStatus.OK);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable("paymentId") Long paymentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.notFound().build();
        }

        if (!paymentService.existsById(paymentId)) {
            return ResponseEntity.notFound().build();
        }
        paymentService.deletePaymentById(paymentId);
        return ResponseEntity.noContent().build();
    }
}
