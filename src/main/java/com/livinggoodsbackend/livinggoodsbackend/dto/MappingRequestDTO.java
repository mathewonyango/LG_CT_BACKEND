package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingRequestDTO {
    private Long chaId;
    private Long chpId;
    private Long communityUnitId;
}
