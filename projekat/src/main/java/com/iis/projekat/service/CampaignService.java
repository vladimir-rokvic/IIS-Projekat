package com.iis.projekat.service;

import com.iis.projekat.dto.Campaign.CampaignDTO;
import com.iis.projekat.dto.Campaign.CampaignCreateDTO;
import com.iis.projekat.model.Campaign;
import com.iis.projekat.model.Project;
import com.iis.projekat.repository.CampaignRepository;
import com.iis.projekat.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {
	private final CampaignRepository campaignRepository;
	private final ProjectRepository projectRepository;
	private final DonationService donationService;

	public CampaignService(CampaignRepository campaignRepository, ProjectRepository projectRepository, DonationService donationService) {
		this.campaignRepository = campaignRepository;
		this.projectRepository = projectRepository;
		this.donationService = donationService;
	}

	public CampaignDTO createCampaign(CampaignCreateDTO dto) {
		Campaign campaign = new Campaign();
		applyDto(campaign, dto);
		return new CampaignDTO(campaignRepository.save(campaign));
	}

	public CampaignDTO updateCampaign(Long id, CampaignCreateDTO dto) {
		Campaign campaign = campaignRepository.findById(id).orElseThrow();
		applyDto(campaign, dto);
		return new CampaignDTO(campaignRepository.save(campaign));
	}

	public CampaignDTO getCampaignById(Long id) {
		return new CampaignDTO(campaignRepository.findById(id).orElseThrow());
	}

	public List<CampaignDTO> listCampaigns() {
		List<CampaignDTO> campaigns = campaignRepository.findAll().stream()
				.map(CampaignDTO::new)
				.collect(Collectors.toList());
		for (CampaignDTO campaign : campaigns) {
			double raised = donationService.findByCampaignId(campaign.getId()).stream()
					.mapToDouble(donation -> donation.getAmount())
					.sum();
			campaign.setRaised(raised);
		}
		return campaigns;
	}

	public void deleteCampaign(Long id) {
		campaignRepository.deleteById(id);
	}

	private void applyDto(Campaign campaign, CampaignCreateDTO dto) {
		campaign.setName(dto.getName());
		campaign.setGoal(dto.getGoal());
		campaign.setStartDate(dto.getStartDate());
		campaign.setEndDate(dto.getEndDate());
		campaign.setDescription(dto.getDescription());
		campaign.setStatus(dto.getStatus());
		if (dto.getProjectId() != null) {
			Project project = projectRepository.findById(dto.getProjectId()).orElse(null);
			campaign.setProject(project);
		} else {
			campaign.setProject(null);
		}
	}
}