package com.iis.projekat.dto.Campaign;

import java.time.LocalDate;
import java.util.List;

public class DonationTrendsReportDTO {

    // Aggregate totals across all campaigns
    public double totalRaised;
    public int totalDonations;
    public int totalDonors;
    public int totalCampaigns;

    // Monthly trends and category breakdown (global)
    public List<DonationTrendDTO> trends;
    public List<CategoryDonationDTO> categoryBreakdown;

    // Most successful campaign (highest total amount raised)
    public TopCampaignDTO topCampaign;

    public static class TopCampaignDTO {
        public Long campaignId;
        public String name;
        public String category;
        public String status;
        public Double goal;
        public double totalRaised;
        public Double goalProgressPercent;
        public LocalDate startDate;
        public LocalDate endDate;
        public int donorCount;
        public int donationCount;
    }
}