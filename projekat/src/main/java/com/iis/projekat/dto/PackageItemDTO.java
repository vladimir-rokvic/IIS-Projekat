package com.iis.projekat.dto;

public class PackageItemDTO {

    private String product;

    private Float quantity;

    public PackageItemDTO() {
    }

    public String getProduct() {
        return product;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }
}