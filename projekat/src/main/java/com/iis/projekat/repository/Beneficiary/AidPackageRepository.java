package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidPackage;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AidPackageRepository extends JpaRepository<AidPackage, Long> {
    List<AidPackage> findByBeneficiaryId(Long id);
        @Query("""
        SELECT COUNT(DISTINCT ap.beneficiary.id)
        FROM AidPackage ap
        WHERE ap.distribution.scheduledDate BETWEEN :start AND :end
    """)
        long countBeneficiariesReceivedAid(
                @Param("start") LocalDate start,
                @Param("end") LocalDate end
        );

        @Query("""
        SELECT b.type, COUNT(ap.id)
        FROM AidPackage ap
        JOIN ap.beneficiary b
        WHERE ap.distribution.scheduledDate BETWEEN :start AND :end
        GROUP BY b.type
    """)
        List<Object[]> countByAidType(
                @Param("start") LocalDate start,
                @Param("end") LocalDate end
        );

        // broj paketa po distribuciji
        @Query("""
        SELECT COUNT(ap)
        FROM AidPackage ap
        GROUP BY ap.distribution.id
    """)
        List<Long> countPackagesPerDistribution();

        @Query("""
        SELECT COUNT(ap)
        FROM AidPackage ap
        GROUP BY ap.distribution.id
    """)
        List<Long> countPerDistribution();

    @Query("""
    SELECT COUNT(DISTINCT ap.beneficiary.id)
    FROM AidPackage ap
    GROUP BY ap.distribution.id
""")
    List<Long> countDistinctBeneficiariesPerDistribution();

    @Query("""
    SELECT COUNT(p)
    FROM AidPackage p
""")
    long countAllPackages();
}