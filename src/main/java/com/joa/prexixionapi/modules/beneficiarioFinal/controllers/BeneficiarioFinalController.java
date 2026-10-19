package com.joa.prexixionapi.modules.beneficiarioFinal.controllers;

import com.joa.prexixionapi.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalReporteDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.services.Bf3800ExcelService;
import com.joa.prexixionapi.modules.beneficiarioFinal.services.BeneficiarioFinalService;
import com.joa.prexixionapi.modules.beneficiarioFinal.controllers.BeneficiarioFinalController;

@Slf4j
@RestController
@RequestMapping("/api/bf3800")
@RequiredArgsConstructor
public class BeneficiarioFinalController {

    private final BeneficiarioFinalService bf3800Service;
    private final Bf3800ExcelService excelService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BeneficiarioFinalReporteDTO>>> list(
            @RequestParam String periodos,
            @RequestParam(defaultValue = "1,2,3,4,5,6,7,8") String estados,
            @RequestParam(defaultValue = "0,1,2,3,4,5,6,7,8,9,b") String grupos) {
        
        log.info("GET /api/bf3800 - periodos: {}, estados: {}, grupos: {}", periodos, estados, grupos);
        List<BeneficiarioFinalReporteDTO> list = bf3800Service.list(periodos, estados, grupos);
        log.info("GET /api/bf3800 - Results found: {}", list.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "Listado cargado", list));
    }

    @GetMapping("/{ruc}/{anio}/{mes}")
    public ResponseEntity<ApiResponse<BeneficiarioFinalReporteDTO>> getOne(
            @PathVariable String ruc,
            @PathVariable String anio,
            @PathVariable String mes) {
        
        BeneficiarioFinalReporteDTO dto = bf3800Service.getOne(ruc, anio, mes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Detalle cargado", dto));
    }

    @GetMapping("/beneficiario/{id}")
    public ResponseEntity<ApiResponse<BeneficiarioFinalDTO>> getBeneficiarioById(@PathVariable int id) {
        BeneficiarioFinalDTO dto = bf3800Service.getBeneficiarioById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiario cargado", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> save(@RequestBody BeneficiarioFinalReporteDTO dto) {
        bf3800Service.save(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Datos guardados correctamente", null));
    }

    @GetMapping("/excel-download")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam String periodos,
            @RequestParam(defaultValue = "1,2,3,4,5,6,7,8") String estados,
            @RequestParam(defaultValue = "0,1,2,3,4,5,6,7,8,9,b") String grupos) throws IOException {
        
        List<BeneficiarioFinalReporteDTO> data = bf3800Service.list(periodos, estados, grupos);
        byte[] file = excelService.generateExcel(data);

        String filename = "BF3800_Reporte.xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
