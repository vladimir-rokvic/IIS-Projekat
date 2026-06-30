package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ImpactReportDto {

    private long totalSurveys;
    private long totalPackages;

    private double responseRate;

    private double averageRating;

    private Map<Long, Double> averageRatingPerDistribution;

    private Map<Integer, Long> ratingDistribution; // 1-5

    private List<SurveyCommentDto> latestComments;

    private Map<AidType, Double> averageRatingPerAidType;
}
