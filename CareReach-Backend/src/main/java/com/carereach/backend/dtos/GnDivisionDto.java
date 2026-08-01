package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class GnDivisionDto {
    private Long id;
    private String name;
    private Long dsId;
    private String dsName;
    private Long districtId;
    private String districtName;
}
