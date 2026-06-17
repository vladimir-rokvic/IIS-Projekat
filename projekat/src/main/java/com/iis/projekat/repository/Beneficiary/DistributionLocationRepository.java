package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.model.Beneficiary.DistributionLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributionLocationRepository extends JpaRepository<DistributionLocation, Long> {
}