package com.livinggoodsbackend.livinggoodsbackend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.livinggoodsbackend.livinggoodsbackend.Service.LocationDropdownService;
import com.livinggoodsbackend.livinggoodsbackend.dto.LocationDropdownData;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class LocationController {
    
    @Autowired
    private LocationDropdownService locationService;

    @GetMapping("/dropdowns")
    public ResponseEntity<LocationDropdownData> getAllLocationDropdowns(
        @RequestParam(required = false) Long countyId,
        @RequestParam(required = false) Long subCountyId,
        @RequestParam(required = false) Long wardId,
        @RequestParam(required = false) Long facilityId
    ) {
        return ResponseEntity.ok(locationService.getAllLocationData());
    }
}