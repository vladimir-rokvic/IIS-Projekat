package com.iis.projekat.repository;

import com.iis.projekat.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    public boolean existsByEmail(String email);
}
