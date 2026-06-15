package com.iis.projekat.model.Beneficiary;

import jakarta.persistence.*;

@Entity
public class PackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Float quantity;

    @ManyToOne
    private AidPackage aidPackage;

    public PackageItem() {
    }

    public Long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public Float getQuantity() {
        return quantity;
    }

    public AidPackage getAidPackage() {
        return aidPackage;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public void setAidPackage(AidPackage aidPackage) {
        this.aidPackage = aidPackage;
    }
}