package com.iis.projekat.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Employee extends User {
    private Double salary;
    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;
    private LocalDate dateOfEmployment;

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double plata) {
        this.salary = plata;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public LocalDate getDateOfEmployment() {
        return dateOfEmployment;
    }

    public void setDateOfEmployment(LocalDate dateOfEmployment) {
        this.dateOfEmployment = dateOfEmployment;
    }
}
