package com.livinggoodsbackend.livinggoodsbackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.livinggoodsbackend.livinggoodsbackend.Repository.*;
import com.livinggoodsbackend.livinggoodsbackend.dto.*;
import com.livinggoodsbackend.livinggoodsbackend.Model.Ward;
import java.util.stream.Collectors;
import java.util.List; // <-- import statement added

@Service
public class LocationDropdownService {
    @Autowired private CountyRepository countyRepository;
    @Autowired private SubCountyRepository subCountyRepository;
    @Autowired private WardRepository wardRepository;
    @Autowired private FacilityRepository facilityRepository;
    @Autowired private CommodityUnitRepository communityUnitRepository;

    public LocationDropdownData getAllLocationData() {
        LocationDropdownData data = new LocationDropdownData();

        // Counties don't have parent ID
        data.setCounties(
            countyRepository.findAll().stream()
                .map(county -> new DropdownResponse(county.getId(), county.getName()))
                .collect(Collectors.toList())
        );

        // SubCounties include county ID as parent
        data.setSubCounties(
            subCountyRepository.findAll().stream()
                .filter(subCounty -> subCounty.getCounty() != null)
                .map(subCounty -> new DropdownResponse(
                    subCounty.getId(),
                    subCounty.getName(),
                    subCounty.getCounty().getId()))
                .collect(Collectors.toList())
        );

        // Wards include subCounty ID as parent
        data.setWards(
            wardRepository.findAll().stream()
                .filter(ward -> ward.getSubCounty() != null)
                .map(ward -> new DropdownResponse(
                    ward.getId(),
                    ward.getName(),
                    ward.getSubCounty().getId()))
                .collect(Collectors.toList())
        );

        // Facilities include ward IDs as parentIds (list)
        data.setFacilities(
            facilityRepository.findAll().stream()
                .map(facility -> new DropdownResponse(
                    facility.getId(),
                    facility.getName(),
                    facility.getWards().stream()
                        .map(Ward::getId)
                        .collect(Collectors.toList())
                ))
                .collect(Collectors.toList())
        );

        return data;
    }
}
