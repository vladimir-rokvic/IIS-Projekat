package com.iis.projekat.model.Beneficiary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BeneficiarySurvey {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Beneficiary beneficiary;

    @ManyToOne
    private AidDistribution distribution;

    @Column
    private double rating;

    @Column
    private String comment;

}
