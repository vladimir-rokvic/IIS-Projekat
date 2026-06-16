package com.iis.projekat.dto;

import com.iis.projekat.model.CampaignCategory;
import com.iis.projekat.model.CampaignStatus;

import java.time.LocalDate;

public class CampaignCreateDTO {
	private String name;
	private Double goal;
	private LocalDate startDate;
	private LocalDate endDate;
	private String description;
	private CampaignStatus status;
	private CampaignCategory category;
	private Long projectId;

	public CampaignCreateDTO() {
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

	public CampaignCategory getCategory() {
		return category;
	}

	public void setCategory(CampaignCategory category) {
		this.category = category;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}