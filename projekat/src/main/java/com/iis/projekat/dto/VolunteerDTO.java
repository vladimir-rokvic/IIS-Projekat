package com.iis.projekat.dto;

import com.iis.projekat.model.Address;
import com.iis.projekat.model.Volunteer;
import java.time.LocalDate;
import java.util.List;

public class VolunteerDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String bio;
    private LocalDate dateOfBirth;
    private AddressDTO address;
    private List<SkillDTO> skills;

    public VolunteerDTO(Volunteer v) {
        this.id = v.getId();
        this.name = v.getName();
        this.surname = v.getSurname();
        this.email = v.getEmail();
        this.phone = v.getPhone();
        this.dateOfBirth = v.getDateOfBirth();
        this.address = new AddressDTO(v.getAddress());
        this.bio = v.getBio();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }
}