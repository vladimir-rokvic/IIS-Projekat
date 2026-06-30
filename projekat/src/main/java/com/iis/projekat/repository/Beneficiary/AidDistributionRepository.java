package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.dto.Beneficiary.LocationCountDto;
import com.iis.projekat.dto.Beneficiary.StatusCountDto;
import com.iis.projekat.model.Beneficiary.AidDistribution;
import com.iis.projekat.model.Beneficiary.DistributionLocation;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AidDistributionRepository extends JpaRepository<AidDistribution,Long> {
    List<AidDistribution> findAllByLocation(DistributionLocation location);

    List<AidDistribution> findAllByScheduledDateLessThanEqualAndStatus(LocalDate date, DistributionStatus status);
    @Query("""
        SELECT d.status, COUNT(d.id)
        FROM AidDistribution d
        WHERE d.scheduledDate BETWEEN :start AND :end
        GROUP BY d.status
    """)
    List<Object[]> countByStatus(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
        SELECT new com.iis.projekat.dto.Beneficiary.StatusCountDto(d.status, COUNT(d))
        FROM AidDistribution d
        GROUP BY d.status
    """)
    List<StatusCountDto> countByStatus();


    @Query("""
        SELECT new com.iis.projekat.dto.Beneficiary.LocationCountDto(
            l.name,
            COUNT(d)
        )
        FROM AidDistribution d
        JOIN d.location l
        GROUP BY l.name
    """)
    List<LocationCountDto> countByLocation();


    @Query("""
        SELECT COUNT(d)
        FROM AidDistribution d
    """)
    long totalDistributions();

}


