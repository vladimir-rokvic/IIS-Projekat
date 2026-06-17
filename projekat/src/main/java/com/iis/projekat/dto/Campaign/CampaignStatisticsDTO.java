package com.iis.projekat.dto.Campaign;

import java.util.List;

public class CampaignStatisticsDTO {

    private List<DonationTrendDTO> donationTrends;

    private List<CategoryDonationDTO> donationsPerCategory;

    private List<CampaignComparisonDTO> campaignComparison;

    public CampaignStatisticsDTO(
            List<DonationTrendDTO> donationTrends,
            List<CategoryDonationDTO> donationsPerCategory,
            List<CampaignComparisonDTO> campaignComparison
    ) {
        this.donationTrends = donationTrends;
        this.donationsPerCategory = donationsPerCategory;
        this.campaignComparison = campaignComparison;
    }

    // getters
    public List<DonationTrendDTO> getDonationTrends() {
        return donationTrends;
    }

    public List<CategoryDonationDTO> getDonationsPerCategory() {
        return donationsPerCategory;
    }

    public List<CampaignComparisonDTO> getCampaignComparison() {
        return campaignComparison;
    }
}