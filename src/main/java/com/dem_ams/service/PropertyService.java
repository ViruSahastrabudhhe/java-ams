package com.dem_ams.service;

import com.dem_ams.domain.dto.PropertyRequest;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.LandlordProfile;
import com.dem_ams.domain.model.Property;
import com.dem_ams.repository.LandlordProfileRepository;
import com.dem_ams.repository.PropertyRepository;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public PropertyService(
            PropertyRepository propertyRepository,
            UserModelRepository userModelRepository,
            LandlordProfileRepository landlordProfileRepository) {
        this.propertyRepository = propertyRepository;
        this.userModelRepository = userModelRepository;
        this.landlordProfileRepository = landlordProfileRepository;
    }

    public Property getPropertyForUser(Long id, CustomUserDetails principal) {
        if (principal.isLandlord()) {
            return getPropertyByIdAndLandlordProfileUserId(id, principal.getUser().getId());
        }

        return getPropertyById(id);
    }

    public Property getPropertyByIdAndLandlordProfileUserId(Long id, Long userId) {
        return propertyRepository.findByIdAndLandlordProfileUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Could not find property with id", String.valueOf(id)));
    }

    private Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find property with id", String.valueOf(id)));
    }

    public Page<Property> getPropertiesForUser(Pageable pageable, CustomUserDetails principal) {
        // for guest users who only wish to browse properties
        if (principal == null) {
            return getAllProperties(pageable);
        }
        if (principal.isLandlord()) {
            return getPropertiesByLandlordProfileUserId(principal.getUser().getId(), pageable);
        }

        return getAllProperties(pageable);
    }

    // TODO: could implement a factory for sorts?
    private Page<Property> getAllProperties(Pageable pageable) {
        return propertyRepository.findAll(
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSortOr(Sort.by(Sort.Direction.ASC, "createdAt"))
                ));
    }

    private Page<Property> getPropertiesByLandlordProfileUserId(Long landlordProfileUserId, Pageable pageable) {
        return propertyRepository.findAllByLandlordProfileUserId(landlordProfileUserId,
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSortOr(Sort.by(Sort.Direction.ASC, "createdAt"))
                ));
    }

    public Property createProperty(PropertyRequest propertyRequest, Long userId) {
        LandlordProfile landlordProfile = getLandlordProfileByUserId(userId);

        Property propertyToCreate = new Property();
        propertyToCreate.setLandlordProfile(landlordProfile);
        propertyToCreate.setName(propertyRequest.name());
        propertyToCreate.setPropertyType(propertyRequest.propertyType());
        propertyToCreate.setPropertyValue(propertyRequest.propertyValue());
        propertyToCreate.setBillingType(propertyRequest.billingType());
        propertyToCreate.setSize(propertyRequest.size());
        propertyToCreate.setDescription(propertyRequest.description());
        return propertyRepository.saveAndFlush(propertyToCreate);
    }

    public void updateProperty(Property propertyToUpdate, PropertyRequest propertyRequest) {
        propertyToUpdate.setName(propertyRequest.name());
        propertyToUpdate.setPropertyType(propertyRequest.propertyType());
        propertyToUpdate.setPropertyValue(propertyRequest.propertyValue());
        propertyToUpdate.setBillingType(propertyRequest.billingType());
        propertyToUpdate.setDescription(propertyRequest.description());
        propertyToUpdate.setSize(propertyRequest.size());
        propertyRepository.saveAndFlush(propertyToUpdate);
    }

    private LandlordProfile getLandlordProfileByUserId(Long userId) {
        return landlordProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find landlord profile with userId", String.valueOf(userId)));
    }

    public void deletePropertyById(Long id) {
        propertyRepository.deleteById(id);
    }

    //    private static Sort toSort(Pageable pageable) {
//        /*
//            there's probably a better way to do this, but if you want to sort by more shit e.g.
//            billingType, location, price, then this was the only solution i could think of
//            for this shit twin </3
//        */
//        Sort sort;
//        if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("propertyValueAsc"))) {
//            sort = Sort.by(Sort.Direction.ASC, "propertyDetail.propertyValue");
//        } else if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("propertyValueDesc"))) {
//            sort = Sort.by(Sort.Direction.DESC, "propertyDetail.propertyValue");
//        } else if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("nameAsc"))) {
//            sort = Sort.by(Sort.Direction.ASC, "propertyDetail.name");
//        } else if (pageable.getSort().toString().equals(SORTABLE_FIELDS.get("nameDesc"))) {
//            sort = Sort.by(Sort.Direction.DESC, "propertyDetail.name");
//        } else {
//            sort = Sort.by("createdAt").descending();
//        }
//        return sort;
//    }

}
