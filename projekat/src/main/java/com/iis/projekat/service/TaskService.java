package com.iis.projekat.service;

import com.iis.projekat.dto.CreateTaskDTO;
import com.iis.projekat.dto.SkillDTO;
import com.iis.projekat.dto.TaskDTO;
import com.iis.projekat.model.Skill;
import com.iis.projekat.model.Task;
import com.iis.projekat.model.Volunteer;
import com.iis.projekat.repository.SkillRepository;
import com.iis.projekat.repository.TaskRepository;
import com.iis.projekat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private SkillRepository skillRepository;

    public void saveTask(CreateTaskDTO dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        Volunteer v = volunteerRepository.getReferenceById(dto.getVolunteerId());
        task.setVolunteer(v);

        Set<Skill> skills = new HashSet<>();
        for(SkillDTO s: dto.getRequiredSkills()) {
            Skill sk = skillRepository.findByName(s.getName()).orElseGet(
                    () -> {
                        Skill n = new Skill();
                        n.setName(s.getName());
                        return skillRepository.save(n);
                    }
            );

            skills.add(sk);
        }

        task.setStartDate(dto.getStartDate());
        task.setEndDate(dto.getEndDate());

        task.setRequiredSkills(skills);
        taskRepository.save(task);
    }

    public List<TaskDTO> getAll() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskDTO> ret = new ArrayList<>();

        for(Task t: tasks) {
            ret.add(new TaskDTO(t));
        }

        return ret;
    }
}
