package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingResponseDTO {
    private Long id;
    private Long chaId;
    private Long chpId;
    private Long communityUnitId;
}