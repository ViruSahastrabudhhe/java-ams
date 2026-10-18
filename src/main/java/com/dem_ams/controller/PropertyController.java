package com.dem_ams.controller;

import com.dem_ams.domain.dto.PropertyRequest;
import com.dem_ams.domain.dto.PropertyResponse;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Property;
import com.dem_ams.domain.model.assembler.PropertyModelAssembler;
import com.dem_ams.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private static final Logger logger = Logger.getLogger(PropertyController.class.getName());

    private final PropertyService propertyService;
    private final PropertyModelAssembler assembler;

    public PropertyController(
            PropertyService propertyService,
            PropertyModelAssembler assembler
    ) {
        this.propertyService = propertyService;
        this.assembler = assembler;
    }

    /*
        TODO: what if a User has both TENANT and LANDLORD roles,
         how will they be able to view other properties in the listing website as a tenant?
     */
    @GetMapping("/{requestedId}")
    public ResponseEntity<EntityModel<PropertyResponse>> getPropertyById(
            @PathVariable("requestedId") Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Property property = propertyService.getPropertyForUser(requestedId, principal);
        return ResponseEntity.ok(assembler.toModel(PropertyResponse.from(property)));
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PropertyResponse>>> getAllProperties(
            Pageable pageable,
            PagedResourcesAssembler<PropertyResponse> pageAssembler,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Page<Property> page = propertyService.getPropertiesForUser(pageable, principal);
        Page<PropertyResponse> propertyResponsePage = page.map(PropertyResponse::from);
        return ResponseEntity.ok(pageAssembler.toModel(propertyResponsePage));
    }

    @PostMapping
    public ResponseEntity<EntityModel<PropertyResponse>> createProperty(
            @RequestBody PropertyRequest propertyRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Property property = propertyService.createProperty(propertyRequest, principal.getUser().getId());
        return new ResponseEntity<>(assembler.toModel(PropertyResponse.from(property)), HttpStatus.CREATED);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<PropertyResponse>> updateProperty(
            @PathVariable("requestedId") Long requestedId,
            @RequestBody PropertyRequest propertyRequest,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Property property = propertyService.getPropertyForUser(requestedId, principal);
        propertyService.updateProperty(property, propertyRequest);
        return ResponseEntity.ok(assembler.toModel(PropertyResponse.from(property)));
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<EntityModel<PropertyResponse>> deleteProperty(
            @PathVariable("requestedId") Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Property property = propertyService.getPropertyForUser(requestedId, principal);
        propertyService.deletePropertyById(property.getId());
        return ResponseEntity.noContent().build();
    }
}
