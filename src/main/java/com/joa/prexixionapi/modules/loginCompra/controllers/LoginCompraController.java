package com.joa.prexixionapi.modules.loginCompra.controllers;

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
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraDTO;
import com.joa.prexixionapi.modules.loginCompra.services.LoginCompraExcelService;
import com.joa.prexixionapi.modules.loginCompra.controllers.LoginCompraController;
import com.joa.prexixionapi.modules.loginCompra.services.LoginCompraService;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraRequest;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraListDTO;

@RestController
@RequestMapping("/api/login-compras")
@RequiredArgsConstructor
@Slf4j
public class LoginCompraController {

    private final LoginCompraService loginCompraService;
    private final LoginCompraExcelService loginCompraExcelService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<LoginCompraListDTO>> list(@RequestBody LoginCompraRequest req)
            throws Exception {
        return ResponseEntity.ok(loginCompraService.list(req));
    }

    @GetMapping("/getVencimientoLimits")
    public ResponseEntity<String[]> getVencimientoLimits(
            @RequestParam(required = true) String periodoI,
            @RequestParam(required = true) String periodoF) {
        return ResponseEntity.ok(loginCompraService.getVencimientoLimits(periodoI, periodoF));
    }

    @GetMapping("/getOne/{ruc}/{anio}/{mes}")
    public ResponseEntity<LoginCompraDTO> getOne(
            @PathVariable("ruc") String ruc,
            @PathVariable("anio") String anio,
            @PathVariable("mes") String mes) {
        LoginCompraDTO dto = loginCompraService.getOne(ruc, anio, mes);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody LoginCompraDTO dto) {
        int result = loginCompraService.insertUpdate(dto);
        String message = result == 1 ? "Creado exitosamente" : "Actualizado exitosamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> excel(@RequestBody LoginCompraRequest req) {
        byte[] excelContent = loginCompraExcelService.excel(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "LoginCompras.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @PostMapping("/excelDiario")
    public ResponseEntity<byte[]> excelDiario(@RequestBody LoginCompraRequest req) {
        byte[] excelContent = loginCompraExcelService.excelDiario(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "ReporteDiario_LoginCompras.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }
}
