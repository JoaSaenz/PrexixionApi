package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ActivoTipoDTO;
import com.joa.prexixionapi.services.ActivoTipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activos/tipos")
@RequiredArgsConstructor
@Slf4j
public class ActivoTipoController {

    private final ActivoTipoService service;

    @GetMapping
    public List<ActivoTipoDTO> getAll() {
        try {
            return service.getAllActivoTipos();
        } catch (Exception e) {
            log.error("Error al obtener todos los tipos de activos", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ActivoTipoDTO getById(@PathVariable String id) {
        try {
            return service.getActivoTipoById(id);
        } catch (Exception e) {
            log.error("Error al obtener tipo de activo por ID: {}", id, e);
            throw e;
        }
    }

    @PostMapping("/save")
    public void save(@RequestBody ActivoTipoDTO dto) {
        try {
            service.saveActivoTipo(dto);
        } catch (Exception e) {
            log.error("Error al guardar tipo de activo: {}", dto.getId(), e);
            throw e;
        }
    }
}
