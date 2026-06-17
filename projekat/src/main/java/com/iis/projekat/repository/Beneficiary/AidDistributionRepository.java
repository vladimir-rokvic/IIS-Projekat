package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidDistribution;
import com.iis.projekat.model.Beneficiary.DistributionLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AidDistributionRepository extends JpaRepository<AidDistribution,Long> {
    List<AidDistribution> findAllByLocation(DistributionLocation location);
}
