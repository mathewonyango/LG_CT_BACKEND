package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Model.ManagerCountyMapping;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.Repository.ManagerCountyMappingRepository;
import com.livinggoodsbackend.livinggoodsbackend.dto.CountyDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ManagerCountyMappingRequestDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.ManagerCountyMappingResponseDTO;
import com.livinggoodsbackend.livinggoodsbackend.dto.UserDTO;
import com.livinggoodsbackend.livinggoodsbackend.exception.ResourceNotFoundException;
import com.livinggoodsbackend.livinggoodsbackend.Service.UserService;
import com.livinggoodsbackend.livinggoodsbackend.Service.CountyService;
 import com.livinggoodsbackend.livinggoodsbackend.Service.KafkaProducerService;

import org.springframework.stereotype.Service;
@Service
public class ManagerCountyMappingService {

    @Autowired
    private ManagerCountyMappingRepository repository;

     @Autowired
    private UserService UserService;

    @Autowired
    private CountyService CountyService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public ManagerCountyMappingResponseDTO mapManagerToCounty(ManagerCountyMappingRequestDTO dto) {
        ManagerCountyMapping mapping = new ManagerCountyMapping();
        mapping.setManagerId(dto.getManagerId());
        mapping.setCountyId(dto.getCountyId());
        ManagerCountyMapping saved = repository.save(mapping);

        ManagerCountyMappingResponseDTO response = new ManagerCountyMappingResponseDTO();
        response.setId(saved.getId());
        response.setManagerId(saved.getManagerId());
        response.setCountyId(saved.getCountyId());

        kafkaProducerService.sendMessage("manager_county_mappings", String.valueOf(saved.getId()) , response);
        return response;
    }


public List<ManagerCountyMappingResponseDTO> getMappingsByManager(Long managerId) {
    return repository.findByManagerId(managerId).stream().map(mapping -> {
        ManagerCountyMappingResponseDTO dto = new ManagerCountyMappingResponseDTO();
        dto.setId(mapping.getId());
        dto.setManagerId(mapping.getManagerId());
        dto.setCountyId(mapping.getCountyId());

        // Fetch manager username
        UserService.getUserById(mapping.getManagerId())
            .ifPresentOrElse(
                user -> dto.setManagerName(user.getUsername()),
                () -> {
                    throw new ResourceNotFoundException("Manager not found with ID: " + mapping.getManagerId());
                }
            );

        // Fetch county name
        CountyService.getCountyById(mapping.getCountyId())
            .ifPresentOrElse(
                county -> dto.setCountyName(county.getName()),
                () -> {
                    throw new ResourceNotFoundException("County not found with ID: " + mapping.getCountyId());
                }
            );
        kafkaProducerService.sendMessage("manager_county_mappings", String.valueOf(mapping.getId()) , dto);
        return dto;
    }).collect(Collectors.toList());
}


public List<ManagerCountyMappingResponseDTO> getAllMappings() {
    return repository.findAll().stream().map(mapping -> {
        ManagerCountyMappingResponseDTO dto = new ManagerCountyMappingResponseDTO();
        dto.setId(mapping.getId());
        dto.setManagerId(mapping.getManagerId());
        dto.setCountyId(mapping.getCountyId());

        // Fetch manager username
        UserService.getUserById(mapping.getManagerId())
            .ifPresentOrElse(
                user -> dto.setManagerName(user.getUsername()),
                () -> {
                    throw new ResourceNotFoundException("Manager not found with ID: " + mapping.getManagerId());
                }
            );

        // Fetch county name
        CountyService.getCountyById(mapping.getCountyId())
            .ifPresentOrElse(
                county -> dto.setCountyName(county.getName()),
                () -> {
                    throw new ResourceNotFoundException("County not found with ID: " + mapping.getCountyId());
                }
            );
        kafkaProducerService.sendMessage("manager_county_mappings", String.valueOf(mapping.getId()) , dto);
        return dto;
    }).collect(Collectors.toList());
}



}
