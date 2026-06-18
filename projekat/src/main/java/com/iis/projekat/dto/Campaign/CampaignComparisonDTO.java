package com.iis.projekat.dto.Campaign;

public class CampaignComparisonDTO {
    private String campaignName;
    private double raised;
    private double goal;

    public CampaignComparisonDTO(
            String campaignName,
            double raised,
            double goal
    ) {
        this.campaignName = campaignName;
        this.raised = raised;
        this.goal = goal;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public double getRaised() {
        return raised;
    }

    public double getGoal() {
        return goal;
    }
}