package com.dem_ams.service;

import com.dem_ams.domain.dto.LeaseRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.*;
import com.dem_ams.repository.LeaseRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class LeaseService {

    private static final Logger logger = Logger.getLogger(LeaseService.class.getName());

    private final LeaseRepository leaseRepository;
    private final UnitService unitService;
    private final TenantProfileService tenantProfileService;

    public LeaseService(
            LeaseRepository leaseRepository,
            UnitService unitService,
            TenantProfileService tenantProfileService) {
        this.leaseRepository = leaseRepository;
        this.unitService = unitService;
        this.tenantProfileService = tenantProfileService;
    }

    public Lease getLeaseForUser(Long id, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return getLeaseById(id);
        }
        return getLeaseByIdAndLandlordProfileUserId(id, principal.getUser().getId());
    }

    private Lease getLeaseById(Long id) {
        return leaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find lease with id", String.valueOf(id)));
    }

    private Lease getLeaseByIdAndLandlordProfileUserId(Long id, Long userId) {
        return leaseRepository.findByIdAndLandlordProfileUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find lease with userId", String.valueOf(userId)));
    }

    public Page<Lease> getLeasesForUser(Pageable pageable, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return getAllLeases(pageable);
        }
        return getAllLeasesByLandlordProfileUserId(principal.getUser().getId(), pageable);
    }

    private Page<Lease> getAllLeasesByLandlordProfileUserId(Long userId, Pageable pageable) {
        return leaseRepository.findAllByLandlordProfileUserId(userId,
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSort()
        ));
    }

    private Page<Lease> getAllLeases(Pageable pageable) {
        return leaseRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        ));
    }

    public Lease getLeaseByIdAndTenantProfileUserId(Long id, Long userId) {
        return leaseRepository.findByIdAndTenantProfileUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find lease with id", String.valueOf(id)));
    }

    public Page<Lease> getAllLeasesByTenantProfileUserId(Long userId, Pageable pageable) {
        return leaseRepository.findAllByTenantProfileUserId(userId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()
                ));
    }

    public Lease createLease(LeaseRequest leaseRequest, CustomUserDetails principal) {
        Unit unit = unitService.getUnitByIdAndLandlordProfileUserId(leaseRequest.unitId(), principal.getUser().getId());
        TenantProfile tenantProfile = tenantProfileService.getTenantProfileById(leaseRequest.tenantProfileId(), principal);

        Lease lease = new Lease();
        lease.setUnit(unit);
        return getLease(lease, leaseRequest, tenantProfile);
    }

    public void updateLease(Lease leaseToUpdate, LeaseRequest leaseRequest, CustomUserDetails principal) {
        Unit unit = unitService.getUnitByIdAndLandlordProfileUserId(leaseRequest.unitId(), principal.getUser().getId());
        TenantProfile tenantProfile = tenantProfileService.getTenantProfileById(leaseRequest.tenantProfileId(), principal);

        leaseToUpdate.setUnit(unit);
        getLease(leaseToUpdate, leaseRequest, tenantProfile);
    }

    @NonNull
    private Lease getLease(Lease leaseToUpdate, LeaseRequest leaseRequest, TenantProfile tenantProfile) {
        leaseToUpdate.setStartDate(leaseRequest.startDate());
        leaseToUpdate.setEndDate(leaseRequest.endDate());
        leaseToUpdate.setBillingType(leaseRequest.billingType());
        leaseToUpdate.setRent(leaseRequest.rent());
        leaseToUpdate.setDeposit(leaseRequest.deposit());
        leaseToUpdate.setStatus(leaseRequest.status());
        tenantProfile.addLease(leaseToUpdate);
        return leaseRepository.saveAndFlush(leaseToUpdate);
    }

    public void deleteLeaseById(Long id) { leaseRepository.deleteById(id); }
}
