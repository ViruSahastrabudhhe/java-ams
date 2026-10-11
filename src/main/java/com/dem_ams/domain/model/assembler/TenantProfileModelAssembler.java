package com.dem_ams.domain.model.assembler;

import com.dem_ams.controller.TenantProfileController;
import com.dem_ams.domain.dto.TenantProfileResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TenantProfileModelAssembler implements RepresentationModelAssembler<TenantProfileResponse, EntityModel<TenantProfileResponse>> {

    @Override
    public  EntityModel<TenantProfileResponse> toModel(TenantProfileResponse tenantProfile) {
        return EntityModel.of(tenantProfile,
                linkTo(methodOn(TenantProfileController.class).getTenantProfile(null)).withSelfRel());
    }
}
