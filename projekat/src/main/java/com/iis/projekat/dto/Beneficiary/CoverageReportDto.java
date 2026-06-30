package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidType;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoverageReportDto {

    private String period;

    private long totalEligibleBeneficiaries;

    private long beneficiariesReceivedAid;

    private double coveragePercentage;

    private Map<AidType, Long> aidTypeBreakdown;

    private Map<DistributionStatus, Long> distributionsByStatus;
}


