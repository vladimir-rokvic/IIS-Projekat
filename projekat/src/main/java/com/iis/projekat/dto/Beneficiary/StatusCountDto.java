package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.DistributionStatus;

public record StatusCountDto(
        DistributionStatus status,
        long count
) {}
