package com.iis.projekat.dto.Beneficiary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageItemRequest {
    private String product;
    private Float quantity;
    private String unit;
    private String description;
}
