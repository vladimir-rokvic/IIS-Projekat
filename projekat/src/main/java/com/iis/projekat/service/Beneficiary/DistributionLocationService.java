package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.DistributionLocationDTO;
import com.iis.projekat.dto.Beneficiary.DistributionLocationResponse;
import com.iis.projekat.model.Address;
import com.iis.projekat.model.Beneficiary.DistributionLocation;
import com.iis.projekat.repository.AddressRepository;
import com.iis.projekat.repository.Beneficiary.DistributionLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistributionLocationService {

    @Autowired
    private DistributionLocationRepository distributionLocationRepository;

    @Autowired
    private AddressRepository addressRepository;

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<DistributionLocationResponse> getAll(){

        List<DistributionLocation> distrbutionLocations = distributionLocationRepository.findAll();
        List<DistributionLocationResponse> responses = new ArrayList<>();

        for(DistributionLocation dl : distrbutionLocations){
            responses.add(toLocationResponse(dl));
        }

        return responses;
    }

    public DistributionLocation create(DistributionLocationDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("DTO is null");
        }

        if (isBlank(dto.getName()) ||
                dto.getCapacity() == null ||
                isBlank(dto.getType()) ||
                isBlank(dto.getContactName()) ||
                isBlank(dto.getContactNumber()) ||
                isBlank(dto.getCity()) ||
                isBlank(dto.getStreet()) ||
                isBlank(dto.getCountry()) ||
                dto.getWorkHoursBegin() == null ||
                dto.getWorkHoursEnd() == null) {

            throw new IllegalArgumentException("Missing fields");
        }

        Address address;

        if (addressRepository.existsByCityAndStreetAndCountry(
                dto.getCity(),
                dto.getStreet(),
                dto.getCountry()
        )) {

            address = addressRepository.findByCityAndStreetAndCountry(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );

        } else {

            address = new Address(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );

            addressRepository.save(address);
        }

        DistributionLocation location = new DistributionLocation();

        location.setName(dto.getName());
        location.setCapacity(dto.getCapacity());
        location.setType(dto.getType());
        location.setContactName(dto.getContactName());
        location.setContactNumber(dto.getContactNumber());
        location.setWorkHoursBegin(dto.getWorkHoursBegin());
        location.setWorkHoursEnd(dto.getWorkHoursEnd());
        location.setAddress(address);

        return distributionLocationRepository.save(location);
    }

    private DistributionLocationResponse toLocationResponse(DistributionLocation l) {
        return DistributionLocationResponse.builder()
                .id(l.getId())
                .name(l.getName())
                .capacity(l.getCapacity())
                .type(l.getType())
                .contactName(l.getContactName())
                .contactNumber(l.getContactNumber())
                .workHoursBegin(l.getWorkHoursBegin())
                .workHoursEnd(l.getWorkHoursEnd())
                .build();
    }
}