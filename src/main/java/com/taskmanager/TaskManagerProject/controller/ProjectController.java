package com.taskmanager.TaskManagerProject.controller;

import com.taskmanager.TaskManagerProject.dto.ProjectDtos;
import com.taskmanager.TaskManagerProject.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDtos.ProjectResponse> createProject(
            @Valid @RequestBody ProjectDtos.CreateProjectRequest request
    ) {
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDtos.ProjectResponse>> myProjects() {
        return ResponseEntity.ok(projectService.myProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDtos.ProjectDetailsResponse> projectDetails(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.projectDetails(projectId));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectDtos.ProjectMemberResponse> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectDtos.AddMemberRequest request
    ) {
        return ResponseEntity.ok(projectService.addMember(projectId, request));
    }
}
