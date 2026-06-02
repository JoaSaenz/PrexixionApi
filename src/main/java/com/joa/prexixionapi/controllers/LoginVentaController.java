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

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.services.LoginVentaService;
import com.joa.prexixionapi.services.LoginVentaExcelService;
import com.joa.prexixionapi.dto.LoginVentaDataTablesRequest;
import com.joa.prexixionapi.dto.LoginVentaDataTablesResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/login-ventas")
@Slf4j
public class LoginVentaController {

    @Autowired
    private LoginVentaService loginVentaService;

    @Autowired
    private LoginVentaExcelService loginVentaExcelService;

    @GetMapping("/server-side")
    public LoginVentaDataTablesResponse listServerSide(LoginVentaDataTablesRequest req) {
        try {
            return loginVentaService.listServerSide(req);
        } catch (Exception e) {
            log.error("Error en listServerSide login-ventas para req: {}", req, e);
            throw e;
        }
    }

    @GetMapping
    public List<Cliente> list(
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes,
            @RequestParam(required = false, defaultValue = "") String estados,
            @RequestParam(required = false, defaultValue = "") String grupos) {
        try {
            return loginVentaService.list(anio, mes, estados, grupos);
        } catch (Exception e) {
            log.error("Error al listar login ventas - anio: {}, mes: {}, estados: {}, grupos: {}", anio, mes, estados, grupos, e);
            throw e;
        }
    }

    @GetMapping("/get-one")
    public Cliente getOne(
            @RequestParam(required = true) String ruc,
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes) {
        try {
            return loginVentaService.getOne(ruc, anio, mes);
        } catch (Exception e) {
            log.error("Error al obtener un login venta - ruc: {}, anio: {}, mes: {}", ruc, anio, mes, e);
            throw e;
        }
    }

    @PostMapping
    public int insertUpdate(@RequestBody Cliente cliente) {
        try {
            return loginVentaService.insertUpdate(cliente);
        } catch (Exception e) {
            log.error("Error al insertar o actualizar login venta - ruc: {}", cliente.getRuc(), e);
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
            byte[] excelContent = loginVentaExcelService.exportarExcel(anio, mes, estados, grupos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "LoginVentas.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/excel-diario-download")
    public ResponseEntity<byte[]> excelDiarioDownload(
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes,
            @RequestParam(required = true) String proceso,
            @RequestParam(required = true) String fecha) {
        try {
            byte[] excelContent = loginVentaExcelService.exportarExcelDiario(anio, mes, proceso, fecha);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "ReporteDiario_LoginVentas.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel diario", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
