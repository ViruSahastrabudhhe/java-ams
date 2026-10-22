package com.dem_ams.domain.model.assembler;

import com.dem_ams.controller.LandlordProfileController;
import com.dem_ams.controller.LeaseController;
import com.dem_ams.domain.dto.LandlordProfileResponse;
import com.dem_ams.domain.dto.LeaseResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LeaseModelAssembler implements RepresentationModelAssembler<LeaseResponse, EntityModel<LeaseResponse>> {

    @Override
    public  EntityModel<LeaseResponse> toModel(LeaseResponse lease) {
        return EntityModel.of(lease,
                linkTo(methodOn(LeaseController.class).getLeaseById(null, null)).withSelfRel(),
                linkTo(methodOn(LeaseController.class).getAllLeases(null, null, null)).withRel("allLeases")
        );
    }
}
