package com.iis.projekat.dto.Beneficiary;

import java.time.LocalDate;

public record DistributionVolunteerCountDto(
        Long distributionId,
        String note,
        LocalDate scheduledDate,
        Long volunteerCount
) {}
