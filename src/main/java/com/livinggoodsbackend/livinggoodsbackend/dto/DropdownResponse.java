package com.livinggoodsbackend.livinggoodsbackend.dto;

import java.util.Collections;
import java.util.List;

public class DropdownResponse {
    private Long id;
    private String name;
    private List<Long> parentIds;

    // For items with no parent
    public DropdownResponse(Long id, String name) {
        this.id = id;
        this.name = name;
        this.parentIds = Collections.emptyList();
    }

    // For items with a single parent
    public DropdownResponse(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentIds = Collections.singletonList(parentId);
    }

    // For items with multiple parents
    public DropdownResponse(Long id, String name, List<Long> parentIds) {
        this.id = id;
        this.name = name;
        this.parentIds = parentIds;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Long> getParentIds() { return parentIds; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setParentIds(List<Long> parentIds) { this.parentIds = parentIds; }
}