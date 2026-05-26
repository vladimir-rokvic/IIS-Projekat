package com.iis.projekat.dto;

import com.iis.projekat.model.Address;

public class AddressDTO {
    private Long id;
    private String city;
    private String street;
    private String country;

    public AddressDTO() {}

    public AddressDTO(Address a){
        id = a.getId();
        city = a.getCity();
        street = a.getStreet();
        country = a.getCountry();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
