package com.iis.projekat.dto.Beneficiary;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AidDistributionRequest {
    private LocalDate scheduledDate;
    private String note;
    private Long locationId;
    private List<Long> volunteerIds;
}
