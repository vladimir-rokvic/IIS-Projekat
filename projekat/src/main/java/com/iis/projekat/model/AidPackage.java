package com.iis.projekat.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AidPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Beneficiary beneficiary;

    @OneToMany(mappedBy = "aidPackage", cascade = CascadeType.ALL)
    private List<PackageItem> items = new ArrayList<>();

    public AidPackage() {
    }

    public Long getId() {
        return id;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public List<PackageItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public void setItems(List<PackageItem> items) {
        this.items = items;
    }
}