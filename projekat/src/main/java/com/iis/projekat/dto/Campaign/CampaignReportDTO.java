package com.iis.projekat.dto.Campaign;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CampaignReportDTO {

    // Basic campaign data
    public Long campaignId;
    public String name;
    public String description;
    public Double goal;
    public LocalDate startDate;
    public LocalDate endDate;
    public String status;
    public String category;
    public String linkedProjectName;

    // Financials
    public double totalRaised;
    public int donorCount;
    public int donationCount;
    public Double goalProgressPercent;

    // Breakdown by donation type (name -> total amount)
    public Map<String, Double> amountByType;

    // Breakdown by periodicity (name -> count)
    public Map<String, Long> countByPeriodicity;

    // Top donors for this campaign
    public List<DonorAmountDTO> topDonors;

    // Overall (cross-campaign) trends and category breakdown
    public List<DonationTrendDTO> trends;
    public List<CategoryDonationDTO> categoryBreakdown;

    public static class DonorAmountDTO {
        public String name;
        public double amount;
    }
}