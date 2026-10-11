package com.dem_ams.service;

import com.dem_ams.domain.dto.TenantProfileRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.domain.model.TenantProfile;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.PaymentRepository;
import com.dem_ams.repository.TenantProfileRepository;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class TenantProfileService {
    private final TenantProfileRepository tenantProfileRepository;
    private final UserModelRepository userModelRepository;

    public TenantProfileService(
            TenantProfileRepository tenantProfileRepository,
            UserModelRepository userModelRepository
    ) {
        this.tenantProfileRepository = tenantProfileRepository;
        this.userModelRepository = userModelRepository;
    }

    public Page<TenantProfile> getAllTenantProfiles(Pageable pageable, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return tenantProfileRepository.findAll(
                    PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                    ));
        }
        return null;
    }

    public TenantProfile getTenantProfileByUserId(Long userId) {
        return tenantProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find tenant profile with userId", userId));
    }

    public TenantProfile getTenantProfileById(Long id, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return tenantProfileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Could not find tenant profile with id", id));
        }

        throw new ResourceNotFoundException("Could not find tenant profile with id", id);
    }

    public TenantProfile createTenantProfile(TenantProfileRequest tenantProfileRequest, Long userId) {
        UserModel userModel = userModelRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id", userId));
        
        TenantProfile tenantProfileToCreate = new TenantProfile();
        tenantProfileToCreate.setFirstName(tenantProfileRequest.firstName());
        tenantProfileToCreate.setMiddleName(tenantProfileRequest.middleName());
        tenantProfileToCreate.setLastName(tenantProfileRequest.lastName());
        tenantProfileToCreate.setUser(userModel);
        return tenantProfileRepository.saveAndFlush(tenantProfileToCreate);
    }

    public void updateTenantProfile(TenantProfile tenantProfileToUpdate, TenantProfileRequest tenantProfileRequest) {
        tenantProfileToUpdate.setFirstName(tenantProfileRequest.firstName());
        tenantProfileToUpdate.setMiddleName(tenantProfileRequest.middleName());
        tenantProfileToUpdate.setLastName(tenantProfileRequest.lastName());
        tenantProfileRepository.saveAndFlush(tenantProfileToUpdate);
    }

    public void deleteTenantProfileById(Long id) {
        tenantProfileRepository.deleteById(id);
    }
}
