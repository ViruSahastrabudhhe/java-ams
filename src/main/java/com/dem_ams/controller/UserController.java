package com.dem_ams.controller;

import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<UserModel> getUser(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Optional<UserModel> userModel = userRepository.findById(requestedId);
        if  (userModel.isPresent()) {
            return ResponseEntity.ok(userModel.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<UserModel> updateUser(
            @RequestBody UserModel userModelRequest,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Optional<UserModel> userModel = userRepository.findById(user.getId());
        if (!userModel.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserModel updatedUserModel = userModel.get();
        updatedUserModel.setEmail(userModelRequest.getEmail());
        updatedUserModel.setUsername(userModelRequest.getUsername());
        updatedUserModel.setPassword(userModelRequest.getPassword());
        userRepository.save(updatedUserModel);

        return ResponseEntity.noContent().build();
    }
}
