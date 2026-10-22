package com.dem_ams.domain.model.assembler;

import com.dem_ams.controller.TenantProfileController;
import com.dem_ams.controller.UserController;
import com.dem_ams.domain.dto.TenantProfileResponse;
import com.dem_ams.domain.dto.UserModelResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserModelResponse, EntityModel<UserModelResponse>> {

    @Override
    public  EntityModel<UserModelResponse> toModel(UserModelResponse userModel) {
        return EntityModel.of(userModel,
                linkTo(methodOn(UserController.class).getUser(null)).withSelfRel());
    }
}
