package com.joa.prexixionapi.modules.depreciacion.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import com.joa.prexixionapi.modules.depreciacion.services.CalculoService;
import com.joa.prexixionapi.modules.depreciacion.controllers.CalculoController;

@RestController
@RequestMapping("/api/activos/calculos")
@RequiredArgsConstructor
@Slf4j
public class CalculoController {

    private final CalculoService calculoService;

    @GetMapping("/fecha-final")
    public ResponseEntity<Map<String, String>> getFechaFinal(
            @RequestParam String fechaInicio, 
            @RequestParam Double porcentaje) {
        String fechaFin = calculoService.calcularFechaFinal(fechaInicio, porcentaje);
        
        Map<String, String> response = new HashMap<>();
        response.put("fechaFin", fechaFin);
        return ResponseEntity.ok(response);
    }
}
