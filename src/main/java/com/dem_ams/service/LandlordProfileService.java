package com.dem_ams.service;

import com.dem_ams.domain.dto.LandlordProfileRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.LandlordProfile;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.LandlordProfileRepository;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LandlordProfileService {
    private final LandlordProfileRepository landlordProfileRepository;
    private final UserModelRepository userModelRepository;

    public LandlordProfileService(
            LandlordProfileRepository landlordProfileRepository,
            UserModelRepository userModelRepository
    ) {
        this.landlordProfileRepository = landlordProfileRepository;
        this.userModelRepository = userModelRepository;
    }

    public Page<LandlordProfile> getAllLandlordProfiles(Pageable pageable) {
        return landlordProfileRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
    }

    public LandlordProfile getLandlordProfileByUserId(Long userId) {
        return landlordProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find landlord profile with userId", userId));
    }

    public LandlordProfile getLandlordProfileById(Long id, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return landlordProfileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Could not find landlord profile with id", id));
        }

        throw new ResourceNotFoundException("Could not find tenant profile with id", id);
    }

    public LandlordProfile createLandlordProfile(LandlordProfileRequest landlordProfileRequest, Long userId) {
        UserModel userModel = userModelRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id", userId));

        LandlordProfile landlordProfileToCreate = new LandlordProfile();
        landlordProfileToCreate.setFirstName(landlordProfileRequest.firstName());
        landlordProfileToCreate.setMiddleName(landlordProfileRequest.middleName());
        landlordProfileToCreate.setLastName(landlordProfileRequest.lastName());
        landlordProfileToCreate.setUser(userModel);
        return landlordProfileRepository.saveAndFlush(landlordProfileToCreate);
    }

    public void updateLandlordProfile(LandlordProfile landlordProfileToUpdate, LandlordProfileRequest landlordProfileRequest) {
        landlordProfileToUpdate.setFirstName(landlordProfileRequest.firstName());
        landlordProfileToUpdate.setMiddleName(landlordProfileRequest.middleName());
        landlordProfileToUpdate.setLastName(landlordProfileRequest.lastName());
        landlordProfileRepository.saveAndFlush(landlordProfileToUpdate);
    }

    public void deleteLandlordProfileById(Long id) {
        landlordProfileRepository.deleteById(id);
    }
}
