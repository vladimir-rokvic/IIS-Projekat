package com.iis.projekat.dto;

import java.time.LocalTime;

public class DistributionLocationDTO {

    private String name;

    private Integer capacity;

    private String type;

    private String contactName;

    private String contactNumber;

    private String city;

    private String street;

    private String country;

    private LocalTime workHoursBegin;

    private LocalTime workHoursEnd;

    public DistributionLocationDTO() {
    }

    public String getName() {
        return name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getType() {
        return type;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getCountry() {
        return country;
    }

    public LocalTime getWorkHoursBegin() {
        return workHoursBegin;
    }

    public LocalTime getWorkHoursEnd() {
        return workHoursEnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setWorkHoursBegin(LocalTime workHoursBegin) {
        this.workHoursBegin = workHoursBegin;
    }

    public void setWorkHoursEnd(LocalTime workHoursEnd) {
        this.workHoursEnd = workHoursEnd;
    }
}