package com.taskmanager.TaskManagerProject.dto;

import com.taskmanager.TaskManagerProject.entity.ProjectMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectDtos {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectRequest {
        @NotBlank
        private String name;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddMemberRequest {
        @NotNull
        private Long userId;
        @NotNull
        private ProjectMember.Role role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String description;
        private Long createdByUserId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectMemberResponse {
        private Long id;
        private Long userId;
        private String userName;
        private String userEmail;
        private ProjectMember.Role role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectDetailsResponse {
        private ProjectResponse project;
        private List<ProjectMemberResponse> members;
    }
}
