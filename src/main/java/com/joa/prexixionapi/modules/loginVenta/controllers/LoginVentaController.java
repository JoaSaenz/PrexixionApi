package com.joa.prexixionapi.modules.loginVenta.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaListDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaRequest;
import com.joa.prexixionapi.modules.loginVenta.services.LoginVentaExcelService;
import com.joa.prexixionapi.modules.loginVenta.services.LoginVentaService;
import com.joa.prexixionapi.modules.loginVenta.controllers.LoginVentaController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login-ventas")
@Slf4j
public class LoginVentaController {

    private final LoginVentaService loginVentaService;
    private final LoginVentaExcelService loginVentaExcelService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<LoginVentaListDTO>> list(@RequestBody LoginVentaRequest req)
            throws Exception {
        return ResponseEntity.ok(loginVentaService.list(req));
    }

    @GetMapping("/getOne/{ruc}/{anio}/{mes}")
    public ResponseEntity<LoginVentaDTO> getOne(
            @PathVariable("ruc") String ruc,
            @PathVariable("anio") String anio,
            @PathVariable("mes") String mes) {
        LoginVentaDTO dto = loginVentaService.getOne(ruc, anio, mes);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody LoginVentaDTO cliente) {
        int result = loginVentaService.insertUpdate(cliente);
        String message = result == 1 ? "Creado exitosamente" : "Actualizado exitosamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> excel(@RequestBody LoginVentaRequest req) {
        byte[] excelContent = loginVentaExcelService.excel(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "LoginVentas.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @PostMapping("/excelDiario")
    public ResponseEntity<byte[]> excelDiario(@RequestBody LoginVentaRequest req) {
        byte[] excelContent = loginVentaExcelService.excelDiario(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "ReporteDiario_LoginVentas.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @GetMapping("/getVencimientoLimits")
    public ResponseEntity<String[]> getVencimientoLimits(
            @RequestParam(required = true) String periodoI,
            @RequestParam(required = true) String periodoF) {
        return ResponseEntity.ok(loginVentaService.getVencimientoLimits(periodoI, periodoF));
    }
}
