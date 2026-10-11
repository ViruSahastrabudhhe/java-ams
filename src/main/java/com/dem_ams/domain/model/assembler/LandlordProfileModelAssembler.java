package com.dem_ams.domain.model.assembler;

import com.dem_ams.controller.LandlordProfileController;
import com.dem_ams.domain.dto.LandlordProfileResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LandlordProfileModelAssembler implements RepresentationModelAssembler<LandlordProfileResponse, EntityModel<LandlordProfileResponse>> {

    @Override
    public  EntityModel<LandlordProfileResponse> toModel(LandlordProfileResponse landlordProfile) {
        return EntityModel.of(landlordProfile,
                linkTo(methodOn(LandlordProfileController.class).getLandlordProfile(null)).withSelfRel());
    }
}
