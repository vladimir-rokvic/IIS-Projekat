package com.iis.projekat.repository;

import com.iis.projekat.model.Campaign;
import com.iis.projekat.model.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    public List<Campaign> findByStatus(CampaignStatus status);
}