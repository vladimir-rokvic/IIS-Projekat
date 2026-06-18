package com.iis.projekat.dto.Campaign;

public class DonationTrendDTO {
    private String month;
    private double amount;

    public DonationTrendDTO(String month, double amount) {
        this.month = month;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public double getAmount() {
        return amount;
    }
}
