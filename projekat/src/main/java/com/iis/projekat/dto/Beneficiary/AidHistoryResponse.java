package com.iis.projekat.dto.Beneficiary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class AidHistoryResponse {
    private Long beneficiaryId;
    private String beneficiaryName;
    private LocalDate dateReceived;
    private List<PackageItemResponse> items;
}
