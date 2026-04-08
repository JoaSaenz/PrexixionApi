package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ActivoDTO;
import com.joa.prexixionapi.dto.ActivoExcelDTO;
import com.joa.prexixionapi.services.ActivoExcelService;
import com.joa.prexixionapi.services.ActivoService;
import lombok.RequiredArgsConstructor;
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
public class ActivoController {

    private final ActivoService activoService;
    private final ActivoExcelService excelService;

    @GetMapping("/cliente/{idCliente}")
    public List<ActivoDTO> getActivosByCliente(@PathVariable String idCliente) {
        return activoService.getActivosByIdCliente(idCliente);
    }

    @GetMapping("/cliente/{idCliente}/bien/{id}")
    public ActivoDTO getActivoDetalle(@PathVariable String idCliente, @PathVariable String id) {
        return activoService.getActivoDetalle(idCliente, id);
    }

    @PostMapping("/save")
    public int saveActivo(@RequestBody ActivoDTO activo, @RequestParam(defaultValue = "false") boolean calcular) {
        return activoService.saveActivo(activo, calcular);
    }

    @PostMapping("/preview-depreciacion")
    public ActivoDTO previewDepreciacion(@RequestBody ActivoDTO activo) {
        // En el legacy, 'depreciar' devolvía el objeto Activo con las listas calculadas
        activo.setDepreciacionesContables(activoService.getDepreciacionService().calcularDepreciacion(activo, 1, null));
        System.out.println("DEP TRIB: " + activo.getPorcentajeDepreciacionTributaria());
        activo.setDepreciacionesTributarias(activoService.getDepreciacionService().calcularDepreciacion(activo, 2, null));
        return activo;
    }

    @DeleteMapping("/cliente/{idCliente}/bien/{id}")
    public int deleteActivo(@PathVariable String idCliente, @PathVariable String id) {
        return activoService.deleteActivo(idCliente, id);
    }

    @PutMapping("/cliente/{idCliente}/bloqueo/{status}")
    public int updateBloqueo(@PathVariable String idCliente, @RequestBody List<String> ids, @PathVariable int status) {
        return activoService.updateBloqueo(idCliente, ids, status);
    }

    @GetMapping("/cliente/{idCliente}/excel-data")
    public ActivoExcelDTO getActivosExcelData(@PathVariable String idCliente) {
        return activoService.getActivosExcelData(idCliente);
    }

    @GetMapping("/cliente/{idCliente}/fijos-rv")
    public List<ActivoDTO> getActivosFijosRVExcel(@PathVariable String idCliente) {
        return activoService.getActivosFijosRVExcel(idCliente);
    }

    @GetMapping("/cliente/{idCliente}/fijos-anio/{anio}")
    public List<ActivoDTO> getActivosFijosExcelWithDepreciations(@PathVariable String idCliente, @PathVariable String anio) {
        return activoService.getActivosFijosExcelWithDepreciations(idCliente, anio);
    }

    @PostMapping("/upload")
    public int uploadActivos(@RequestBody List<ActivoDTO> list) {
        return activoService.uploadActivos(list);
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
            return ResponseEntity.internalServerError().build();
        }
    }
}
