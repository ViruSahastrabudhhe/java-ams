package com.dem_ams.domain.assembler;

import com.dem_ams.controller.PaymentController;
import com.dem_ams.domain.dto.PaymentResponse;
import com.dem_ams.domain.model.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<PaymentResponse, EntityModel<PaymentResponse>> {

    @Override
    public  EntityModel<PaymentResponse> toModel(PaymentResponse payment) {
        return EntityModel.of(payment,
                linkTo(methodOn(PaymentController.class).getPaymentById(payment.id(), null)).withSelfRel(),
                linkTo(methodOn(PaymentController.class).getAllPayments(Pageable.unpaged(), null, null)).withRel("payments")
        );
    }
}
