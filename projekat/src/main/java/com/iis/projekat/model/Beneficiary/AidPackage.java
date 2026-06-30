package com.iis.projekat.model.Beneficiary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AidPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Beneficiary beneficiary;

    @OneToMany(mappedBy = "aidPackage", cascade = CascadeType.ALL)
    private List<PackageItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "distribution_id", nullable = false)
    private AidDistribution distribution;

    public AidDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(AidDistribution distribution) {
        this.distribution = distribution;
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