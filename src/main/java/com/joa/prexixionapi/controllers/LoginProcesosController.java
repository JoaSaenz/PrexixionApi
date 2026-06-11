package com.joa.prexixionapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.joa.prexixionapi.entities.LoginProcesos;
import com.joa.prexixionapi.services.LoginProcesosService;
import com.joa.prexixionapi.services.LoginProcesosExcelService;
import com.joa.prexixionapi.dto.LoginProcesosDataTablesRequest;
import com.joa.prexixionapi.dto.LoginProcesosDataTablesResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/login-procesos")
@Slf4j
public class LoginProcesosController {

    @Autowired
    private LoginProcesosService loginProcesosService;

    @Autowired
    private LoginProcesosExcelService loginProcesosExcelService;

    @GetMapping("/server-side")
    public LoginProcesosDataTablesResponse listServerSide(LoginProcesosDataTablesRequest req) {
        try {
            return loginProcesosService.listServerSide(req);
        } catch (Exception e) {
            log.error("Error en listServerSide login-procesos para req: {}", req, e);
            throw e;
        }
    }

    @GetMapping
    public List<LoginProcesos> list(
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes,
            @RequestParam(required = false, defaultValue = "") String estados,
            @RequestParam(required = false, defaultValue = "") String grupos) {
        try {
            return loginProcesosService.list(anio, mes, estados, grupos);
        } catch (Exception e) {
            log.error("Error al listar login procesos - anio: {}, mes: {}, estados: {}, grupos: {}", anio, mes, estados,
                    grupos, e);
            throw e;
        }
    }

    @GetMapping("/get-one")
    public LoginProcesos getOne(
            @RequestParam(required = true) String ruc,
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes) {
        try {
            return loginProcesosService.getOne(ruc, anio, mes);
        } catch (Exception e) {
            log.error("Error al obtener un login proceso - ruc: {}, anio: {}, mes: {}", ruc, anio, mes, e);
            throw e;
        }
    }

    @PostMapping
    public int insertUpdate(@RequestBody LoginProcesos lp) {
        try {
            return loginProcesosService.insertUpdate(lp);
        } catch (Exception e) {
            log.error("Error al insertar o actualizar login proceso - ruc: {}", lp.getRuc(), e);
            throw e;
        }
    }

    @GetMapping("/excel-download")
    public ResponseEntity<byte[]> excelDownload(
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes,
            @RequestParam(required = false, defaultValue = "") String estados,
            @RequestParam(required = false, defaultValue = "") String grupos) {
        try {
            byte[] excelContent = loginProcesosExcelService.exportarExcel(anio, mes, estados, grupos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "LoginProcesos.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel mensual login procesos", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/excel-diario-download")
    public ResponseEntity<byte[]> excelDiarioDownload(
            @RequestParam(required = true) String proceso,
            @RequestParam(required = true) String fechaI,
            @RequestParam(required = true) String fechaF,
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes) {
        try {
            byte[] excelContent = loginProcesosExcelService.exportarExcelDiario(proceso, fechaI, fechaF, anio, mes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "ReporteDiario_LoginProcesos.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel diario login procesos", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/excel-range-download")
    public ResponseEntity<byte[]> excelRangeDownload(
            @RequestParam(required = true) String pI,
            @RequestParam(required = true) String pF,
            @RequestParam(required = false, defaultValue = "") String estados,
            @RequestParam(required = false, defaultValue = "") String grupos) {
        try {
            byte[] excelContent = loginProcesosExcelService.exportarExcelRange(pI, pF, estados, grupos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "LoginProcesosAcumulado.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel range login procesos", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
