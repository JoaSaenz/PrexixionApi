package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ApiResponse;
import com.joa.prexixionapi.dto.Bf3800DTO;
import com.joa.prexixionapi.services.Bf3800ExcelService;
import com.joa.prexixionapi.services.Bf3800Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bf3800")
@RequiredArgsConstructor
public class Bf3800Controller {

    private final Bf3800Service bf3800Service;
    private final Bf3800ExcelService excelService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Bf3800DTO>>> list(
            @RequestParam String periodos,
            @RequestParam(defaultValue = "1,2,3,4,5,6,7,8") String estados,
            @RequestParam(defaultValue = "0,1,2,3,4,5,6,7,8,9,b") String grupos) {
        
        log.info("GET /api/bf3800 - periodos: {}, estados: {}, grupos: {}", periodos, estados, grupos);
        List<Bf3800DTO> list = bf3800Service.list(periodos, estados, grupos);
        log.info("GET /api/bf3800 - Results found: {}", list.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "Listado cargado", list));
    }

    @GetMapping("/{ruc}/{anio}/{mes}")
    public ResponseEntity<ApiResponse<Bf3800DTO>> getOne(
            @PathVariable String ruc,
            @PathVariable String anio,
            @PathVariable String mes) {
        
        Bf3800DTO dto = bf3800Service.getOne(ruc, anio, mes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Detalle cargado", dto));
    }

    @GetMapping("/beneficiario/{id}")
    public ResponseEntity<ApiResponse<com.joa.prexixionapi.dto.BeneficiarioDTO>> getBeneficiarioById(@PathVariable int id) {
        com.joa.prexixionapi.dto.BeneficiarioDTO dto = bf3800Service.getBeneficiarioById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiario cargado", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> save(@RequestBody Bf3800DTO dto) {
        bf3800Service.save(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Datos guardados correctamente", null));
    }

    @GetMapping("/excel-download")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam String periodos,
            @RequestParam(defaultValue = "1,2,3,4,5,6,7,8") String estados,
            @RequestParam(defaultValue = "0,1,2,3,4,5,6,7,8,9,b") String grupos) throws IOException {
        
        List<Bf3800DTO> data = bf3800Service.list(periodos, estados, grupos);
        byte[] file = excelService.generateExcel(data);

        String filename = "BF3800_Reporte.xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
