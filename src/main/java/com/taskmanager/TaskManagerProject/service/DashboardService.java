package com.taskmanager.TaskManagerProject.service;

import com.taskmanager.TaskManagerProject.dto.DashboardDtos;
import com.taskmanager.TaskManagerProject.entity.Task;
import com.taskmanager.TaskManagerProject.repository.TaskRepository;
import com.taskmanager.TaskManagerProject.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    private final TaskRepository taskRepository;
    private final AuthContextService authContextService;

    public DashboardService(TaskRepository taskRepository, AuthContextService authContextService) {
        this.taskRepository = taskRepository;
        this.authContextService = authContextService;
    }

    public DashboardDtos.DashboardSummaryResponse mySummary() {
        UserPrincipal principal = authContextService.currentUser();
        List<Task> myTasks = taskRepository.findByAssignedToId(principal.getId());
        long total = myTasks.size();
        long todo = myTasks.stream().filter(task -> task.getStatus() == Task.Status.TODO).count();
        long inProgress = myTasks.stream().filter(task -> task.getStatus() == Task.Status.IN_PROGRESS).count();
        long done = myTasks.stream().filter(task -> task.getStatus() == Task.Status.DONE).count();
        long overdue = taskRepository
                .findByAssignedToIdAndDueDateBeforeAndStatusNot(principal.getId(), LocalDate.now(), Task.Status.DONE)
                .size();
        return new DashboardDtos.DashboardSummaryResponse(total, todo, inProgress, done, overdue);
    }
}
