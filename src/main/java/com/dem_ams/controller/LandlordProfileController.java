package com.dem_ams.controller;

import com.dem_ams.domain.dto.LandlordProfileResponse;
import com.dem_ams.domain.dto.LandlordProfileRequest;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.LandlordProfile;
import com.dem_ams.domain.model.assembler.LandlordProfileModelAssembler;
import com.dem_ams.service.LandlordProfileService;
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
@RequestMapping("/api/landlords")
public class LandlordProfileController {

    private final LandlordProfileService landlordProfileService;
    private final LandlordProfileModelAssembler assembler;

    public LandlordProfileController(
            LandlordProfileService landlordProfileService,
            LandlordProfileModelAssembler assembler
    ) {
        this.landlordProfileService = landlordProfileService;
        this.assembler = assembler;
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<LandlordProfileResponse>> getLandlordProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        LandlordProfile landlordProfile = landlordProfileService.getLandlordProfileByUserId(principal.getId());
        return ResponseEntity.ok(assembler.toModel(LandlordProfileResponse.from(landlordProfile)));
    }

    /*
        NOTE: HOW WILL WE IMPLEMENT A TENANT REQUESTING THEIR APARTMENT'S LANDLORD PROFILE?
    */
    @GetMapping("/{requestedId}")
    public ResponseEntity<EntityModel<LandlordProfileResponse>> getLandlordProfileById(
            @PathVariable("requestedId") Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        LandlordProfile landlordProfile = landlordProfileService.getLandlordProfileById(requestedId, principal);
        return ResponseEntity.ok(assembler.toModel(LandlordProfileResponse.from(landlordProfile)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<LandlordProfileResponse>>> getAllLandlordProfiles(
            Pageable pageable,
            PagedResourcesAssembler<LandlordProfileResponse> pageAssembler
    ) {
        Page<LandlordProfile> page = landlordProfileService.getAllLandlordProfiles(pageable);
        Page<LandlordProfileResponse> landlordProfilePage = page.map(LandlordProfileResponse::from);
        return ResponseEntity.ok(pageAssembler.toModel(landlordProfilePage));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<EntityModel<LandlordProfileResponse>> createLandlordProfile(
            @RequestBody LandlordProfileRequest landlordProfileRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        LandlordProfile profile = landlordProfileService.createLandlordProfile(landlordProfileRequest, principal.getId());
        return new ResponseEntity<>(assembler.toModel(LandlordProfileResponse.from(profile)), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<EntityModel<LandlordProfileResponse>> updateLandlordProfile(
            @RequestBody LandlordProfileRequest landlordProfileRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        LandlordProfile profile = landlordProfileService.getLandlordProfileByUserId(principal.getId());
        landlordProfileService.updateLandlordProfile(profile, landlordProfileRequest);
        return ResponseEntity.ok(assembler.toModel(LandlordProfileResponse.from(profile)));
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<EntityModel<LandlordProfileResponse>> deleteLandlordProfile(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        LandlordProfile profile = landlordProfileService.getLandlordProfileByUserId(principal.getId());
        landlordProfileService.deleteLandlordProfileById(profile.getId());
        return ResponseEntity.noContent().build();
    }
}
