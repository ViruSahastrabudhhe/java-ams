package com.dem_ams.controller;

import com.dem_ams.domain.dto.PaymentResponse;
import com.dem_ams.domain.dto.TenantProfileRequest;
import com.dem_ams.domain.dto.TenantProfileResponse;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.TenantProfile;
import com.dem_ams.domain.model.assembler.TenantProfileModelAssembler;
import com.dem_ams.service.TenantProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
public class TenantProfileController {

    private final TenantProfileService tenantProfileService;
    private final TenantProfileModelAssembler assembler;

    public TenantProfileController(TenantProfileService tenantProfileService, TenantProfileModelAssembler assembler) {
        this.tenantProfileService = tenantProfileService;
        this.assembler = assembler;
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<TenantProfileResponse>> getTenantProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileByUserId(principal.getId());
        return ResponseEntity.ok(assembler.toModel(TenantProfileResponse.from(profile)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<PagedModel<EntityModel<TenantProfileResponse>>> getAllTenantProfiles(
            Pageable pageable,
            PagedResourcesAssembler<TenantProfileResponse> pageAssembler,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Page<TenantProfile> page = tenantProfileService.getAllTenantProfiles(pageable, principal);
        Page<TenantProfileResponse> tenantProfilePage = page.map(TenantProfileResponse::from);
        return ResponseEntity.ok(pageAssembler.toModel(tenantProfilePage));
    }

    @GetMapping("/{requestedId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<TenantProfileResponse>> getTenantProfileById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileById(requestedId, principal);
        return ResponseEntity.ok(assembler.toModel(TenantProfileResponse.from(profile)));
    }

    @PostMapping
    public ResponseEntity<EntityModel<TenantProfileResponse>> createTenantProfile(
            @RequestBody TenantProfileRequest tenantProfileRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.createTenantProfile(tenantProfileRequest, principal.getId());
        return new ResponseEntity<>(assembler.toModel(TenantProfileResponse.from(profile)), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<EntityModel<TenantProfileResponse>> updateTenantProfile(
            @RequestBody TenantProfileRequest tenantProfileRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileByUserId(principal.getId());
        tenantProfileService.updateTenantProfile(profile, tenantProfileRequest);
        return ResponseEntity.ok(assembler.toModel(TenantProfileResponse.from(profile)));
    }

    @DeleteMapping
    public ResponseEntity<EntityModel<TenantProfileResponse>> deleteTenantProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileByUserId(principal.getId());
        tenantProfileService.deleteTenantProfileById(profile.getId());
        return ResponseEntity.noContent().build();
    }
}
