package com.iis.projekat.service;

import com.iis.projekat.dto.DistributionLocationDTO;
import com.iis.projekat.model.Address;
import com.iis.projekat.model.DistributionLocation;
import com.iis.projekat.repository.AddressRepository;
import com.iis.projekat.repository.DistributionLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributionLocationService {

    @Autowired
    private DistributionLocationRepository distributionLocationRepository;

    @Autowired
    private AddressRepository addressRepository;

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
}