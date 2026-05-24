package com.iis.projekat.dto;

import java.time.LocalDate;

public class DonorCreateDTO {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private boolean company;
    private String companyName;

    public DonorCreateDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isCompany() { return company; }
    public void setCompany(boolean company) { this.company = company; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
