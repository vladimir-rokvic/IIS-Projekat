package com.iis.projekat.service;

import com.iis.projekat.dto.DonationCreateDTO;
import com.iis.projekat.dto.DonationDTO;
import com.iis.projekat.model.Donation;
import com.iis.projekat.model.Donor;
import com.iis.projekat.repository.DonationRepository;
import com.iis.projekat.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private DtoMapperService mapper;

    public DonationDTO createDonation(DonationCreateDTO dto) {
        Donation d = new Donation();
        d.setAmount(dto.getAmount());
        d.setDonationType(dto.getDonationType());
        d.setPaymentDate(dto.getPaymentDate());
        d.setPeriodicity(dto.getPeriodicity());
        d.setWantsNotifications(dto.isWantsNotifications());
        d.setNotificationFrequency(dto.getNotificationFrequency());
        d.setNotificationChannel(dto.getNotificationChannel());

        if(dto.getDonorId() != null) {
            Donor donor = donorRepository.findById(dto.getDonorId()).orElse(null);
            d.setDonor(donor);
        }

        donationRepository.save(d);
        return mapper.toDonationDto(d);
    }

    public boolean updateDonation(Long id, DonationCreateDTO dto) {
        Donation d = donationRepository.findById(id).orElse(null);
        if(d == null) return false;

        d.setAmount(dto.getAmount());
        d.setDonationType(dto.getDonationType());
        d.setPaymentDate(dto.getPaymentDate());
        d.setPeriodicity(dto.getPeriodicity());
        d.setWantsNotifications(dto.isWantsNotifications());
        d.setNotificationFrequency(dto.getNotificationFrequency());
        d.setNotificationChannel(dto.getNotificationChannel());

        if(dto.getDonorId() != null) {
            Donor donor = donorRepository.findById(dto.getDonorId()).orElse(null);
            d.setDonor(donor);
        }

        donationRepository.save(d);
        return true;
    }

    public DonationDTO getDonationById(Long id) {
        Donation d = donationRepository.findById(id).orElseThrow();
        return mapper.toDonationDto(d);
    }

    public List<DonationDTO> listDonations() {
        return donationRepository.findAll().stream().map(mapper::toDonationDto).collect(Collectors.toList());
    }

    public void deleteDonation(Long id) { donationRepository.deleteById(id); }
}
