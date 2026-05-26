package com.iis.projekat.dto;

import java.util.List;

public class AidPackageDTO {

    private Long beneficiaryId;

    private List<PackageItemDTO> items;

    public AidPackageDTO() {
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public List<PackageItemDTO> getItems() {
        return items;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public void setItems(List<PackageItemDTO> items) {
        this.items = items;
    }
}