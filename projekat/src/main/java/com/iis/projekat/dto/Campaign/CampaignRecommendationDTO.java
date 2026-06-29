package com.iis.projekat.dto.Campaign;

import com.iis.projekat.model.CampaignCategory;

import java.util.List;

public class CampaignRecommendationDTO {

    private CampaignCategory recommendedCategory;

    private double recommendedGoal;

    private int recommendedDurationDays;

    private List<CampaignDTO> referenceCampaigns;

    public CampaignRecommendationDTO(CampaignCategory recommendedCategory, double recommendedGoal, int recommendedDurationDays, List<CampaignDTO> referenceCampaigns) {
        this.recommendedCategory = recommendedCategory;
        this.recommendedGoal = recommendedGoal;
        this.recommendedDurationDays = recommendedDurationDays;
        this.referenceCampaigns = referenceCampaigns;
    }

    public CampaignCategory getRecommendedCategory() {
        return recommendedCategory;
    }

    public double getRecommendedGoal() {
        return recommendedGoal;
    }

    public int getRecommendedDurationDays() {
        return recommendedDurationDays;
    }

    public List<CampaignDTO> getReferenceCampaigns() {
        return referenceCampaigns;
    }

}