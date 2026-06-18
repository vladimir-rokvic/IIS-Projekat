package com.iis.projekat.service;

import com.iis.projekat.dto.DonorCreateDTO;
import com.iis.projekat.dto.DonorDTO;
import com.iis.projekat.dto.DonationHistoryDTO;
import com.iis.projekat.dto.DonorProfileDTO;
import com.iis.projekat.model.Address;
import com.iis.projekat.model.Donation;
import com.iis.projekat.model.Donor;
import com.iis.projekat.repository.AddressRepository;
import com.iis.projekat.repository.CampaignRepository;
import com.iis.projekat.repository.DonationRepository;
import com.iis.projekat.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DtoMapperService mapper;

    private final PasswordEncoder passwordEncoder;

    public DonorService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public DonorDTO createDonor(DonorCreateDTO dto) {
        if(donorRepository.existsByEmail(dto.getEmail())) return null;

        Donor d = new Donor();
        d.setName(dto.getName());
        d.setSurname(dto.getSurname());
        d.setEmail(dto.getEmail());
        d.setPassword(passwordEncoder.encode(dto.getPassword()));
        d.setPhone(dto.getPhone());
        d.setCompany(dto.isCompany());
        d.setCompanyName(dto.getCompanyName());

        donorRepository.save(d);
        return mapper.toDonorDto(d);
    }

    public boolean updateDonor(Long id, DonorCreateDTO dto) {
        Donor d = donorRepository.findById(id).orElse(null);
        if(d == null) return false;

        d.setName(dto.getName());
        d.setSurname(dto.getSurname());
        d.setEmail(dto.getEmail());
        if(dto.getPassword() != null && !dto.getPassword().isBlank()) {
            d.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        d.setPhone(dto.getPhone());
        d.setCompany(dto.isCompany());
        d.setCompanyName(dto.getCompanyName());

        donorRepository.save(d);
        return true;
    }

    public DonorDTO getDonorById(Long id) {
        Donor d = donorRepository.findById(id).orElseThrow();
        return mapper.toDonorDto(d);
    }

        public DonorProfileDTO getDonorProfileById(Long id) {
        Donor donor = donorRepository.findById(id).orElseThrow();
        List<Donation> donations = donationRepository.findByDonor_IdOrderByPaymentDateDesc(id);

        DonorProfileDTO profile = new DonorProfileDTO();
        profile.setId(donor.getId());
        profile.setName(donor.getName());
        profile.setSurname(donor.getSurname());
        profile.setEmail(donor.getEmail());
        profile.setPhone(donor.getPhone());
        profile.setCompany(donor.isCompany());
        profile.setCompanyName(donor.getCompanyName());

        profile.setTotalDonated(donations.stream()
            .map(Donation::getAmount)
            .filter(amount -> amount != null)
            .reduce(0.0, Double::sum));
        profile.setDonationsMade((long) donations.size());

        Optional<LocalDate> lastDonationDate = donations.stream()
            .map(Donation::getPaymentDate)
            .filter(date -> date != null)
            .findFirst();
        profile.setLastDonationDate(lastDonationDate.orElse(null));

        List<DonationHistoryDTO> history = donations.stream()
            .map(donation -> new DonationHistoryDTO(
                donation.getCampaign() != null ? donation.getCampaign().getName() : "N/a",
                donation.getAmount(),
                donation.getPaymentDate(),
                donation.getCampaign() != null ? donation.getCampaign().getStatus().toString() : "N/a"
            ))
            .collect(Collectors.toList());
        profile.setDonationHistory(history);

        return profile;
        }

    public List<DonorDTO> listDonors() {
        return donorRepository.findAll().stream().map(mapper::toDonorDto).collect(Collectors.toList());
    }

    public void deleteDonor(Long id) { donorRepository.deleteById(id); }
}
