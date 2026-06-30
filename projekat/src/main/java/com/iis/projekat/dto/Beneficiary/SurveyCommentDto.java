package com.iis.projekat.dto.Beneficiary;

public record SurveyCommentDto(
        Long distributionId,
        String comment,
        double rating
) {}
