package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BeneficiaryDetailsResponse {
    private Long id;
    private String name;
    private String surname;
    private AidType aidType;
    private boolean eligible;
    private String country;
    private String city;
    private String street;
    private LocalDate dateOfBirth;
}
