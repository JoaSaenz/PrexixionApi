package com.joa.prexixionapi.modules.pdt621.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621AnualResponseDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621DTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621FilterRequestDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621HistorialDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ListDTO;
import com.joa.prexixionapi.modules.pdt621.services.Pdt621ExcelService;
import com.joa.prexixionapi.modules.pdt621.services.Pdt621Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pdt621")
@RequiredArgsConstructor
@Slf4j
public class Pdt621Controller {

    private final Pdt621Service pdt621Service;
    private final Pdt621ExcelService pdt621ExcelService;

    @GetMapping("/report/anual")
    public ResponseEntity<ApiResponse<Pdt621AnualResponseDTO>> getReportAnual(
            @RequestParam String ruc,
            @RequestParam String anios) {
        List<String> anioList = Arrays.asList(anios.split(","));
        return ResponseEntity.ok(new ApiResponse<>(true, "Reporte generado", pdt621Service.getPdt621ReportAnual(ruc, anioList)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Pdt621ListDTO>>> getPdt621List(
            @org.springframework.web.bind.annotation.ModelAttribute Pdt621FilterRequestDTO request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista obtenida", pdt621Service.getPdt621List(request)));
    }

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<Pdt621ListDTO>> list(@RequestBody Pdt621FilterRequestDTO req) {
        return ResponseEntity.ok(pdt621Service.getPdt621DataTables(req));
    }

    @GetMapping("/getOne/{ruc}/{anio}/{mes}")
    public ResponseEntity<Pdt621DTO> getOne(
            @PathVariable("ruc") String ruc,
            @PathVariable("anio") String anio,
            @PathVariable("mes") String mes) {
        return ResponseEntity.ok(pdt621Service.getOne(ruc, anio, mes));
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody Pdt621DTO dto) {
        int result = pdt621Service.insertUpdate(dto);
        String message = result == 1 ? "Creado exitosamente" : "Actualizado exitosamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @GetMapping("/historial/{ruc}")
    public ResponseEntity<ApiResponse<List<Pdt621HistorialDTO>>> getHistorial(
            @PathVariable("ruc") String ruc,
            @RequestParam(required = false) String periodoI,
            @RequestParam(required = false) String periodoF) {
        String pI = (periodoI != null && periodoI.length() >= 7) ? periodoI : "2020-01";
        String pF = (periodoF != null && periodoF.length() >= 7) ? periodoF : "2030-12";
        List<Pdt621HistorialDTO> list = pdt621Service.listHistorial(ruc, pI, pF);
        return ResponseEntity.ok(new ApiResponse<>(true, "Historial obtenido", list));
    }

    @GetMapping("/getVencimientoLimits")
    public ResponseEntity<String[]> getVencimientoLimits(
            @RequestParam String periodoI,
            @RequestParam String periodoF) {
        return ResponseEntity.ok(pdt621Service.getVencimientoLimits(periodoI, periodoF));
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> excel(@RequestBody Pdt621FilterRequestDTO req) {
        byte[] content = pdt621ExcelService.excel(req);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "PDT621.xlsx");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @PostMapping("/excelAcumulado")
    public ResponseEntity<byte[]> excelAcumulado(@RequestBody Pdt621FilterRequestDTO req) {
        byte[] content = pdt621ExcelService.excelAcumulado(req);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "PDT621Acumulado.xlsx");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @GetMapping("/excelCliente")
    public ResponseEntity<byte[]> excelCliente(
            @RequestParam String ruc,
            @RequestParam String anio,
            @RequestParam String mes) {
        byte[] content = pdt621ExcelService.excelCliente(ruc, anio, mes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "PDT621_" + ruc + "_" + anio + mes + ".xlsx");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
