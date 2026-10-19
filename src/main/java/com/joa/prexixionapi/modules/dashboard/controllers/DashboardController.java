package com.joa.prexixionapi.modules.dashboard.controllers;

import com.joa.prexixionapi.modules.dashboard.dto.DashboardSummaryDTO;
import com.joa.prexixionapi.modules.dashboard.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary(@RequestParam(required = false) String dni) {
        return ResponseEntity.ok(dashboardService.getSummary(dni));
    }
}
