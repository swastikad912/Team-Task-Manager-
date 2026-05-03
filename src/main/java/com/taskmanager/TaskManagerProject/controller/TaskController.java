package com.taskmanager.TaskManagerProject.controller;

import com.taskmanager.TaskManagerProject.dto.TaskDtos;
import com.taskmanager.TaskManagerProject.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDtos.TaskResponse> createTask(@Valid @RequestBody TaskDtos.CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDtos.TaskResponse>> projectTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.tasksByProject(projectId));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDtos.TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDtos.UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskDtos.TaskResponse>> myTasks() {
        return ResponseEntity.ok(taskService.myTasks());
    }
}
