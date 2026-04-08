package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.DepreciacionResumenDTO;
import com.joa.prexixionapi.services.DepreciacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/depreciaciones")
@RequiredArgsConstructor
public class DepreciacionController {

    private final DepreciacionService service;

    @GetMapping("/clientes")
    public ResponseEntity<Map<String, Object>> getClientesResumen(@RequestParam("anio") String anio) {
        List<DepreciacionResumenDTO> list = service.getResumenByAnio(anio);
        long count = list.size(); // Usamos el tamaño de la lista de agregados
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", list);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
}
