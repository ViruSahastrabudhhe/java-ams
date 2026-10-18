package com.dem_ams.controller;

import com.dem_ams.domain.dto.LeaseResponse;
import com.dem_ams.domain.dto.TenantProfileRequest;
import com.dem_ams.domain.dto.TenantProfileResponse;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.TenantProfile;
import com.dem_ams.domain.model.assembler.LeaseModelAssembler;
import com.dem_ams.domain.model.assembler.TenantProfileModelAssembler;
import com.dem_ams.service.LeaseService;
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
    private final LeaseService leaseService;
    private final TenantProfileModelAssembler tenantProfileAssembler;
    private final LeaseModelAssembler leaseAssembler;

    public TenantProfileController(
            TenantProfileService tenantProfileService,
            LeaseService leaseService,
            TenantProfileModelAssembler tenantProfileAssembler,
            LeaseModelAssembler leaseAssembler) {
        this.tenantProfileService = tenantProfileService;
        this.leaseService = leaseService;
        this.tenantProfileAssembler = tenantProfileAssembler;
        this.leaseAssembler = leaseAssembler;
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<TenantProfileResponse>> getTenantProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileByUserId(principal.getUser().getId());
        return ResponseEntity.ok(tenantProfileAssembler.toModel(TenantProfileResponse.from(profile)));
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
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<EntityModel<TenantProfileResponse>> getTenantProfileById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileById(requestedId, principal);
        return ResponseEntity.ok(tenantProfileAssembler.toModel(TenantProfileResponse.from(profile)));
    }

    @PostMapping
    public ResponseEntity<EntityModel<TenantProfileResponse>> createTenantProfile(
            @RequestBody TenantProfileRequest tenantProfileRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.createTenantProfile(tenantProfileRequest, principal.getUser().getId());
        return new ResponseEntity<>(tenantProfileAssembler.toModel(TenantProfileResponse.from(profile)), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<EntityModel<TenantProfileResponse>> updateTenantProfile(
            @RequestBody TenantProfileRequest tenantProfileRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileByUserId(principal.getUser().getId());
        tenantProfileService.updateTenantProfile(profile, tenantProfileRequest);
        return ResponseEntity.ok(tenantProfileAssembler.toModel(TenantProfileResponse.from(profile)));
    }

    @DeleteMapping
    public ResponseEntity<EntityModel<TenantProfileResponse>> deleteTenantProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TenantProfile profile = tenantProfileService.getTenantProfileByUserId(principal.getUser().getId());
        tenantProfileService.deleteTenantProfileById(profile.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/leases")
    public ResponseEntity<PagedModel<EntityModel<LeaseResponse>>> getAllTenantLeases(
            Pageable pageable,
            PagedResourcesAssembler<LeaseResponse> pageAssembler,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Page<Lease> page = leaseService.getAllLeasesByTenantProfileUserId(principal.getUser().getId(), pageable);
        Page<LeaseResponse> leaseResponsePage = page.map(LeaseResponse::from);
        return ResponseEntity.ok(pageAssembler.toModel(leaseResponsePage));
    }

    @GetMapping("/me/leases/{requestedId}")
    public ResponseEntity<EntityModel<LeaseResponse>> getTenantLeaseById(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Lease lease = leaseService.getLeaseByIdAndTenantProfileUserId(requestedId, principal.getUser().getId());
        return ResponseEntity.ok(leaseAssembler.toModel(LeaseResponse.from(lease)));
    }
}
