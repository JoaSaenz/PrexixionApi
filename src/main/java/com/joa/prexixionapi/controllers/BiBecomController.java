package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.BiBecomDashboardDTO;
import com.joa.prexixionapi.services.BiBecomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bi-becom")
@RequiredArgsConstructor
@Slf4j
public class BiBecomController {

    private final BiBecomService biBecomService;

    @GetMapping("/dashboard")
    public BiBecomDashboardDTO getDashboardData(@RequestParam(required = false) String periodo) {
        try {
            log.info("GET /api/bi-becom/dashboard - Solicitando datos del dashboard BI BECOM para periodo: {}", periodo);
            return biBecomService.getDashboardData(periodo);
        } catch (Exception e) {
            log.error("Error al obtener datos del dashboard BI BECOM", e);
            throw e;
        }
    }
}
