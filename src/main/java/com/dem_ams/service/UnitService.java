package com.dem_ams.service;

import com.dem_ams.domain.dto.*;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.*;
import com.dem_ams.domain.model.enums.UnitType;
import com.dem_ams.repository.ResidentialUnitRepository;
import com.dem_ams.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class UnitService {

    private static final Logger logger = Logger.getLogger(UnitService.class.getName());

    private final UnitRepository unitRepository;
    private final ResidentialUnitRepository residentialUnitRepository;
    private final PropertyService propertyService;

    public UnitService(UnitRepository unitRepository,
                       ResidentialUnitRepository residentialUnitRepository,
                       PropertyService propertyService) {
        this.unitRepository = unitRepository;
        this.residentialUnitRepository = residentialUnitRepository;
        this.propertyService = propertyService;
    }

    public Unit getUnitForUser(Long id, String type, Long userId) {
        Unit unit = getUnitByIdAndLandlordProfileUserId(id, userId);
        IUnitDetails unitDetails = getUnitDetailsByUnitId(unit.getId(), type);
        unit.setUnitDetails(unitDetails);
        return unit;
    }

    public Unit getUnitByIdAndLandlordProfileUserId(Long id, Long userId) {
        return unitRepository.findByIdAndLandlordProfileUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find unit with id", String.valueOf(id)));
    }

    private ResidentialUnit getResidentialUnitByUnitId(Long unitId) {
        return residentialUnitRepository.findByUnitId(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find residential unit with unitId", String.valueOf(unitId)));
    }

    private CommercialUnit getCommercialUnitByUnitId(Long unitId) {
        return new CommercialUnit();
    }

    public IUnitDetails getUnitDetailsByUnitId(Long unitId, String type) {
        UnitType unitType = UnitType.valueOf(type.toUpperCase());

        return switch (unitType) {
            case RESIDENTIAL -> ResidentialUnitDetails.from(getResidentialUnitByUnitId(unitId));
            case COMMERCIAL -> CommercialUnitDetails.from(getCommercialUnitByUnitId(unitId));
        };
    }

    @Transactional
    public Unit createUnit(UnitRequest unitRequest, Long landlordProfileUserId) {
        Property property = propertyService.getPropertyByIdAndLandlordProfileUserId(unitRequest.propertyId(), landlordProfileUserId);
        Unit unitToCreate = new Unit();
        return toUnit(unitToCreate, unitRequest, property);
    }

    @Transactional
    public void updateUnit(Unit unitToUpdate, UnitRequest unitRequest, Long landlordProfileUserId) {
        Property property = propertyService.getPropertyByIdAndLandlordProfileUserId(unitRequest.propertyId(), landlordProfileUserId);
        toUnit(unitToUpdate, unitRequest, property);
    }

    @Transactional
    public void deleteUnitById(Long id) { unitRepository.deleteById(id); }

    private Unit toUnit(Unit unit, UnitRequest unitRequest, Property property) {
        if (unitRequest.unitType().equals(UnitType.RESIDENTIAL)) {
            ResidentialUnit residentialUnit = createResidentialUnit(unitRequest);
            ResidentialUnitDetails residentialUnitDetails = ResidentialUnitDetails.from(residentialUnit);
            residentialUnit.setUnit(unit);
            unit.setResidentialUnit(residentialUnit);
            unit.setProperty(property);
            unit.setUnitType(unitRequest.unitType());
            unit.setBaseRent(unitRequest.baseRent());
            unit.setSquareMeters(unitRequest.squareMeters());
            unit.setCurrency(unitRequest.currency());
            unit.setStatus(unitRequest.status());
            unit.setUnitDetails(residentialUnitDetails);
        }
        return unitRepository.saveAndFlush(unit);
    }

    private ResidentialUnit createResidentialUnit(UnitRequest unitRequest) {
        ResidentialUnit residentialUnit = new ResidentialUnit();
        ResidentialUnitDetails unitDetails = (ResidentialUnitDetails) unitRequest.unitDetails();
        residentialUnit.setUnitNumber(unitDetails.unit_number());
        residentialUnit.setFloor(unitDetails.unit_floor());
        residentialUnit.setBedroomQuantity(unitDetails.bedroom_quantity());
        residentialUnit.setBathroomQuantity(unitDetails.bathroom_quantity());
        return residentialUnit;
    }
}
