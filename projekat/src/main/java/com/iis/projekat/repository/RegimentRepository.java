package com.iis.projekat.repository;

import com.iis.projekat.model.Regiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegimentRepository extends JpaRepository<Regiment, Long> {
    List<Regiment> findAllByTrainerId(Long trainerId);
    List<Regiment> findAllByTraineesId(Long volunteerId);
}
