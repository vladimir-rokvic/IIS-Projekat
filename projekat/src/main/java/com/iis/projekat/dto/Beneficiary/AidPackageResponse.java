package com.iis.projekat.dto.Beneficiary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AidPackageResponse {
    private Long id;
    private Long beneficiaryId;
    private String beneficiaryName;
    private List<PackageItemResponse> items;
}
