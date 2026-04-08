package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ActivoTipoDTO;
import com.joa.prexixionapi.services.ActivoTipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activos/tipos")
@RequiredArgsConstructor
public class ActivoTipoController {

    private final ActivoTipoService service;

    @GetMapping
    public List<ActivoTipoDTO> getAll() {
        return service.getAllActivoTipos();
    }

    @GetMapping("/{id}")
    public ActivoTipoDTO getById(@PathVariable String id) {
        return service.getActivoTipoById(id);
    }

    @PostMapping("/save")
    public void save(@RequestBody ActivoTipoDTO dto) {
        service.saveActivoTipo(dto);
    }
}
