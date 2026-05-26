package com.iis.projekat.repository;

import com.iis.projekat.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    public List<Task> findAllByVolunteerId(Long volunteerId);
}
