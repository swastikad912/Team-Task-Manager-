package com.taskmanager.TaskManagerProject.dto;

import com.taskmanager.TaskManagerProject.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class TaskDtos {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTaskRequest {
        @NotNull
        private Long projectId;
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private Long assignedToUserId;
        @NotNull
        private LocalDate dueDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotNull
        private Task.Status status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private Task.Status status;
        private Long assignedToUserId;
        private String assignedToName;
        private Long projectId;
        private LocalDate dueDate;
    }
}
