package com.joa.prexixionapi.modules.asistencia.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.joa.prexixionapi.modules.asistencia.controllers.AsistenciaController;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaStatsDTO;
import com.joa.prexixionapi.modules.asistencia.services.AsistenciaExcelService;
import com.joa.prexixionapi.modules.asistencia.services.AsistenciaService;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaDTO;

@RestController
@RequestMapping("/api/asistencia")
@RequiredArgsConstructor
@Slf4j
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final AsistenciaExcelService excelService;

    @GetMapping("/list")
    public ResponseEntity<List<AsistenciaDTO>> list(
            @RequestParam String fecha,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> types) {
        log.info("Request daily asistencia for date: {}, statuses: {}, types: {}", fecha, statuses, types);
        return ResponseEntity.ok(asistenciaService.list(fecha, statuses, types));
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> excel(
            @RequestParam String fecha,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> types) throws IOException {
        log.info("Exporting daily asistencia for date: {}, filters: true", fecha);
        List<AsistenciaDTO> data = asistenciaService.list(fecha, statuses, types);
        byte[] excelContent = excelService.excel(fecha, data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asistencia_" + fecha + ".xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excelContent));
    }

    @GetMapping("/stats")
    public ResponseEntity<AsistenciaStatsDTO> stats(@RequestParam String fechaI, @RequestParam String fechaF)
            throws Exception {
        log.info("Request asistencia stats from {} to {}", fechaI, fechaF);
        return ResponseEntity.ok(asistenciaService.stats(fechaI, fechaF));
    }


    @GetMapping("/logs")
    public ResponseEntity<List<Map<String, Object>>> getRawLogs(@RequestParam String dni, @RequestParam String fecha) {
        log.info("Request raw logs for DNI: {} on date: {}", dni, fecha);
        return ResponseEntity.ok(asistenciaService.getRawLogs(dni, fecha));
    }

    @GetMapping("/mensual")
    public ResponseEntity<List<AsistenciaDTO>> mensual(@RequestParam String dni, @RequestParam String fechaI,
            @RequestParam String fechaF) {
        return ResponseEntity.ok(asistenciaService.mensual(dni, fechaI, fechaF));
    }

}
