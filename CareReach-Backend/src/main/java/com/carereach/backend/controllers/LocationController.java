package com.carereach.backend.controllers;

import com.carereach.backend.models.District;
import com.carereach.backend.models.DivisionalSecretariat;
import com.carereach.backend.models.GramaNiladhariDivision;
import com.carereach.backend.dtos.GnDivisionDto;
import com.carereach.backend.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/districts")
    public ResponseEntity<List<District>> getAllDistricts() {
        return ResponseEntity.ok(locationService.getAllDistricts());
    }

    @GetMapping("/districts/{districtId}/divisional-secretariats")
    public ResponseEntity<List<DivisionalSecretariat>> getDsByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(locationService.getDsByDistrict(districtId));
    }

    @GetMapping("/divisional-secretariats/{dsId}/divisions")
    public ResponseEntity<List<GramaNiladhariDivision>> getGnDivisionsByDs(@PathVariable Long dsId) {
        return ResponseEntity.ok(locationService.getGnDivisionsByDs(dsId));
    }

    @GetMapping("/divisions")
    public ResponseEntity<List<GnDivisionDto>> getAllGnDivisionsWithParents() {
        return ResponseEntity.ok(locationService.getAllGnDivisionsWithParents());
    }

    @GetMapping("/divisional-secretariats")
    public ResponseEntity<List<com.carereach.backend.dtos.DsDto>> getAllDivisionalSecretariats() {
        return ResponseEntity.ok(locationService.getAllDivisionalSecretariats());
    }
}
