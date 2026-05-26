package com.iis.projekat.service;

import com.iis.projekat.dto.CreateTaskDTO;
import com.iis.projekat.dto.SkillDTO;
import com.iis.projekat.dto.TaskDTO;
import com.iis.projekat.dto.UpdateTaskDTO;
import com.iis.projekat.model.Skill;
import com.iis.projekat.model.Task;
import com.iis.projekat.model.Volunteer;
import com.iis.projekat.repository.SkillRepository;
import com.iis.projekat.repository.TaskRepository;
import com.iis.projekat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public TaskDTO getById(Long id) {
        Task t = taskRepository.findById(id).orElse(null);
        if(t == null) return null;
        return new TaskDTO(t);
    }

    public Task updateTaskById(Long id, UpdateTaskDTO dto) {
        Task t = taskRepository.findById(id).orElse(null);
        if(t == null) return null;
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        if(dto.getVolunteerId() != null) {
            t.setVolunteer(volunteerRepository.findById(dto.getVolunteerId()).orElse(null));
        } else {
            t.setVolunteer(null);
        }

        Set<Skill> skills = new HashSet<>();
        for(SkillDTO skillDTO: dto.getRequiredSkills()){
            Skill s = skillRepository.findByName(skillDTO.getName()).orElse(null);
            if(s == null){
                Skill nSkill = new Skill();
                nSkill.setName(skillDTO.getName());
                nSkill.setDescription(skillDTO.getDesc());
                skills.add(skillRepository.save(nSkill));
            } else {
                skills.add(s);
            }
        }

        t.setRequiredSkills(skills);
        taskRepository.save(t);
        return t;
    }
}
