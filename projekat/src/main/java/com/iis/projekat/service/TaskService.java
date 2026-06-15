package com.iis.projekat.service;

import com.iis.projekat.dto.*;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.*;
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
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProjectPhaseRepository projectPhaseRepository;
    @Autowired
    private SkillTypeRepository skillTypeRepository;

    public void saveTask(CreateTaskDTO dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        Volunteer v = volunteerRepository.getReferenceById(dto.getVolunteerId());
        task.setVolunteer(v);

        Employee coordiantor = employeeRepository.findById(dto.getCoordinatorId())
                .orElse(null);
        task.setCoordinator(coordiantor);

        if (dto.getPhaseId() != null) {
            ProjectPhase phase = projectPhaseRepository.findById(dto.getPhaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Faza sa ID=" + dto.getPhaseId() + " ne postoji."));
            task.setPhase(phase);
        }

        List<SkillType> skills = new ArrayList<>();
        for(SkillTypeDTO s: dto.getRequiredSkillTypes()) {
            SkillType sk = skillTypeRepository.findById(s.id).orElse(null);
            skills.add(sk);
        }

        task.setStartDate(dto.getStartDate());
        task.setEndDate(dto.getEndDate());

        task.setRequiredSkillTypes(skills);
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

    public List<TaskDTO> getTasksForVolunteer(Long volunteerId) {
        List<Task> tasks = taskRepository.findAllByVolunteerId(volunteerId);

        List<TaskDTO> ret = new ArrayList<>();
        for(Task t: tasks){
            ret.add(new TaskDTO(t));
        }

        return ret;
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
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

    public Performance rateTask(PerformanceDTO grade) {
        Volunteer v = volunteerRepository.findById(grade.getVolunteerId())
                .orElse(null);
        if(v == null) return null;

        Task t = taskRepository.findById(grade.getTaskId())
                .orElse(null);
        if(t == null) return null;

        Performance p = new Performance(
                grade.getGrade(),
                grade.getComment(),
                v,
                t
        );

        return performanceRepository.save(p);
    }

    public List<TaskDTO> getTasksForPhase(Long phaseId) {
        List<Task> tasks = taskRepository.findAllByPhaseId(phaseId);
        List<TaskDTO> ret = new ArrayList<>();
        for (Task t : tasks) {
            ret.add(new TaskDTO(t));
        }
        return ret;
    }
}
