package com.joa.prexixionapi.modules.loginProcesos.controllers;

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

import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;

import lombok.extern.slf4j.Slf4j;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosListDTO;
import com.joa.prexixionapi.modules.loginProcesos.services.LoginProcesosService;
import com.joa.prexixionapi.modules.loginProcesos.services.LoginProcesosExcelService;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login-procesos")
@Slf4j
public class LoginProcesosController {

    private final LoginProcesosService loginProcesosService;

    private final LoginProcesosExcelService loginProcesosExcelService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<LoginProcesosListDTO>> list(@RequestBody LoginProcesosRequest req)
            throws Exception {
        return ResponseEntity.ok(loginProcesosService.list(req));
    }

    @GetMapping("/getOne/{ruc}/{anio}/{mes}")
    public ResponseEntity<LoginProcesosDTO> getOne(
            @PathVariable("ruc") String ruc,
            @PathVariable("anio") String anio,
            @PathVariable("mes") String mes) {
        LoginProcesosDTO lp = loginProcesosService.getOne(ruc, anio, mes);
        return lp != null ? ResponseEntity.ok(lp) : ResponseEntity.notFound().build();
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody LoginProcesosDTO lp) {
        int result = loginProcesosService.insertUpdate(lp);
        String message = result == 1 ? "Creado exitosamente" : "Actualizado exitosamente";
        if (result == 10) {
            return ResponseEntity.status(409).body(new ApiResponse<>(false,
                    "Conflicto de concurrencia: el registro fue modificado por otro usuario", result));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> excel(@RequestBody LoginProcesosRequest req) {
        byte[] excelContent = loginProcesosExcelService.excel(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "LoginProcesos.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @PostMapping("/excelDiario")
    public ResponseEntity<byte[]> excelDiario(@RequestBody LoginProcesosRequest req) {
        byte[] excelContent = loginProcesosExcelService.excelDiario(req);

        String filename = "ReporteDiario_" + (req.getProceso() != null ? req.getProceso() : "Procesos") + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @PostMapping("/excelRange")
    public ResponseEntity<byte[]> excelRange(@RequestBody LoginProcesosRequest req) {
        byte[] excelContent = loginProcesosExcelService.excelRange(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "LoginProcesosAcumulado.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @PostMapping("/excelAvance")
    public ResponseEntity<byte[]> excelAvance(@RequestBody LoginProcesosRequest req) {
        byte[] excelContent = loginProcesosExcelService.excelAvance(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "AvanceMensualLoginProcesos.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @GetMapping("/getVencimientoLimits")
    public ResponseEntity<String[]> getVencimientoLimits(
            @RequestParam(required = true) String periodoI,
            @RequestParam(required = true) String periodoF) {
        return ResponseEntity.ok(loginProcesosService.getVencimientoLimits(periodoI, periodoF));
    }
}
