package com.joa.prexixionapi.modules.depreciacion.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joa.prexixionapi.modules.depreciacion.dto.DepreciacionResumenDTO;
import com.joa.prexixionapi.modules.depreciacion.services.DepreciacionService;

@RestController
@RequestMapping("/api/depreciaciones")
@RequiredArgsConstructor
@Slf4j
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
