package com.iis.projekat.repository;

import com.iis.projekat.model.Regiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegimentRepository extends JpaRepository<Regiment, Long> {
}
