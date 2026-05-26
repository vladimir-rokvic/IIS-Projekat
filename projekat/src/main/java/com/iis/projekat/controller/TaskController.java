package com.iis.projekat.controller;

import com.iis.projekat.dto.CreateTaskDTO;
import com.iis.projekat.dto.TaskDTO;
import com.iis.projekat.dto.UpdateTaskDTO;
import com.iis.projekat.dto.VolunteerDTO;
import com.iis.projekat.model.Task;
import com.iis.projekat.model.Volunteer;
import com.iis.projekat.service.EmailService;
import com.iis.projekat.service.TaskService;
import com.iis.projekat.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VolunteerService volunteerService;

    @PostMapping
    public ResponseEntity<?> saveTask(@RequestBody CreateTaskDTO dto) {
        if(dto.getVolunteerId() != null) {
            VolunteerDTO v = volunteerService.getVolunteerById(dto.getVolunteerId());
            if(v != null) {
                String body = "You have been chosen to work on a task";
                String subject = "Task Assignment";
                String sendTo = v.getEmail();
                emailService.sendMail(sendTo, subject, body);
            }
        }
        taskService.saveTask(dto);
        return ResponseEntity.ok("All seems good");
    }

    @GetMapping("volunteer/{id}")
    public ResponseEntity<List<TaskDTO>> getForVolunteer (@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTasksForVolunteer(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity.ok(taskService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable Long id) {
        TaskDTO ret = taskService.getById(id);
        if(ret == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(ret);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody UpdateTaskDTO dto) {
        Task oldTask = taskService.findById(id);
        String oldEmail = oldTask.getVolunteer() != null ?
                oldTask.getVolunteer().getEmail() : null;
        if(oldTask == null) {
            return ResponseEntity.badRequest().build();
        }

        Task newTask = taskService.updateTaskById(id, dto);
        String newEmail = newTask.getVolunteer() != null ?
                newTask.getVolunteer().getEmail() : null;

        if(newEmail != null){
            if(!newEmail.equals(oldEmail)) {
                String body = "You have been chosen to work on a task";
                String subject = "Task Assignment";
                String sendTo = newTask.getVolunteer().getEmail();
                emailService.sendMail(sendTo, subject, body);
            }
        }

        return ResponseEntity.ok(newTask);
    }
}
