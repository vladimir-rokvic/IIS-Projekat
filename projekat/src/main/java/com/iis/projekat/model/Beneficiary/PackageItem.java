package com.iis.projekat.model.Beneficiary;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Float quantity;

    private String description;
    private String unit; // kg, kom, l...

    @ManyToOne
    private AidPackage aidPackage;

}