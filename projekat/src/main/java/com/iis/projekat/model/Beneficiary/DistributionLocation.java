package com.iis.projekat.model.Beneficiary;

import com.iis.projekat.model.Address;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class DistributionLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer capacity;

    private String type;

    private String contactName;

    private String contactNumber;

    private LocalTime workHoursBegin;

    private LocalTime workHoursEnd;

    @ManyToOne
    private Address address;

    public DistributionLocation() {
    }

    public Long getId() {
        return id;
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

    public LocalTime getWorkHoursBegin() {
        return workHoursBegin;
    }

    public LocalTime getWorkHoursEnd() {
        return workHoursEnd;
    }

    public Address getAddress() {
        return address;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setWorkHoursBegin(LocalTime workHoursBegin) {
        this.workHoursBegin = workHoursBegin;
    }

    public void setWorkHoursEnd(LocalTime workHoursEnd) {
        this.workHoursEnd = workHoursEnd;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}