package com.joa.prexixionapi.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.dto.ApiResponse;
import com.joa.prexixionapi.dto.Pdt621AnualResponseDTO;
import com.joa.prexixionapi.services.Pdt621Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdt621")
@RequiredArgsConstructor
public class Pdt621Controller {

    private final Pdt621Service pdt621Service;

    @GetMapping("/report/anual")
    public ApiResponse<Pdt621AnualResponseDTO> getReportAnual(
            @RequestParam String ruc,
            @RequestParam String anios) {
        List<String> anioList = Arrays.asList(anios.split(","));
        return new ApiResponse<>(true, "Reporte generado", pdt621Service.getPdt621ReportAnual(ruc, anioList));
    }
}
