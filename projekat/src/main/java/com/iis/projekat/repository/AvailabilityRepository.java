package com.iis.projekat.repository;

import com.iis.projekat.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findAllByVolunteerId(Long volunteerId);

    List<Availability> findAllByTaskId(Long taskId);
}
