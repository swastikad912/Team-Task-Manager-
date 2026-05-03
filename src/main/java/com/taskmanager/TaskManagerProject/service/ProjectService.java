package com.taskmanager.TaskManagerProject.service;

import com.taskmanager.TaskManagerProject.dto.ProjectDtos;
import com.taskmanager.TaskManagerProject.entity.Project;
import com.taskmanager.TaskManagerProject.entity.ProjectMember;
import com.taskmanager.TaskManagerProject.entity.User;
import com.taskmanager.TaskManagerProject.exception.ApiException;
import com.taskmanager.TaskManagerProject.repository.ProjectMemberRepository;
import com.taskmanager.TaskManagerProject.repository.ProjectRepository;
import com.taskmanager.TaskManagerProject.repository.UserRepository;
import com.taskmanager.TaskManagerProject.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final AuthContextService authContextService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository,
                          AuthContextService authContextService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.authContextService = authContextService;
    }

    public ProjectDtos.ProjectResponse createProject(ProjectDtos.CreateProjectRequest request) {
        UserPrincipal principal = authContextService.currentUser();
        User creator = getUserOrThrow(principal.getId());

        Project project = new Project();
        project.setName(request.getName().trim());
        project.setDescription(request.getDescription());
        project.setCreatedBy(creator);
        projectRepository.save(project);

        ProjectMember adminMembership = new ProjectMember();
        adminMembership.setProject(project);
        adminMembership.setUser(creator);
        adminMembership.setRole(ProjectMember.Role.ADMIN);
        projectMemberRepository.save(adminMembership);

        return toProjectResponse(project);
    }

    public List<ProjectDtos.ProjectResponse> myProjects() {
        UserPrincipal principal = authContextService.currentUser();
        return projectMemberRepository.findByUserId(principal.getId()).stream()
                .map(ProjectMember::getProject)
                .distinct()
                .map(this::toProjectResponse)
                .toList();
    }

    public ProjectDtos.ProjectDetailsResponse projectDetails(Long projectId) {
        UserPrincipal principal = authContextService.currentUser();
        requireMembership(projectId, principal.getId());

        Project project = getProjectOrThrow(projectId);
        List<ProjectDtos.ProjectMemberResponse> members = projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::toMemberResponse)
                .toList();
        return new ProjectDtos.ProjectDetailsResponse(toProjectResponse(project), members);
    }

    public ProjectDtos.ProjectMemberResponse addMember(Long projectId, ProjectDtos.AddMemberRequest request) {
        UserPrincipal principal = authContextService.currentUser();
        requireAdmin(projectId, principal.getId());

        Project project = getProjectOrThrow(projectId);
        User user = getUserOrThrow(request.getUserId());
        projectMemberRepository.findByUserIdAndProjectId(user.getId(), projectId).ifPresent(pm -> {
            throw new ApiException("User is already in this project");
        });

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(request.getRole());
        projectMemberRepository.save(member);

        return toMemberResponse(member);
    }

    public void requireMembership(Long projectId, Long userId) {
        projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ApiException("You are not a member of this project"));
    }

    public void requireAdmin(Long projectId, Long userId) {
        ProjectMember projectMember = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ApiException("You are not a member of this project"));
        if (projectMember.getRole() != ProjectMember.Role.ADMIN) {
            throw new ApiException("Only project admin can perform this action");
        }
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private ProjectDtos.ProjectResponse toProjectResponse(Project project) {
        return new ProjectDtos.ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedBy().getId()
        );
    }

    private ProjectDtos.ProjectMemberResponse toMemberResponse(ProjectMember member) {
        return new ProjectDtos.ProjectMemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole()
        );
    }
}
