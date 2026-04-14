package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ActivoDTO;
import com.joa.prexixionapi.dto.ActivoExcelDTO;
import com.joa.prexixionapi.services.ActivoExcelService;
import com.joa.prexixionapi.services.ActivoService;
import com.joa.prexixionapi.services.DepreciacionReporteExcelService;
import com.joa.prexixionapi.services.DepreciacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
@RequiredArgsConstructor
@Slf4j
public class ActivoController {

    private final ActivoService activoService;
    private final ActivoExcelService excelService;
    private final DepreciacionReporteExcelService reporteExcelService;
    private final DepreciacionService depreciacionService;

    @GetMapping("/cliente/{idCliente}")
    public List<ActivoDTO> getActivosByCliente(@PathVariable String idCliente) {
        try {
            return activoService.getActivosByIdCliente(idCliente);
        } catch (Exception e) {
            log.error("Error al obtener activos para cliente: {}", idCliente, e);
            throw e;
        }
    }

    @GetMapping("/cliente/{idCliente}/bien/{id}")
    public ActivoDTO getActivoDetalle(@PathVariable String idCliente, @PathVariable String id) {
        try {
            return activoService.getActivoDetalle(idCliente, id);
        } catch (Exception e) {
            log.error("Error al obtener detalle del activo: {} para cliente: {}", id, idCliente, e);
            throw e;
        }
    }

    @PostMapping("/save")
    public int saveActivo(@RequestBody ActivoDTO activo, @RequestParam(defaultValue = "false") boolean calcular) {
        try {
            return activoService.saveActivo(activo, calcular);
        } catch (Exception e) {
            log.error("Error al guardar activo: {} para cliente: {}", activo.getId(), activo.getIdCliente(), e);
            throw e;
        }
    }

    @PostMapping("/preview-depreciacion")
    public ActivoDTO previewDepreciacion(@RequestBody ActivoDTO activo) {
        try {
            // Calcula las depreciaciones contable y tributaria sin persistir, para previsualizar en la UI
            activo.setDepreciacionesContables(depreciacionService.calcularDepreciacion(activo, 1, null));
            activo.setDepreciacionesTributarias(depreciacionService.calcularDepreciacion(activo, 2, null));
            return activo;
        } catch (Exception e) {
            log.error("Error al previsualizar depreciación para activo: {}", activo.getId(), e);
            throw e;
        }
    }

    @DeleteMapping("/cliente/{idCliente}/bien/{id}")
    public int deleteActivo(@PathVariable String idCliente, @PathVariable String id) {
        try {
            return activoService.deleteActivo(idCliente, id);
        } catch (Exception e) {
            log.error("Error al eliminar activo: {} para cliente: {}", id, idCliente, e);
            throw e;
        }
    }

    @PutMapping("/cliente/{idCliente}/bloqueo/{status}")
    public int updateBloqueo(@PathVariable String idCliente, @RequestBody List<String> ids, @PathVariable int status) {
        try {
            return activoService.updateBloqueo(idCliente, ids, status);
        } catch (Exception e) {
            log.error("Error al actualizar bloqueo para cliente: {}", idCliente, e);
            throw e;
        }
    }

    @GetMapping("/cliente/{idCliente}/excel-data")
    public ActivoExcelDTO getActivosExcelData(@PathVariable String idCliente) {
        try {
            return activoService.getActivosExcelData(idCliente);
        } catch (Exception e) {
            log.error("Error al obtener datos excel para cliente: {}", idCliente, e);
            throw e;
        }
    }

    @GetMapping("/cliente/{idCliente}/fijos-rv")
    public List<ActivoDTO> getActivosFijosRVExcel(@PathVariable String idCliente) {
        try {
            return activoService.getActivosFijosRVExcel(idCliente);
        } catch (Exception e) {
            log.error("Error al obtener activos fijos RV para cliente: {}", idCliente, e);
            throw e;
        }
    }

    @GetMapping("/cliente/{idCliente}/fijos-anio/{anio}")
    public List<ActivoDTO> getActivosFijosExcelWithDepreciations(@PathVariable String idCliente, @PathVariable String anio) {
        try {
            return activoService.getActivosFijosExcelWithDepreciations(idCliente, anio);
        } catch (Exception e) {
            log.error("Error al obtener activos fijos con depreciaciones para cliente: {} y año: {}", idCliente, anio, e);
            throw e;
        }
    }

    @PostMapping("/upload")
    public int uploadActivos(@RequestBody List<ActivoDTO> list) {
        try {
            return activoService.uploadActivos(list);
        } catch (Exception e) {
            log.error("Error al subir activos en bloque", e);
            throw e;
        }
    }

    @GetMapping("/cliente/{idCliente}/excel-plena/anio/{anio}")
    public ResponseEntity<Resource> getExcelPlena(
            @PathVariable String idCliente,
            @PathVariable String anio,
            @RequestParam String ruc,
            @RequestParam String razonSocial) {
        try {
            byte[] excel = excelService.generateActivosFijosExcel(idCliente, anio, ruc, razonSocial);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RegActivosFijos.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new ByteArrayResource(excel));
        } catch (Exception e) {
            log.error("Error al generar excel plena para cliente: {} - {}", idCliente, razonSocial, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cliente/{idCliente}/reporte-excel")
    public ResponseEntity<Resource> getReporteExcel(
            @PathVariable String idCliente,
            @RequestParam String razonSocial) {
        try {
            byte[] excel = reporteExcelService.generateExcel(idCliente, razonSocial);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DEPRECIACIONES_" + razonSocial + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new ByteArrayResource(excel));
        } catch (Exception e) {
            log.error("Error al generar reporte excel para cliente: {} - {}", idCliente, razonSocial, e);
            // Devuelve el stack trace como body para facilitar el diagnóstico en producción
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            return ResponseEntity.internalServerError().body(new ByteArrayResource(sw.toString().getBytes()));
        }
    }
}
