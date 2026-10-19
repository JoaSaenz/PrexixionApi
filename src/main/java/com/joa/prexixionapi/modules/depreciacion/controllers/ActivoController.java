package com.joa.prexixionapi.modules.depreciacion.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.modules.depreciacion.services.ActivoService;
import com.joa.prexixionapi.modules.depreciacion.services.ActivoExcelService;
import com.joa.prexixionapi.modules.depreciacion.services.DepreciacionReporteExcelService;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoExcelDTO;
import com.joa.prexixionapi.modules.depreciacion.controllers.ActivoController;
import com.joa.prexixionapi.modules.depreciacion.services.DepreciacionService;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoDTO;

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
    public ResponseEntity<List<ActivoDTO>> getActivosByCliente(@PathVariable String idCliente) {
        return ResponseEntity.ok(activoService.getActivosByIdCliente(idCliente));
    }

    @GetMapping("/cliente/{idCliente}/bien/{id}")
    public ResponseEntity<ActivoDTO> getActivoDetalle(@PathVariable String idCliente, @PathVariable String id) throws Exception  {
        ActivoDTO dto = activoService.getActivoDetalle(idCliente, id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Integer>> saveActivo(@RequestBody ActivoDTO activo, @RequestParam(defaultValue = "false") boolean calcular) {
        int id = activoService.saveActivo(activo, calcular);
        String message = activo.getId() == null || activo.getId().trim().isEmpty() ? "Activo creado exitosamente" : "Activo actualizado exitosamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, id));
    }

    @PostMapping("/preview-depreciacion")
    public ResponseEntity<ActivoDTO> previewDepreciacion(@RequestBody ActivoDTO activo) throws Exception  {
        // Calcula las depreciaciones contable y tributaria sin persistir, para previsualizar en la UI
        activo.setDepreciacionesContables(depreciacionService.calcularDepreciacion(activo, 1, null));
        activo.setDepreciacionesTributarias(depreciacionService.calcularDepreciacion(activo, 2, null));
        return ResponseEntity.ok(activo);
    }

    @DeleteMapping("/cliente/{idCliente}/bien/{id}")
    public ResponseEntity<ApiResponse<Integer>> deleteActivo(@PathVariable String idCliente, @PathVariable String id) {
        int result = activoService.deleteActivo(idCliente, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activo eliminado exitosamente", result));
    }

    @PutMapping("/cliente/{idCliente}/bloqueo/{status}")
    public ResponseEntity<ApiResponse<Integer>> updateBloqueo(@PathVariable String idCliente, @RequestBody List<String> ids, @PathVariable int status) {
        int result = activoService.updateBloqueo(idCliente, ids, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Estado de bloqueo actualizado exitosamente", result));
    }

    @GetMapping("/cliente/{idCliente}/excel-data")
    public ResponseEntity<ActivoExcelDTO> getActivosExcelData(@PathVariable String idCliente) throws Exception  {
        ActivoExcelDTO dto = activoService.getActivosExcelData(idCliente);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cliente/{idCliente}/fijos-rv")
    public ResponseEntity<List<ActivoDTO>> getActivosFijosRVExcel(@PathVariable String idCliente) {
        return ResponseEntity.ok(activoService.getActivosFijosRVExcel(idCliente));
    }

    @GetMapping("/cliente/{idCliente}/fijos-anio/{anio}")
    public ResponseEntity<List<ActivoDTO>> getActivosFijosExcelWithDepreciations(@PathVariable String idCliente, @PathVariable String anio) {
        return ResponseEntity.ok(activoService.getActivosFijosExcelWithDepreciations(idCliente, anio));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Integer>> uploadActivos(@RequestBody List<ActivoDTO> list) {
        int result = activoService.uploadActivos(list);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activos subidos exitosamente", result));
    }

    @GetMapping("/cliente/{idCliente}/excel-plena/anio/{anio}")
    public ResponseEntity<Resource> getExcelPlena(
            @PathVariable String idCliente,
            @PathVariable String anio,
            @RequestParam String ruc,
            @RequestParam String razonSocial) throws Exception  {
        byte[] excel = excelService.generateActivosFijosExcel(idCliente, anio, ruc, razonSocial);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RegActivosFijos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excel));
    }

    @GetMapping("/cliente/{idCliente}/reporte-excel")
    public ResponseEntity<Resource> getReporteExcel(
            @PathVariable String idCliente,
            @RequestParam String razonSocial) throws Exception  {
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
