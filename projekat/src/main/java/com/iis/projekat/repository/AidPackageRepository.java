package com.iis.projekat.repository;

import com.iis.projekat.model.Beneficiary.AidPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AidPackageRepository extends JpaRepository<AidPackage, Long> {
}