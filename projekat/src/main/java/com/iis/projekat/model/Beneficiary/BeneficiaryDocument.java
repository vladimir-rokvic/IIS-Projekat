package com.iis.projekat.model.Beneficiary;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="beneficiary documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryDocument {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip", nullable = false)
    private DocumentTypeBeneficiary tip;

    @Column(name = "putanja_fajla", nullable = false)
    private String putanjaFajla;

    @Column(name = "naziv_fajla", nullable = false)
    private String nazivFajla;

    @Column(name = "aktivan", nullable = false)
    private boolean aktivan = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Beneficiary korisnik;

    @CreationTimestamp
    @Column(name = "datum_uploada", updatable = false)
    private LocalDateTime datumUploada;
}