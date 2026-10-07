package com.dem_ams.controller;

import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.UserModelRepository;
import com.dem_ams.service.UserModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserModelService userModelService;

    public UserController(UserModelService userModelService) {
        this.userModelService = userModelService;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<UserModel> getUser(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        UserModel userModel = userModelService.getUserModelForUser(requestedId, principal);
        return ResponseEntity.ok(userModel);
    }

    @GetMapping("/{requestedId}/info")
    public ResponseEntity<UserModel> getUserInfo(
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
