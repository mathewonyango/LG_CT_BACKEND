package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;

@Data
public class ManagerCountyMappingResponseDTO {
private Long id;
private Long managerId;
private String managerName;
private Long countyId;
private String countyName;


}
