package com.iis.projekat.repository;

import com.iis.projekat.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
	List<Donation> findByDonor_IdOrderByPaymentDateDesc(Long donorId);
	Optional<Donation> findByDonor_IdAndProject_Id(Long donorId, Long projectId);

	@Query("SELECT d FROM Donation d WHERE d.project.id = :projectId")
	List<Donation> findByProjectId(@Param("projectId") Long projectId);
}
