package com.iis.projekat.dto.Beneficiary;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AidPackageRequest {
    private Long beneficiaryId;
    private List<PackageItemRequest> items; // max 3, validacija u servisu
}