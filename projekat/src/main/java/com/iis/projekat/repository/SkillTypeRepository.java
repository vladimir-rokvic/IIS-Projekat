package com.iis.projekat.repository;

import com.iis.projekat.model.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillTypeRepository extends JpaRepository<SkillType, Long> {
    List<SkillType> findAllByOrderByNameAsc();
}
