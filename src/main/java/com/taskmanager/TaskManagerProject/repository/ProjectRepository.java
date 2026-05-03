package com.taskmanager.TaskManagerProject.repository;

import com.taskmanager.TaskManagerProject.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
