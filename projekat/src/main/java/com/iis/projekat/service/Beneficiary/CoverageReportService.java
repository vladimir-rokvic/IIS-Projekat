package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.CoverageReportDto;
import com.iis.projekat.model.Beneficiary.AidType;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import com.iis.projekat.repository.Beneficiary.AidDistributionRepository;
import com.iis.projekat.repository.Beneficiary.AidPackageRepository;
import com.iis.projekat.repository.Beneficiary.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoverageReportService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AidPackageRepository aidPackageRepository;
    private final AidDistributionRepository distributionRepository;

    public CoverageReportDto generate(String period) {

        LocalDate end = LocalDate.now();
        LocalDate start = resolveStartDate(period, end);

        long totalEligible =
                beneficiaryRepository.countByEligibleTrue();

        long receivedAid =
                aidPackageRepository.countBeneficiariesReceivedAid(start, end);

        double coverage =
                totalEligible == 0
                        ? 0
                        : ((double) receivedAid / totalEligible) * 100.0;

        Map<AidType, Long> aidTypeBreakdown =
                new EnumMap<>(AidType.class);

        for (Object[] row : aidPackageRepository.countByAidType(start, end)) {
            aidTypeBreakdown.put(
                    (AidType) row[0],
                    (Long) row[1]
            );
        }

        Map<DistributionStatus, Long> distributionsByStatus =
                new EnumMap<>(DistributionStatus.class);

        for (Object[] row : distributionRepository.countByStatus(start,end.plusYears(1))) {
            distributionsByStatus.put(
                    (DistributionStatus) row[0],
                    (Long) row[1]
            );
        }

        return CoverageReportDto.builder()
                .period(period)
                .totalEligibleBeneficiaries(totalEligible)
                .beneficiariesReceivedAid(receivedAid)
                .coveragePercentage(coverage)
                .aidTypeBreakdown(aidTypeBreakdown)
                .distributionsByStatus(distributionsByStatus)
                .build();
    }

    private LocalDate resolveStartDate(String period, LocalDate end) {

        return switch (period.toLowerCase()) {
            case "week" -> end.minusWeeks(1);
            case "month" -> end.minusMonths(1);
            case "year" -> end.minusYears(1);
            default -> throw new IllegalArgumentException("Invalid period");
        };
    }
}


