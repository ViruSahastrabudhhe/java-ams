package com.dem_ams.controller;

import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UserController {
    @GetMapping("/username")
    public ResponseEntity<String> currentUserName(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(user.getUsername());
    }
}
