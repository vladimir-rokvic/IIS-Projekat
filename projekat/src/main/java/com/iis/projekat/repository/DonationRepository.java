package com.iis.projekat.repository;

import com.iis.projekat.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
	List<Donation> findByDonor_IdOrderByPaymentDateDesc(Long donorId);
	Optional<Donation> findByDonor_IdAndProject_Id(Long donorId, Long projectId);
}
