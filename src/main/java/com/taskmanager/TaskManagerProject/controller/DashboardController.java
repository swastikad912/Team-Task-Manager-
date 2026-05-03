package com.taskmanager.TaskManagerProject.controller;

import com.taskmanager.TaskManagerProject.dto.DashboardDtos;
import com.taskmanager.TaskManagerProject.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardDtos.DashboardSummaryResponse> summary() {
        return ResponseEntity.ok(dashboardService.mySummary());
    }
}
