package com.iis.projekat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary extends User{

    @Column(nullable = false)
    private boolean eligible;

    public Beneficiary(){

    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
