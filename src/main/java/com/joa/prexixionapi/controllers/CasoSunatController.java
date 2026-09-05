package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ApiResponse;
import com.joa.prexixionapi.dto.CasoSunatDTO;
import com.joa.prexixionapi.dto.CasoSunatListDTO;
import com.joa.prexixionapi.dto.CasoSunatRequest;
import com.joa.prexixionapi.services.CasoSunatExcelService;
import com.joa.prexixionapi.services.CasoSunatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/caso-sunat")
@RequiredArgsConstructor
public class CasoSunatController {

    private final CasoSunatService service;
    private final CasoSunatExcelService excelService;

    @GetMapping
    public ResponseEntity<List<CasoSunatListDTO>> list(CasoSunatRequest request) {
        return ResponseEntity.ok(service.list(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CasoSunatDTO> getOne(@PathVariable Integer id) {
        CasoSunatDTO dto = service.getOne(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.exist(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody CasoSunatDTO dto) {
        int result = service.insertUpdate(dto);
        String message = result == 1 ? "Caso SUNAT registrado correctamente" : "Caso SUNAT actualizado correctamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable Integer id) {
        int result = service.delete(id);
        if (result > 0) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Caso SUNAT eliminado correctamente", result));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error al eliminar el Caso SUNAT", 0));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> listExcel(CasoSunatRequest request) {
        byte[] excelBytes = excelService.exportarExcel(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "REPORTE DE FISCALIZACIONES.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel-seguimiento")
    public ResponseEntity<byte[]> listExcelSeguimiento(CasoSunatRequest request) {
        byte[] excelBytes = excelService.exportarExcelSeguimiento(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "REPORTE DE SEGUIMIENTO.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
