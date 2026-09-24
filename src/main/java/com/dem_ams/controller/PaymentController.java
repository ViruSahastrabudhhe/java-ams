package com.dem_ams.controller;

import com.dem_ams.domain.dto.PaymentFormDTO;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static Logger logger = Logger.getLogger(PaymentController.class.getName());

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<EntityModel<Payment>> getPaymentById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails user,
            PagedResourcesAssembler<Payment> assembler
    ) {
        Payment payment = paymentService.getPaymentByIdAndUserId(requestedId, user.getId());
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

            EntityModel<Payment> model = EntityModel.of(payment,
                    linkTo(methodOn(PaymentController.class).getPaymentById(requestedId, user, assembler)).withSelfRel(),
                    linkTo(methodOn(PaymentController.class).getAllPayments(Pageable.unpaged(), user, assembler)).withRel("payments"));
        return ResponseEntity.ok(model);
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
    public ResponseEntity<PagedModel<EntityModel<Payment>>> getAllPayments(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user,
            PagedResourcesAssembler<Payment> assembler
    ) {
        Page<Payment> page = paymentService.getPayments(user.getId(), pageable);
        return ResponseEntity.ok(assembler.toModel(page));
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updatePayment(
            @PathVariable Long requestedId,
            @RequestBody PaymentFormDTO paymentForm,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Payment payment = paymentService.getPaymentByIdAndUserId(requestedId, user.getId());
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        paymentService.updatePayment(requestedId, paymentForm, user.getId());

        return ResponseEntity.noContent().build();
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
