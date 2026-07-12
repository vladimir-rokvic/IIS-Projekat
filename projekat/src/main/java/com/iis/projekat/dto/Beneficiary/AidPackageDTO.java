package com.iis.projekat.dto.Beneficiary;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AidPackageDTO {

    private Long beneficiaryId;

    private List<PackageItemDTO> items;

    private Long distributionId;

    public AidPackageDTO() {
    }

}