package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.DistributionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class EfficiencyReportDto {

    private long totalDistributions;

    private Map<DistributionStatus, Long> distributionsByStatus;

    private double avgBeneficiariesPerDistribution;

    private double avgPackagesPerDistribution;

    private Map<String, Long> distributionsByLocation;

    private List<DistributionVolunteerCountDto> volunteersPerDistribution;
}
