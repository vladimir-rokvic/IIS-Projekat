package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.DistributionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class AidDistributionResponse {
    private Long id;
    private LocalDate scheduledDate;
    private String note;
    private DistributionStatus status;
    private DistributionLocationResponse location;
    private List<VolunteerResponse> volunteers;
    private List<AidPackageResponse> packages;
}