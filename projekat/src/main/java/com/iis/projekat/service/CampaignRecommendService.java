package com.iis.projekat.service;

import com.iis.projekat.dto.Campaign.CampaignDTO;
import com.iis.projekat.dto.Campaign.CampaignRecommendationDTO;
import com.iis.projekat.model.Campaign;
import com.iis.projekat.model.CampaignCategory;
import com.iis.projekat.model.CampaignStatus;
import com.iis.projekat.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CampaignRecommendService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DonationService donationService;

    public List<CampaignRecommendationDTO> getCampaignRecommendations(int limit) {

        List<Campaign> finishedCampaigns = campaignRepository.findAll().stream()
                .filter(c -> c.getStatus() == CampaignStatus.FINISHED)
                .filter(c -> c.getGoal() > 0)
                .toList();

        if (finishedCampaigns.isEmpty()) {
            return List.of();
        }

        // Step 1: score campaigns
        Map<CampaignCategory, List<Campaign>> groupedByCategory =
                finishedCampaigns.stream()
                        .collect(Collectors.groupingBy(Campaign::getCategory));

        // Step 2: build recommendation per category
        List<CampaignRecommendationDTO> recommendations =
                groupedByCategory.entrySet().stream()
                        .map(entry -> {

                            CampaignCategory category = entry.getKey();
                            List<Campaign> campaigns = entry.getValue();

                            // rank campaigns inside category
                            List<Campaign> topCampaigns = campaigns.stream()
                                    .sorted((c1, c2) -> Double.compare(
                                            getRaised(c2.getId()) / c2.getGoal(),
                                            getRaised(c1.getId()) / c1.getGoal()
                                    ))
                                    .limit(5)
                                    .toList();

                            double recommendedGoal = topCampaigns.stream()
                                    .mapToDouble(Campaign::getGoal)
                                    .average()
                                    .orElse(0);

                            int recommendedDurationDays = (int) topCampaigns.stream()
                                    .mapToLong(c -> java.time.temporal.ChronoUnit.DAYS.between(
                                            c.getStartDate(),
                                            c.getEndDate()
                                    ))
                                    .average()
                                    .orElse(0);

                            List<CampaignDTO> referenceCampaigns = topCampaigns.stream()
                                    .map(c -> new CampaignDTO(c, getRaised(c.getId())))
                                    .toList();

                            return new CampaignRecommendationDTO(
                                    category,
                                    recommendedGoal,
                                    recommendedDurationDays,
                                    referenceCampaigns
                            );
                        })
                        // sort by strongest category (total raised)
                        .sorted((r1, r2) -> Double.compare(
                                totalRaised(r2.getReferenceCampaigns()),
                                totalRaised(r1.getReferenceCampaigns())
                        ))
                        .limit(limit)
                        .toList();

        return recommendations;
    }

    private double totalRaised(List<CampaignDTO> campaigns) {
        return campaigns.stream()
                .mapToDouble(CampaignDTO::getRaised)
                .sum();
    }
    private double getRaised(Long campaignId) {
        return donationService.findByCampaignId(campaignId).stream()
                .mapToDouble(d -> d.getAmount())
                .sum();
    }
}
