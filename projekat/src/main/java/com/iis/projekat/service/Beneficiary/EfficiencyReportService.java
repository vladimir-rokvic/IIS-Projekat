package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.*;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import com.iis.projekat.repository.Beneficiary.AidDistributionRepository;
import com.iis.projekat.repository.Beneficiary.AidPackageRepository;
import com.iis.projekat.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EfficiencyReportService {

    private final AidDistributionRepository distributionRepository;
    private final AidPackageRepository packageRepository;
    private final VolunteerRepository volunteerRepository;

    public EfficiencyReportDto generate() {

        long totalDistributions = distributionRepository.totalDistributions();

        Map<DistributionStatus, Long> statusBreakdown =
                distributionRepository.countByStatus()
                        .stream()
                        .collect(Collectors.toMap(
                                StatusCountDto::status,
                                StatusCountDto::count
                        ));

        Map<String, Long> locationBreakdown =
                distributionRepository.countByLocation()
                        .stream()
                        .collect(Collectors.toMap(
                                LocationCountDto::locationName,
                                LocationCountDto::count
                        ));

        // =========================
        // PACKAGES PER DISTRIBUTION
        // =========================
        List<Long> packagesPerDistribution =
                packageRepository.countPerDistribution();

        double avgPackagesPerDistribution =
                packagesPerDistribution.stream()
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0.0);

        // =========================
        // BENEFICIARIES PER DISTRIBUTION
        // =========================
        List<Long> beneficiariesPerDistribution =
                packageRepository.countDistinctBeneficiariesPerDistribution();

        double avgBeneficiariesPerDistribution =
                beneficiariesPerDistribution.stream()
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0.0);

        // =========================
        // VOLUNTEERS PER DISTRIBUTION
        // =========================
        List<DistributionVolunteerCountDto> volunteersPerDistribution =
                volunteerRepository.countVolunteersPerDistribution();

        // =========================
        // BUILD DTO
        // =========================
        return EfficiencyReportDto.builder()
                .totalDistributions(totalDistributions)
                .distributionsByStatus(statusBreakdown)
                .distributionsByLocation(locationBreakdown)
                .avgPackagesPerDistribution(avgPackagesPerDistribution)
                .avgBeneficiariesPerDistribution(avgBeneficiariesPerDistribution)
                .volunteersPerDistribution(volunteersPerDistribution)
                .build();
    }
}
