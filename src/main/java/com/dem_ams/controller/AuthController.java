package com.dem_ams.controller;

import com.dem_ams.config.CustomUserDetailsService;
import com.dem_ams.constant.ApplicationConstants;
import com.dem_ams.domain.dto.*;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Permission;
import com.dem_ams.domain.model.Role;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.PermissionRepository;
import com.dem_ams.repository.RoleRepository;
import com.dem_ams.repository.UserModelRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserModelRepository userModelRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserModelRepository userModelRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userModelRepository = userModelRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            @RequestBody LoginRequest loginRequest
    ) throws AuthenticationException {
        try {
            Authentication authenticationRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
            Authentication authenticationResponse =
                    this.authenticationManager.authenticate(authenticationRequest);

            CustomUserDetails authenticatedUser = (CustomUserDetails) authenticationResponse.getPrincipal();
            UserModel userModel = authenticatedUser.getUser();

            UserModelResponse user = UserModelResponse.from(userModel);

            return ResponseEntity.ok(LoginResponse.from(HttpStatus.OK.getReasonPhrase(), user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().build();
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        Role role = roleRepository.findByName(ApplicationConstants.ROLE_TENANT)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find role", ApplicationConstants.ROLE_TENANT));
        Set<Permission> permissions = permissionRepository.findAll().stream()
                .filter(p -> ApplicationConstants.TENANT_PERMISSIONS.contains(p.getName()))
                .collect(Collectors.toSet());
        role.setPermissions(permissions);

        UserModel userModel = new UserModel();
        userModel.setUsername(registerRequest.username());
        userModel.setPassword(passwordEncoder.encode(registerRequest.password()));
        userModel.setEmail(registerRequest.email());
        userModel.setRoles(Set.of(role));
        userModelRepository.saveAndFlush(userModel);
        return new ResponseEntity<>(RegisterResponse.from(userModel), HttpStatus.CREATED);
    }
}
