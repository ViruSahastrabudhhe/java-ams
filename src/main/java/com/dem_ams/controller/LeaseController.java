package com.dem_ams.controller;

import com.dem_ams.domain.dto.LeaseRequest;
import com.dem_ams.domain.dto.LeaseResponse;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.assembler.LeaseModelAssembler;
import com.dem_ams.service.LeaseService;
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
@RequestMapping("/api/leases")
public class LeaseController {

    private final LeaseService leaseService;
    private final LeaseModelAssembler leaseAssembler;

    public LeaseController(
            LeaseService leaseService,
            LeaseModelAssembler leaseAssembler) {
        this.leaseService = leaseService;
        this.leaseAssembler = leaseAssembler;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<EntityModel<LeaseResponse>> getLeaseById(
            @PathVariable("requestedId") Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Lease lease = leaseService.getLeaseForUser(requestedId, principal);
        return ResponseEntity.ok(leaseAssembler.toModel(LeaseResponse.from(lease)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<PagedModel<EntityModel<LeaseResponse>>> getAllLeases(
            Pageable pageable,
            PagedResourcesAssembler<LeaseResponse> pageAssembler,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Page<Lease> page = leaseService.getLeasesForUser(pageable, principal);
        Page<LeaseResponse> leaseResponsePage = page.map(LeaseResponse::from);
        return ResponseEntity.ok(pageAssembler.toModel(leaseResponsePage));
    }

    @PostMapping
    public ResponseEntity<EntityModel<LeaseResponse>> createLease(
            @RequestBody LeaseRequest leaseRequest,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Lease lease = leaseService.createLease(leaseRequest, principal);
        return new ResponseEntity<>(leaseAssembler.toModel(LeaseResponse.from(lease)), HttpStatus.CREATED);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<LeaseResponse>> updateLease(
            @RequestBody LeaseRequest leaseRequest,
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Lease lease = leaseService.getLeaseForUser(requestedId, principal);
        leaseService.updateLease(lease, leaseRequest, principal);
        return ResponseEntity.ok(leaseAssembler.toModel(LeaseResponse.from(lease)));
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<EntityModel<LeaseResponse>> deleteLease(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Lease lease = leaseService.getLeaseForUser(requestedId, principal);
        leaseService.deleteLeaseById(lease.getId());
        return ResponseEntity.noContent().build();
    }
}
