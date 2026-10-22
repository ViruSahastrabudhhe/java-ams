package com.dem_ams.controller;

import com.dem_ams.domain.dto.UnitRequest;
import com.dem_ams.domain.dto.UnitResponse;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Unit;
import com.dem_ams.domain.model.assembler.UnitModelAssembler;
import com.dem_ams.service.UnitService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/units")
public class UnitController {

    private static final Logger logger = Logger.getLogger(UnitController.class.getName());

    private final UnitService unitService;
    private final UnitModelAssembler unitAssembler;

    public UnitController(
            UnitService unitService,
            UnitModelAssembler unitAssembler) {
        this.unitService = unitService;
        this.unitAssembler = unitAssembler;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<EntityModel<UnitResponse>> getUnitById(
            @PathVariable("requestedId") Long requestedId,
            @RequestParam String type,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Unit unit = unitService.getUnitForUser(requestedId, type, principal.getUser().getId());
        return ResponseEntity.ok(unitAssembler.toModel(UnitResponse.from(unit, unit.getUnitDetails())));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UnitResponse>> createUnit(
            @RequestBody UnitRequest unitRequest,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Unit unit = unitService.createUnit(unitRequest, principal.getUser().getId());
        return new ResponseEntity<>(unitAssembler.toModel(UnitResponse.from(unit, unit.getUnitDetails())), HttpStatus.CREATED);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<UnitResponse>> updateUnit(
            @RequestBody UnitRequest unitRequest,
            @PathVariable("requestedId") Long requestedId,
            @RequestParam String type,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long landlordProfileUserId = principal.getUser().getId();
        Unit unit = unitService.getUnitForUser(requestedId, type, landlordProfileUserId);
        unitService.updateUnit(unit, unitRequest, landlordProfileUserId);
        return ResponseEntity.ok(unitAssembler.toModel(UnitResponse.from(unit, unit.getUnitDetails())));
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteUnit(
            @PathVariable Long requestedId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Unit unit = unitService.getUnitByIdAndLandlordProfileUserId(requestedId, principal.getUser().getId());
        unitService.deleteUnitById(unit.getId());
        return ResponseEntity.noContent().build();
    }
}
