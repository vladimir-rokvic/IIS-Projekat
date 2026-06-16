package com.iis.projekat.service;

import com.iis.projekat.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignStatisticsService {

    @Autowired
    private CampaignRepository campaignRepository;
}
