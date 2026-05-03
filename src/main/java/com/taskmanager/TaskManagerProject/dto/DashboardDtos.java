package com.taskmanager.TaskManagerProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DashboardDtos {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummaryResponse {
        private long totalTasks;
        private long todoTasks;
        private long inProgressTasks;
        private long doneTasks;
        private long overdueTasks;
    }
}
