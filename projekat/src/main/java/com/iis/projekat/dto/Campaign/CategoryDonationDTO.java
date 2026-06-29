package com.iis.projekat.dto.Campaign;

import com.iis.projekat.model.CampaignCategory;

public class CategoryDonationDTO {
    private CampaignCategory category;
    private double amount;

    public CategoryDonationDTO(CampaignCategory category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public CampaignCategory getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}