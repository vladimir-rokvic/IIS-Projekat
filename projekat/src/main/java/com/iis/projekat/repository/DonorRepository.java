package com.iis.projekat.repository;

import com.iis.projekat.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    boolean existsByEmail(String email);
}
