package com.iis.projekat.model.Beneficiary;

import com.iis.projekat.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary extends User {

    @Column(nullable = false)
    private boolean eligible;

    @Column(nullable = false)
    private AidType type;

    public Beneficiary(){

    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public AidType getType() {
        return type;
    }

    public void setType(AidType type) {
        this.type = type;
    }
}
