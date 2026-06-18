package com.iis.projekat.repository;

import com.iis.projekat.model.Volunteer;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    public boolean existsByEmail(String email);
    @Query(value = """
        SELECT v.*, u.*
        FROM volunteers v
        JOIN users u ON u.id = v.id
        WHERE v.id NOT IN (SELECT volunteer_id FROM trainees)
        """, nativeQuery = true)
    public List<Volunteer> findAllNotInTraining();
}
