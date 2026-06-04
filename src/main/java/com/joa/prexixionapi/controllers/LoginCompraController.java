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
import com.joa.prexixionapi.services.LoginCompraService;
import com.joa.prexixionapi.services.LoginCompraExcelService;
import com.joa.prexixionapi.dto.LoginCompraDataTablesRequest;
import com.joa.prexixionapi.dto.LoginCompraDataTablesResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/login-compras")
@Slf4j
public class LoginCompraController {

    @Autowired
    private LoginCompraService loginCompraService;

    @Autowired
    private LoginCompraExcelService loginCompraExcelService;

    @GetMapping("/server-side")
    public LoginCompraDataTablesResponse listServerSide(LoginCompraDataTablesRequest req) {
        try {
            return loginCompraService.listServerSide(req);
        } catch (Exception e) {
            log.error("Error en listServerSide login-compras para req: {}", req, e);
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
            return loginCompraService.list(anio, mes, estados, grupos);
        } catch (Exception e) {
            log.error("Error al listar login compras - anio: {}, mes: {}, estados: {}, grupos: {}", anio, mes, estados, grupos, e);
            throw e;
        }
    }

    @GetMapping("/get-one")
    public Cliente getOne(
            @RequestParam(required = true) String ruc,
            @RequestParam(required = true) String anio,
            @RequestParam(required = true) String mes) {
        try {
            return loginCompraService.getOne(ruc, anio, mes);
        } catch (Exception e) {
            log.error("Error al obtener un login compra - ruc: {}, anio: {}, mes: {}", ruc, anio, mes, e);
            throw e;
        }
    }

    @PostMapping
    public int insertUpdate(@RequestBody Cliente cliente) {
        try {
            return loginCompraService.insertUpdate(cliente);
        } catch (Exception e) {
            log.error("Error al insertar o actualizar login compra - ruc: {}", cliente.getRuc(), e);
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
            byte[] excelContent = loginCompraExcelService.exportarExcel(anio, mes, estados, grupos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "LoginCompras.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel login compras", e);
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
            byte[] excelContent = loginCompraExcelService.exportarExcelDiario(proceso, fecha, anio, mes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "ReporteDiario_LoginCompras.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al exportar excel diario login compras", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
