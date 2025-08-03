package com.livinggoodsbackend.livinggoodsbackend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.livinggoodsbackend.livinggoodsbackend.Service.ManagerCountyMappingService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ManagerCountyMappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ManagerCountyMappingResponseDTO;

@RestController
@RequestMapping("/api/manager-county-mapping")
public class ManagerCountyMappingController {

    @Autowired
    private ManagerCountyMappingService service;


    @PostMapping
    public ManagerCountyMappingResponseDTO mapManagerToCounty(@RequestBody ManagerCountyMappingRequestDTO dto) {
        return service.mapManagerToCounty(dto);
    }

    @GetMapping("/{managerId}")
    public List<ManagerCountyMappingResponseDTO> getMappingsByManager(@PathVariable Long managerId) {
        return service.getMappingsByManager(managerId);
    }
    @GetMapping("/managers")
    public ResponseEntity<List<ManagerCountyMappingResponseDTO>> getAllManagerCountyMappings() {
        return ResponseEntity.ok(service.getAllMappings());
    }


}
