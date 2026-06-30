package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BeneficiaryPackageResponse {

    private Long id;
    private String name;
    private String surname;
    private AidType aidType;
    private boolean eligible;
}
