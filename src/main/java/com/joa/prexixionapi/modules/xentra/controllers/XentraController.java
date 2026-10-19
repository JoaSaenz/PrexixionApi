package com.joa.prexixionapi.modules.xentra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.joa.prexixionapi.modules.xentra.dto.XentraDataDTO;
import com.joa.prexixionapi.modules.xentra.dto.XentraReporteDTO;
import com.joa.prexixionapi.modules.xentra.services.XentraService;
import com.joa.prexixionapi.modules.xentra.services.XentraReporteService;
import lombok.extern.slf4j.Slf4j;

import com.joa.prexixionapi.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/xentra")
@Slf4j
public class XentraController {

    private final XentraService xentraService;
    private final XentraReporteService xentraReporteService;

    @GetMapping("/list/{idPuesto}/{idArea}")
    public ResponseEntity<List<XentraDataDTO>> list(
            @PathVariable int idPuesto,
            @PathVariable int idArea) {
        return ResponseEntity.ok(xentraService.list(idPuesto, idArea));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Integer>> create(@RequestBody XentraDataDTO xentraDTO) {
        int result = xentraService.create(xentraDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Creado exitosamente", result));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Integer>> edit(@PathVariable int id, @RequestParam String fechaFinFront) {
        int result = xentraService.edit(id, fechaFinFront);
        return ResponseEntity.ok(new ApiResponse<>(true, "Editado exitosamente", result));
    }

    @GetMapping("/getOne/{id}")
    public ResponseEntity<XentraDataDTO> getOne(@PathVariable int id) throws Exception {
        XentraDataDTO dto = xentraService.getOne(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/getFechas/{idPuesto}/{idArea}/{idSubArea}/{dni}")
    public ResponseEntity<List<XentraDataDTO>> getFechas(
            @PathVariable int idPuesto,
            @PathVariable int idArea,
            @PathVariable int idSubArea,
            @PathVariable String dni) {
        return ResponseEntity.ok(xentraService.getFechas(idPuesto, idArea, idSubArea, dni));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable int id) {
        int result = xentraService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Eliminado exitosamente", result));
    }

    @GetMapping("/reporte/{idPuesto}/{idArea}/{dni}/{fechaInicial}/{fechaFinal}")
    public ResponseEntity<List<XentraReporteDTO>> reporteList(
            @PathVariable int idPuesto,
            @PathVariable int idArea,
            @PathVariable String dni,
            @PathVariable String fechaInicial,
            @PathVariable String fechaFinal) {
        try {
            return ResponseEntity.ok(xentraReporteService.list(idPuesto, idArea, dni, fechaInicial, fechaFinal));
        } catch (Exception e) {
            log.error("Error al listar XentraReporteDTO - Puesto: {}, Area: {}, DNI: {}, Inicio: {}, Fin: {}", 
                    idPuesto, idArea, dni, fechaInicial, fechaFinal, e);
            throw e;
        }
    }
}
