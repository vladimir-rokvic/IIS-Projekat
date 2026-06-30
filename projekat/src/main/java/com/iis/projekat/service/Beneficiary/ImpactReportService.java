package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.ImpactReportDto;
import com.iis.projekat.dto.Beneficiary.SurveyCommentDto;
import com.iis.projekat.model.Beneficiary.AidType;
import com.iis.projekat.repository.Beneficiary.AidPackageRepository;
import com.iis.projekat.repository.Beneficiary.BeneficiarySurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpactReportService {

    private final BeneficiarySurveyRepository surveyRepo;
    private final AidPackageRepository packageRepo;

    public ImpactReportDto generate() {

        long totalSurveys = surveyRepo.countSurveys();
        long totalPackages = packageRepo.countAllPackages();

        double responseRate =
                totalPackages == 0
                        ? 0
                        : (double) totalSurveys / totalPackages * 100;

        double avgRating =
                Optional.ofNullable(surveyRepo.averageRating())
                        .orElse(0.0);

        Map<Long, Double> avgPerDistribution =
                surveyRepo.averageRatingPerDistribution()
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (Double) row[1]
                        ));

        Map<Integer, Long> ratingDistribution =
                surveyRepo.ratingDistribution()
                        .stream()
                        .collect(Collectors.toMap(
                                row -> ((Number) row[0]).intValue(),
                                row -> (Long) row[1]
                        ));

        Map<AidType, Double> avgPerAidType =
                surveyRepo.averageRatingPerAidType()
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (AidType) row[0],
                                row -> (Double) row[1]
                        ));

        List<SurveyCommentDto> latestComments =
                surveyRepo.latestComments(PageRequest.of(0, 10));

        return ImpactReportDto.builder()
                .totalSurveys(totalSurveys)
                .totalPackages(totalPackages)
                .responseRate(responseRate)
                .averageRating(avgRating)
                .averageRatingPerDistribution(avgPerDistribution)
                .ratingDistribution(ratingDistribution)
                .averageRatingPerAidType(avgPerAidType)
                .latestComments(latestComments)
                .build();
    }
}