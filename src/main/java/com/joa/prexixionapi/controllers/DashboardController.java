package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.DashboardSummaryDTO;
import com.joa.prexixionapi.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para desarrollo local
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryDTO getSummary(@RequestParam(required = false) String dni) {
        return dashboardService.getSummary(dni);
    }
}
