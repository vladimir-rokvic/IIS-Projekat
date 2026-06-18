package com.iis.projekat.controller;

import com.iis.projekat.dto.Campaign.CampaignDTO;
import com.iis.projekat.dto.Campaign.CampaignCreateDTO;
import com.iis.projekat.dto.Campaign.CampaignStatisticsDTO;
import com.iis.projekat.dto.Campaign.CoordinatorDashboardDTO;
import com.iis.projekat.service.CampaignRecommendService;
import com.iis.projekat.service.CampaignService;
import com.iis.projekat.service.CampaignStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
	private final CampaignService campaignService;
	private final CampaignStatisticsService campaignStatisticsService;

	private final CampaignRecommendService campaignRecommendService;

	public CampaignController(CampaignService campaignService, CampaignStatisticsService campaignStatisticsService, CampaignRecommendService campaignRecommendService) {
		this.campaignService = campaignService;
		this.campaignStatisticsService = campaignStatisticsService;
		this.campaignRecommendService = campaignRecommendService;
	}

	@PostMapping
	public ResponseEntity<CampaignDTO> createCampaign(@RequestBody CampaignCreateDTO dto) {
		return ResponseEntity.ok(campaignService.createCampaign(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CampaignDTO> updateCampaign(@PathVariable Long id, @RequestBody CampaignCreateDTO dto) {
		return ResponseEntity.ok(campaignService.updateCampaign(id, dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long id) {
		return ResponseEntity.ok(campaignService.getCampaignById(id));
	}

	@GetMapping
	public ResponseEntity<List<CampaignDTO>> listCampaigns() {
		return ResponseEntity.ok(campaignService.listCampaigns());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
		campaignService.deleteCampaign(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/coordinator-dashboard")
	public ResponseEntity<CoordinatorDashboardDTO> getCoordinatorDashboard() {
		return ResponseEntity.ok(campaignStatisticsService.getCoordinatorDashboard());
	}

	@GetMapping("/statistics")
	public ResponseEntity<CampaignStatisticsDTO> getCampaignStatistics() {
		return ResponseEntity.ok(campaignStatisticsService.getCampaignStatistics());
	}

	@GetMapping("/recommend")
	public ResponseEntity<?> getCampaignRecommendations(@RequestParam(defaultValue = "3") int limit) {
		return ResponseEntity.ok(campaignRecommendService.getCampaignRecommendations(limit));
	}
}