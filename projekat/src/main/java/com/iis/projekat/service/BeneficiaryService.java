package com.iis.projekat.service;

import com.iis.projekat.dto.Beneficiary.BeneficiaryDTO;
import com.iis.projekat.model.Address;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import com.iis.projekat.repository.AddressRepository;
import com.iis.projekat.repository.BeneficiaryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;

    public BeneficiaryService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public Beneficiary saveBeneficiary(BeneficiaryDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException();
        }

        // validacija obaveznih polja
        if (isBlank(dto.getName()) ||
                isBlank(dto.getSurname()) ||
                isBlank(dto.getEmail()) ||
                isBlank(dto.getPhone()) ||
                dto.getDateOfBirth() == null ||
                isBlank(dto.getCity()) ||
                isBlank(dto.getStreet()) ||
                isBlank(dto.getCountry()) ||
                isBlank(dto.getPassword()) ||
                dto.getType()== null) {
            throw new IllegalArgumentException();
        }

        if (beneficiaryRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email exists");
        }

        Beneficiary b = new Beneficiary();
        Address a;

        if (addressRepository.existsByCityAndStreetAndCountry(
                dto.getCity(),
                dto.getStreet(),
                dto.getCountry()
        )) {
            a = addressRepository.findByCityAndStreetAndCountry(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );
        } else {
            a = new Address(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );
            addressRepository.save(a);
        }

        b.setAddress(a);

        b.setName(dto.getName());
        b.setSurname(dto.getSurname());
        b.setEmail(dto.getEmail());
        b.setPhone(dto.getPhone());
        b.setDateOfBirth(dto.getDateOfBirth());

        b.setEligible(dto.isEligible());

        b.setPassword(passwordEncoder.encode(dto.getPassword()));
        b.setType(dto.getType());

        beneficiaryRepository.save(b);
        return beneficiaryRepository.save(b);
    }

    public boolean updateBeneficiary(Long id, BeneficiaryDTO dto) {

        Optional<Beneficiary> opt = beneficiaryRepository.findById(id);
        if (opt.isEmpty()) return false;

        Beneficiary b = opt.get();

        if (isNotEmpty(dto.getCity()) || isNotEmpty(dto.getStreet()) || isNotEmpty(dto.getCountry())) {
            String city    = isNotEmpty(dto.getCity())    ? dto.getCity()    : b.getAddress().getCity();
            String street  = isNotEmpty(dto.getStreet())  ? dto.getStreet()  : b.getAddress().getStreet();
            String country = isNotEmpty(dto.getCountry()) ? dto.getCountry() : b.getAddress().getCountry();

            Address a;
            if (addressRepository.existsByCityAndStreetAndCountry(city, street, country)) {
                a = addressRepository.findByCityAndStreetAndCountry(city, street, country);
            } else {
                a = new Address(city, street, country);
                addressRepository.save(a);
            }
            b.setAddress(a);
        }

        if (isNotEmpty(dto.getName()))        b.setName(dto.getName());
        if (isNotEmpty(dto.getSurname()))     b.setSurname(dto.getSurname());
        if (isNotEmpty(dto.getEmail()))       b.setEmail(dto.getEmail());
        if (isNotEmpty(dto.getPhone()))       b.setPhone(dto.getPhone());
        if (dto.getDateOfBirth() != null)     b.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty())
            b.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getType() != null) b.setType(dto.getType());

        beneficiaryRepository.save(b);
        return true;
    }

    private boolean isNotEmpty(String s) {
        return s != null && !s.isBlank();
    }
}