package com.iis.projekat.repository;

import com.iis.projekat.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    public List<Task> findAllByVolunteerId(Long volunteerId);

    List<Task> findAllByPhaseId(Long phaseId);

    List<Task> findAllByVolunteerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long volunteerId, LocalDate end, LocalDate start);
    @Query(value = """
            SELECT * FROM task
            WHERE volunteer_id = :volunteer_id
            AND end_date >= :date_ended
            """, nativeQuery = true)
    List<Task> findAllNotEndedByVolunteerId(@Param("volunteer_id") Long volunteerId, @Param("date_ended") LocalDate dateEnded);
}
