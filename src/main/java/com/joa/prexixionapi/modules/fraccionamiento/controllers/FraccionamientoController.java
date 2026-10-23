package com.joa.prexixionapi.modules.fraccionamiento.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.fraccionamiento.dto.FraccionamientoFilterRequestDTO;
import com.joa.prexixionapi.modules.fraccionamiento.dto.FraccionamientoListDTO;
import com.joa.prexixionapi.modules.fraccionamiento.services.FraccionamientoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/fraccionamiento")
@RequiredArgsConstructor
@Slf4j
public class FraccionamientoController {

    private final FraccionamientoService fraccionamientoService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<FraccionamientoListDTO>> list(@RequestBody(required = false) FraccionamientoFilterRequestDTO req) {
        if (req == null) {
            req = new FraccionamientoFilterRequestDTO();
        }
        return ResponseEntity.ok(fraccionamientoService.list(req));
    }
}
