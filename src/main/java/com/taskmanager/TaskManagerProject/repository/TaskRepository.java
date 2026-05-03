package com.taskmanager.TaskManagerProject.repository;

import com.taskmanager.TaskManagerProject.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByAssignedToIdAndDueDateBeforeAndStatusNot(Long userId, LocalDate date, Task.Status status);
}
