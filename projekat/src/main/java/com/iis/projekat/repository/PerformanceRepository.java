package com.iis.projekat.repository;

import com.iis.projekat.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Query("""
        SELECT AVG(p.grade)
        FROM Performance p
        WHERE p.ratedVolunteer.id = :volunteerId
    """)
    Double findAverageGradeByVolunteerId(@Param("volunteerId") Long volunteerId);
}
