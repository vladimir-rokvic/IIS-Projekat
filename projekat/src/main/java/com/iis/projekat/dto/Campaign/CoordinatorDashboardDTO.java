package com.iis.projekat.dto.Campaign;

import java.util.List;

public class CoordinatorDashboardDTO {
    long totalActiveCampaigns;
    double totalRaisedAmount;
    long totalFinishedCampaigns;
    long totalDonors;
    List<CampaignDTO> topPerformingCampaigns;
    List<String> recentActivity;

    public CoordinatorDashboardDTO(long totalActiveCampaigns, double totalRaisedAmount, long totalFinishedCampaigns, long totalDonors, List<CampaignDTO> topPerformingCampaigns, List<String> recentActivity) {
        this.totalActiveCampaigns = totalActiveCampaigns;
        this.totalRaisedAmount = totalRaisedAmount;
        this.totalFinishedCampaigns = totalFinishedCampaigns;
        this.totalDonors = totalDonors;
        this.topPerformingCampaigns = topPerformingCampaigns;
        this.recentActivity = recentActivity;
    }

    public long getTotalActiveCampaigns() {
        return totalActiveCampaigns;
    }

    public void setTotalActiveCampaigns(long totalActiveCampaigns) {
        this.totalActiveCampaigns = totalActiveCampaigns;
    }

    public double getTotalRaisedAmount() {
        return totalRaisedAmount;
    }

    public void setTotalRaisedAmount(double totalRaisedAmount) {
        this.totalRaisedAmount = totalRaisedAmount;
    }

    public long getTotalFinishedCampaigns() {
        return totalFinishedCampaigns;
    }

    public void setTotalFinishedCampaigns(long totalFinishedCampaigns) {
        this.totalFinishedCampaigns = totalFinishedCampaigns;
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public List<CampaignDTO> getTopPerformingCampaigns() {
        return topPerformingCampaigns;
    }

    public void setTopPerformingCampaigns(List<CampaignDTO> topPerformingCampaigns) {
        this.topPerformingCampaigns = topPerformingCampaigns;
    }

    public List<String> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<String> recentActivity) {
        this.recentActivity = recentActivity;
    }
}
