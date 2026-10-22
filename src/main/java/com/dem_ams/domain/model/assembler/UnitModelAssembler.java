package com.dem_ams.domain.model.assembler;

import com.dem_ams.controller.UnitController;
import com.dem_ams.domain.dto.UnitResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UnitModelAssembler implements RepresentationModelAssembler<UnitResponse, EntityModel<UnitResponse>> {

    @Override
    public  EntityModel<UnitResponse> toModel(UnitResponse unit) {
        return EntityModel.of(unit,
                linkTo(methodOn(UnitController.class).getUnitById(unit.id(), null, null)).withSelfRel()
        );
    }
}
