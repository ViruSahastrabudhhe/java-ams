package com.dem_ams.domain.model.assembler;

import com.dem_ams.controller.PropertyController;
import com.dem_ams.domain.dto.PropertyResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PropertyModelAssembler implements RepresentationModelAssembler<PropertyResponse, EntityModel<PropertyResponse>> {

    @Override
    public  EntityModel<PropertyResponse> toModel(PropertyResponse property) {
        return EntityModel.of(property,
                linkTo(methodOn(PropertyController.class).getPropertyById(property.id(), null)).withSelfRel()
        );
    }
}