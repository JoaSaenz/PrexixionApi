package com.joa.prexixionapi.modules.pdt621.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621AnualResponseDTO;
import com.joa.prexixionapi.modules.pdt621.services.Pdt621Service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621FilterRequestDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ListDTO;

@RestController
@RequestMapping("/api/pdt621")
@RequiredArgsConstructor
public class Pdt621Controller {

    private final Pdt621Service pdt621Service;

    @GetMapping("/report/anual")
    public ResponseEntity<ApiResponse<Pdt621AnualResponseDTO>> getReportAnual(
            @RequestParam String ruc,
            @RequestParam String anios) {
        List<String> anioList = Arrays.asList(anios.split(","));
        return ResponseEntity.ok(new ApiResponse<>(true, "Reporte generado", pdt621Service.getPdt621ReportAnual(ruc, anioList)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Pdt621ListDTO>>> getPdt621List(
            @org.springframework.web.bind.annotation.ModelAttribute Pdt621FilterRequestDTO request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista obtenida", pdt621Service.getPdt621List(request)));
    }
}
