package com.iis.projekat.dto.Beneficiary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PackageItemResponse {
    private Long id;
    private String product;
    private Float quantity;
    private String unit;
    private String description;
}