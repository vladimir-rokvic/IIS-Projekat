package com.iis.projekat.model.Beneficiary;

import com.iis.projekat.model.Volunteer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aid_distributions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AidDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DistributionStatus status;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private DistributionLocation location;

    @OneToMany(mappedBy = "distribution", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Builder.Default
    private List<AidPackage> packages = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "distribution_volunteers",
            joinColumns = @JoinColumn(name = "distribution_id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id")
    )
    @Builder.Default
    private List<Volunteer> volunteers = new ArrayList<>();
}
