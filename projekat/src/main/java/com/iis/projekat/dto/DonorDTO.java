package com.iis.projekat.dto;

import com.iis.projekat.model.Donor;

public class DonorDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private boolean company;
    private String companyName;

    public DonorDTO() {}

    public DonorDTO(Donor d) {
        this.id = d.getId();
        this.name = d.getName();
        this.surname = d.getSurname();
        this.email = d.getEmail();
        this.phone = d.getPhone();
        this.company = d.isCompany();
        this.companyName = d.getCompanyName();
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

    public boolean isCompany() { return company; }
    public void setCompany(boolean company) { this.company = company; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
