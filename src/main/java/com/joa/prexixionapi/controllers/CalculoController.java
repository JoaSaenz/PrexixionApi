package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.services.CalculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/activos/calculos")
@RequiredArgsConstructor
public class CalculoController {

    private final CalculoService calculoService;

    @GetMapping("/fecha-final")
    public Map<String, String> getFechaFinal(
            @RequestParam String fechaInicio, 
            @RequestParam Double porcentaje) {
        
        String fechaFin = calculoService.calcularFechaFinal(fechaInicio, porcentaje);
        
        Map<String, String> response = new HashMap<>();
        response.put("fechaFin", fechaFin);
        return response;
    }
}
