package com.iis.projekat.dto;

import com.iis.projekat.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private List<SkillDTO> skillTypes;
    private Double predictedGrade;
    private List<AvailabilityDTO> availabilities;

    public VolunteerDTO() {}

    public VolunteerDTO(Volunteer v) {
        this.id = v.getId();
        this.name = v.getName();
        this.surname = v.getSurname();
        this.email = v.getEmail();
        this.phone = v.getPhone();
        this.dateOfBirth = v.getDateOfBirth();
        if (v.getAddress() != null) {
            this.address = new AddressDTO(v.getAddress());
        }
        this.bio = v.getBio();
        this.skills = new ArrayList<>();
        this.skillTypes = new ArrayList<>();
        this.availabilities = new ArrayList<>();

        for(Skill s: v.getSkills()){
            this.skills.add(new SkillDTO(s));
        }

        for(Availability a: v.getAvailabilities()) {
            this.availabilities.add(new AvailabilityDTO(a));
        }

        for(SkillType st: v.getVolunteerSkillTypes()) {
            this.skillTypes.add(new SkillDTO(st));
        }
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

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public Double getPredictedGrade() {
        return predictedGrade;
    }

    public void setPredictedGrade(Double predictedGrade) {
        this.predictedGrade = predictedGrade;
    }

    public List<SkillDTO> getSkillTypes() {
        return skillTypes;
    }

    public void setSkillTypes(List<SkillDTO> skillTypes) {
        this.skillTypes = skillTypes;
    }

    public List<AvailabilityDTO> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<AvailabilityDTO> availabilities) {
        this.availabilities = availabilities;
    }
}