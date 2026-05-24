package com.iis.projekat.service;

import com.iis.projekat.dto.DonorCreateDTO;
import com.iis.projekat.dto.DonorDTO;
import com.iis.projekat.model.Address;
import com.iis.projekat.model.Donor;
import com.iis.projekat.repository.AddressRepository;
import com.iis.projekat.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private AddressRepository addressRepository;

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

    public List<DonorDTO> listDonors() {
        return donorRepository.findAll().stream().map(mapper::toDonorDto).collect(Collectors.toList());
    }

    public void deleteDonor(Long id) { donorRepository.deleteById(id); }
}
