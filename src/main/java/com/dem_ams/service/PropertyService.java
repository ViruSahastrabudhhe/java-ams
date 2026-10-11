package com.dem_ams.service;

import com.dem_ams.controller.PropertyController;
import com.dem_ams.domain.dto.PropertyRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.LandlordProfile;
import com.dem_ams.domain.model.Property;
import com.dem_ams.domain.model.PropertyDetail;
import com.dem_ams.repository.LandlordProfileRepository;
import com.dem_ams.repository.PropertyDetailRepository;
import com.dem_ams.repository.PropertyRepository;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class PropertyService {

    private static final Logger logger = Logger.getLogger(PropertyService.class.getName());

    private final PropertyRepository propertyRepository;
    private final UserModelRepository userModelRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private static final Map<String, String> SORTABLE_FIELDS = Map.of(
            "propertyValueAsc", "propertyValue: ASC",
            "propertyValueDesc", "propertyValue: DESC",
            "nameAsc", "name: ASC",
            "nameDesc", "name: DESC"
    );
    private final PropertyDetailRepository propertyDetailRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            UserModelRepository userModelRepository,
            LandlordProfileRepository landlordProfileRepository,
            PropertyDetailRepository propertyDetailRepository) {
        this.propertyRepository = propertyRepository;
        this.userModelRepository = userModelRepository;
        this.landlordProfileRepository = landlordProfileRepository;
        this.propertyDetailRepository = propertyDetailRepository;
    }

    public Property getPropertyForUser(Long id, CustomUserDetails principal) {
        if (principal.isLandlord()) {
            return getPropertyByIdAndLandlordProfileUserId(id, principal.getId());
        }

        return getPropertyById(id);
    }

    private Property getPropertyByIdAndLandlordProfileUserId(Long id, Long userId) {
        return propertyRepository.findByIdAndLandlordProfileUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Could not find property with id", id));
    }

    private Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find property with id", id));
    }

    public Page<Property> getPropertiesForUser(Pageable pageable, CustomUserDetails principal) {
        if (principal.isLandlord()) {
            return getPropertiesByLandlordProfileUserId(principal.getId(), pageable);
        }

        return getAllProperties(pageable);
    }

    // TODO: could implement a factory for sorts?
    private Page<Property> getAllProperties(Pageable pageable) {
        return propertyRepository.findAll(
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    toSort(pageable)
                ));
    }

    private Page<Property> getPropertiesByLandlordProfileUserId(Long landlordProfileUserId, Pageable pageable) {
        return propertyRepository.findAllByLandlordProfileUserId(landlordProfileUserId,
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    toSort(pageable)
                ));
    }

    public Property createProperty(PropertyRequest propertyRequest, Long userId) {
        LandlordProfile landlordProfile = getLandlordProfileByUserId(userId);

        Property propertyToCreate = new Property();
        propertyToCreate.setLandlordProfile(landlordProfile);

        PropertyDetail propertyDetailToCreate = new PropertyDetail();
        propertyDetailToCreate.setName(propertyRequest.name());
        propertyDetailToCreate.setPropertyType(propertyRequest.propertyType());
        propertyDetailToCreate.setPropertyValue(propertyRequest.propertyValue());
        propertyDetailToCreate.setBillingType(propertyRequest.billingType());
        propertyDetailToCreate.setBedroomQuantity(propertyRequest.bedroomQuantity());
        propertyDetailToCreate.setBathroomQuantity(propertyRequest.bathroomQuantity());
        propertyDetailToCreate.setSize(propertyRequest.size());
        propertyDetailToCreate.setDescription(propertyRequest.description());
        propertyDetailToCreate.setProperty(propertyToCreate);
        propertyToCreate.setPropertyDetail(propertyDetailToCreate);

        return propertyRepository.saveAndFlush(propertyToCreate);
    }

    public void updateProperty(Property propertyToUpdate, PropertyRequest propertyRequest) {
        propertyToUpdate.getPropertyDetail().setName(propertyRequest.name());
        propertyToUpdate.getPropertyDetail().setPropertyType(propertyRequest.propertyType());
        propertyToUpdate.getPropertyDetail().setPropertyValue(propertyRequest.propertyValue());
        propertyToUpdate.getPropertyDetail().setBillingType(propertyRequest.billingType());
        propertyToUpdate.getPropertyDetail().setBedroomQuantity(propertyRequest.bedroomQuantity());
        propertyToUpdate.getPropertyDetail().setBathroomQuantity(propertyRequest.bathroomQuantity());
        propertyToUpdate.getPropertyDetail().setDescription(propertyRequest.description());
        propertyToUpdate.getPropertyDetail().setSize(propertyRequest.size());
        propertyRepository.saveAndFlush(propertyToUpdate);
    }

    private LandlordProfile getLandlordProfileByUserId(Long userId) {
        return landlordProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find landlord profile with userId", userId));
    }

    public void deletePropertyById(Long id) {
        propertyRepository.deleteById(id);
    }

    private static Sort toSort(Pageable pageable) {
        /*
            there's probably a better way to do this, but if you want to sort by more shit e.g.
            billingType, location, price, then this was the only solution i could think of
            for this shit twin </3
        */
        Sort sort;
        if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("propertyValueAsc"))) {
            sort = Sort.by(Sort.Direction.ASC, "propertyDetail.propertyValue");
        } else if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("propertyValueDesc"))) {
            sort = Sort.by(Sort.Direction.DESC, "propertyDetail.propertyValue");
        } else if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("nameAsc"))) {
            sort = Sort.by(Sort.Direction.ASC, "propertyDetail.name");
        } else if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("nameDesc"))) {
            sort = Sort.by(Sort.Direction.DESC, "propertyDetail.name");
        } else {
            sort = Sort.by("createdAt").descending();
        }
        return sort;
    }

}
