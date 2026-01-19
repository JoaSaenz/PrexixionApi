package com.joa.prexixion.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.dto.ApiResponse;
import com.joa.prexixion.services.JobLauncherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sunat")
@RequiredArgsConstructor
public class SunatBuzonController {

    private final JobLauncherService jobLauncherService;

    @PostMapping("/sincronizar")
    public ResponseEntity<ApiResponse> sincronizarManualmente() {
        ApiResponse response = jobLauncherService.lanzarSincronizacionSunat();
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
