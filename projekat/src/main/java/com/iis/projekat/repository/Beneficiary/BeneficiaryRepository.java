package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.model.Beneficiary.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    Optional<Beneficiary> findByEmail(String email);
    boolean existsByEmail(String email);

}