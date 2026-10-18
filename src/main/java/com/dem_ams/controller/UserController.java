package com.dem_ams.controller;

import com.dem_ams.domain.dto.UserModelResponse;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.domain.model.assembler.UserModelAssembler;
import com.dem_ams.repository.UserModelRepository;
import com.dem_ams.service.UserModelService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserModelService userModelService;
    private final UserModelAssembler assembler;

    public UserController(
            UserModelService userModelService,
            UserModelAssembler assembler) {
        this.userModelService = userModelService;
        this.assembler = assembler;
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserModelResponse>> getUser(
            @AuthenticationPrincipal CustomUserDetails principal) {
        UserModel userModel = userModelService.getUserModelByUsername(principal.getUsername());
        return ResponseEntity.ok(assembler.toModel(UserModelResponse.from(userModel)));
    }

    @GetMapping("/{requestedId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserModel> getUserById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        UserModel userModel = userModelService.getUserModelForUser(requestedId, principal);
        return ResponseEntity.ok(userModel);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<UserModel> updateUser(
            @PathVariable Long requestedId,
            @RequestBody UserModel userModelRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        UserModel userModel = userModelService.getUserModelForUser(requestedId, principal);
        userModelService.updateUserModel(userModel, userModelRequest);
        return ResponseEntity.noContent().build();
    }
}
