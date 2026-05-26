package com.iis.projekat.controller;

import com.iis.projekat.dto.CreateTaskDTO;
import com.iis.projekat.dto.TaskDTO;
import com.iis.projekat.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> saveTask(@RequestBody CreateTaskDTO dto) {
        taskService.saveTask(dto);
        return ResponseEntity.ok("All seems good");
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity.ok(taskService.getAll());
    }
}
