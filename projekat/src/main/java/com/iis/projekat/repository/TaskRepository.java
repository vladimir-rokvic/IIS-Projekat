package com.iis.projekat.repository;

import com.iis.projekat.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    public List<Task> findAllByVolunteerId(Long volunteerId);

    List<Task> findAllByPhaseId(Long phaseId);

    List<Task> findAllByVolunteerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long volunteerId, LocalDate end, LocalDate start);
}
