package com.iis.projekat.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
public class Employee extends User {
    private Double salary;
    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;
    private LocalDate dateOfEmployment;

    @OneToMany(mappedBy = "coordinator")
    private Set<Task> tasks;

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

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
