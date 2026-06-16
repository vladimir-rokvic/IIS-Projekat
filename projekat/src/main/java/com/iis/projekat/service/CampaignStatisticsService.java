package com.iis.projekat.service;

import com.iis.projekat.dto.CampaignDTO;
import com.iis.projekat.dto.CoordinatorDashboardDTO;
import com.iis.projekat.model.Campaign;
import com.iis.projekat.model.CampaignStatus;
import com.iis.projekat.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CampaignStatisticsService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DonationService donationService;

    public long getTotalActiveCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE).size();
    }

    public double getTotalRaisedAmount() {
        List<Campaign> campaigns = campaignRepository.findAll();
        double totalRaisedAmount = 0.0;
        for (Campaign campaign : campaigns) {
            double totalAmount = donationService.findByCampaignId(campaign.getId()).stream()
                    .mapToDouble(donation -> donation.getAmount())
                    .sum();
            totalRaisedAmount += totalAmount;
        }
        return totalRaisedAmount;
    }

    public long getTotalDonors() {
        List<Campaign> campaigns = campaignRepository.findAll();
        Set<Long> uniqueDonorIds = new HashSet<>();
        for (Campaign campaign : campaigns) {
            uniqueDonorIds.addAll(donationService.findByCampaignId(campaign.getId()).stream()
                    .map(donation -> donation.getDonorId())
                    .distinct()
                    .collect(Collectors.toSet()));
        }
        if (uniqueDonorIds.contains(null)) {
            uniqueDonorIds.remove(null);
        }
        return uniqueDonorIds.size();
    }

    public long getTotalFinishedCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.FINISHED).size();
    }

    public List<CampaignDTO> getTopPerformingCampaigns(int limit) {
        List<Campaign> campaigns = campaignRepository.findAll();
        return campaigns.stream()
                .sorted((c1, c2) -> Double.compare(
                        donationService.findByCampaignId(c2.getId()).stream().mapToDouble(donation -> donation.getAmount()).sum(),
                        donationService.findByCampaignId(c1.getId()).stream().mapToDouble(donation -> donation.getAmount()).sum()
                ))
                .limit(limit)
                .map(campaign -> new CampaignDTO(campaign, donationService.findByCampaignId(campaign.getId()).stream().mapToDouble(donation -> donation.getAmount()).sum()))
                .collect(Collectors.toList());
    }

    public List<String> getRecentActivity(int limit) {
        List<Campaign> campaigns = campaignRepository.findAll();
        return campaigns.stream()
                .sorted((c1, c2) -> c2.getStartDate().compareTo(c1.getStartDate()))
                .limit(limit)
                .map(campaign -> "Campaign '" + campaign.getName() + "' started on " + campaign.getStartDate())
                .collect(Collectors.toList());
    }

    public CoordinatorDashboardDTO getCoordinatorDashboard() {
        long totalActiveCampaigns = getTotalActiveCampaigns();
        double totalRaisedAmount = getTotalRaisedAmount();
        long totalFinishedCampaigns = getTotalFinishedCampaigns();
        long totalDonors = getTotalDonors();
        List<CampaignDTO> topPerformingCampaigns = getTopPerformingCampaigns(5);
        List<String> recentActivity = getRecentActivity(5);

        return new CoordinatorDashboardDTO(totalActiveCampaigns, totalRaisedAmount, totalFinishedCampaigns, totalDonors, topPerformingCampaigns, recentActivity);
    }
}
