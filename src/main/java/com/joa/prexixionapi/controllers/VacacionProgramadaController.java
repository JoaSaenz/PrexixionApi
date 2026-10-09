package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ApiResponse;
import com.joa.prexixionapi.dto.VacacionProgramadaDTO;
import com.joa.prexixionapi.services.VacacionProgramadaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vacaciones")
@RequiredArgsConstructor
public class VacacionProgramadaController {

    private final VacacionProgramadaService service;

    @GetMapping
    public ResponseEntity<List<VacacionProgramadaDTO>> list(
            @RequestParam(required = false) Integer idArea,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String fechaI,
            @RequestParam(required = false) String fechaF) {
        log.info("Listando vacaciones: idArea={}, dni={}, fechaI={}, fechaF={}", idArea, dni, fechaI, fechaF);
        return ResponseEntity.ok(service.list(idArea, dni, fechaI, fechaF));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacacionProgramadaDTO> getOne(@PathVariable Integer id) {
        VacacionProgramadaDTO dto = service.getOne(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody VacacionProgramadaDTO dto) {
        try {
            int result = service.insertUpdate(dto);
            String message = result == 1
                    ? "Vacación programada registrada correctamente"
                    : "Vacación programada actualizada (reprogramada) correctamente";
            return ResponseEntity.ok(new ApiResponse<>(true, message, result));
        } catch (Exception e) {
            log.error("Error al registrar/actualizar vacación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error interno al guardar la vacación: " + e.getMessage(), 0));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable Integer id) {
        try {
            int result = service.delete(id);
            if (result > 0) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Vacación programada eliminada correctamente", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "No se encontró la vacación a eliminar", 0));
            }
        } catch (Exception e) {
            log.error("Error al eliminar vacación ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error al eliminar la vacación: " + e.getMessage(), 0));
        }
    }

    @GetMapping("/estados")
    public ResponseEntity<List<java.util.Map<String, Object>>> getEstados() {
        return ResponseEntity.ok(service.getEstados());
    }

    @GetMapping("/areas")
    public ResponseEntity<List<java.util.Map<String, Object>>> getAreas() {
        return ResponseEntity.ok(service.getAreas());
    }

    @GetMapping("/personal")
    public ResponseEntity<List<com.joa.prexixionapi.dto.PersonDTO>> getPersonal(
            @RequestParam(required = false) Integer idArea) {
        return ResponseEntity.ok(service.getPersonal(idArea));
    }
}
