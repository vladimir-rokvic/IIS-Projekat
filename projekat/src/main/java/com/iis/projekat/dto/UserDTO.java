package com.iis.projekat.dto;

import com.iis.projekat.model.User;

import java.time.LocalDate;

public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate dateOfBirth;
    private String phone;
    private AddressDTO addressDTO;

    public UserDTO() {}

    public UserDTO(User user) {
        this.name = user.getName();
        this.surname = user.getSurname();
        this.id = user.getId();
        this.dateOfBirth = user.getDateOfBirth();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        if(user.getAddress() != null) {
            this.addressDTO = new AddressDTO(user.getAddress());
        } else {
            this.addressDTO = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
