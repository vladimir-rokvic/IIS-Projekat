package com.iis.projekat.repository;

import com.iis.projekat.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
	List<Donation> findByDonor_IdOrderByPaymentDateDesc(Long donorId);

}
