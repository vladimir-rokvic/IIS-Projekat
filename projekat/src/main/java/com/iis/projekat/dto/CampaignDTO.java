package com.iis.projekat.dto;

import com.iis.projekat.model.Campaign;
import com.iis.projekat.model.CampaignStatus;

import java.time.LocalDate;

public class CampaignDTO {
	private Long id;
	private String name;
	private Double goal;
	private LocalDate startDate;
	private LocalDate endDate;
	private String description;
	private CampaignStatus status;
	private Long projectId;

	public CampaignDTO() {
	}

	public CampaignDTO(Campaign campaign) {
		this.id = campaign.getId();
		this.name = campaign.getName();
		this.goal = campaign.getGoal();
		this.startDate = campaign.getStartDate();
		this.endDate = campaign.getEndDate();
		this.description = campaign.getDescription();
		this.status = campaign.getStatus();
		this.projectId = campaign.getProject() != null ? campaign.getProject().getId() : null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getGoal() {
		return goal;
	}

	public void setGoal(Double goal) {
		this.goal = goal;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CampaignStatus getStatus() {
		return status;
	}

	public void setStatus(CampaignStatus status) {
		this.status = status;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}