package com.iis.projekat.controller;

import com.iis.projekat.dto.CreateTaskDTO;
import com.iis.projekat.dto.TaskDTO;
import com.iis.projekat.dto.UpdateTaskDTO;
import com.iis.projekat.service.TaskService;
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

    @PostMapping
    public ResponseEntity<?> saveTask(@RequestBody CreateTaskDTO dto) {
        taskService.saveTask(dto);
        return ResponseEntity.ok("All seems good");
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
        return ResponseEntity.ok(taskService.updateTaskById(id, dto));
    }
}
