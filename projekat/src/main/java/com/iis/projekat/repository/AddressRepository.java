package com.iis.projekat.repository;

import com.iis.projekat.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    public Address findByCityAndStreetAndCountry(String city, String street, String country);
    public boolean existsByCityAndStreetAndCountry(String city, String street, String country);
}
