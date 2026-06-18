package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidPackage;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AidPackageRepository extends JpaRepository<AidPackage, Long> {
    List<AidPackage> findByBeneficiaryId(Long id);
}