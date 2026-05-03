package com.taskmanager.TaskManagerProject.service;

import com.taskmanager.TaskManagerProject.dto.TaskDtos;
import com.taskmanager.TaskManagerProject.entity.Project;
import com.taskmanager.TaskManagerProject.entity.Task;
import com.taskmanager.TaskManagerProject.entity.User;
import com.taskmanager.TaskManagerProject.exception.ApiException;
import com.taskmanager.TaskManagerProject.repository.ProjectRepository;
import com.taskmanager.TaskManagerProject.repository.TaskRepository;
import com.taskmanager.TaskManagerProject.repository.UserRepository;
import com.taskmanager.TaskManagerProject.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final AuthContextService authContextService;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository,
                       ProjectService projectService,
                       AuthContextService authContextService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.authContextService = authContextService;
    }

    public TaskDtos.TaskResponse createTask(TaskDtos.CreateTaskRequest request) {
        UserPrincipal principal = authContextService.currentUser();
        projectService.requireAdmin(request.getProjectId(), principal.getId());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ApiException("Project not found"));
        User assignedTo = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() -> new ApiException("Assigned user not found"));
        projectService.requireMembership(project.getId(), assignedTo.getId());

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setAssignedTo(assignedTo);
        task.setDueDate(request.getDueDate());
        task.setStatus(Task.Status.TODO);
        taskRepository.save(task);
        return toTaskResponse(task);
    }

    public List<TaskDtos.TaskResponse> tasksByProject(Long projectId) {
        UserPrincipal principal = authContextService.currentUser();
        projectService.requireMembership(projectId, principal.getId());
        return taskRepository.findByProjectId(projectId).stream().map(this::toTaskResponse).toList();
    }

    public TaskDtos.TaskResponse updateStatus(Long taskId, TaskDtos.UpdateStatusRequest request) {
        UserPrincipal principal = authContextService.currentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ApiException("Task not found"));
        projectService.requireMembership(task.getProject().getId(), principal.getId());

        boolean isAssignee = task.getAssignedTo().getId().equals(principal.getId());
        try {
            projectService.requireAdmin(task.getProject().getId(), principal.getId());
        } catch (ApiException e) {
            if (!isAssignee) {
                throw new ApiException("Only admin or assignee can update task status");
            }
        }

        task.setStatus(request.getStatus());
        taskRepository.save(task);
        return toTaskResponse(task);
    }

    public List<TaskDtos.TaskResponse> myTasks() {
        UserPrincipal principal = authContextService.currentUser();
        return taskRepository.findByAssignedToId(principal.getId()).stream().map(this::toTaskResponse).toList();
    }

    private TaskDtos.TaskResponse toTaskResponse(Task task) {
        return new TaskDtos.TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAssignedTo().getId(),
                task.getAssignedTo().getName(),
                task.getProject().getId(),
                task.getDueDate()
        );
    }
}
