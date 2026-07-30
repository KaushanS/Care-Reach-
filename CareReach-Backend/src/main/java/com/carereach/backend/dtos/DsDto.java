package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class DsDto {
    private Long id;
    private String name;
    private Long districtId;
    private String districtName;
}
