package com.iis.projekat.dto;

import java.time.LocalDate;

public class NeedsReassessmentRequestDTO {
    private Long beneficiaryId;

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }
}
