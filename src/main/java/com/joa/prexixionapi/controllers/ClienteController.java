package com.joa.prexixionapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.services.ClienteCuentaBancariaExcelService;
import com.joa.prexixionapi.services.ClienteClavesExcelService;
import com.joa.prexixionapi.services.ClienteExcelService;
import com.joa.prexixionapi.services.ClienteService;
import com.joa.prexixionapi.services.ClientePersonalExcelService;
import com.joa.prexixionapi.dto.ClienteDataTablesRequest;
import com.joa.prexixionapi.dto.ClienteDataTablesResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @Autowired
    ClienteExcelService clienteExcelService;

    @Autowired
    ClienteClavesExcelService clienteClavesExcelService;

    @Autowired
    ClientePersonalExcelService clientePersonalExcelService;

    @Autowired
    ClienteCuentaBancariaExcelService clienteCuentaBancariaExcelService;

    @GetMapping
    public List<Cliente> list() {
        return clienteService.list();
    }

    @GetMapping("/server-side")
    public ClienteDataTablesResponse listServerSide(ClienteDataTablesRequest req) {
        return clienteService.listServerSide(req);
    }

    @GetMapping("/excel-download")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam(required = false) String estados,
            @RequestParam(required = false) String grupos) {
        byte[] excelBytes = clienteExcelService.exportarExcelCliente(estados, grupos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Clientes.xlsx\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/excel-claves-download")
    public ResponseEntity<byte[]> downloadExcelClaves(@RequestParam(required = false) String estados,
            @RequestParam(required = false) String grupos) {
        byte[] excelBytes = clienteClavesExcelService.exportarExcelClaves(estados, grupos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Clientes_Claves.xlsx\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/excel-personal-download")
    public ResponseEntity<byte[]> downloadExcelPersonal(@RequestParam(required = false) String estados,
            @RequestParam(required = false) String grupos) {
        byte[] excelBytes = clientePersonalExcelService.exportarExcelPersonal(estados, grupos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Signers_Personal.xlsx\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/excel-cci-download")
    public ResponseEntity<byte[]> downloadExcelCCI(@RequestParam(required = false) String estados,
            @RequestParam(required = false) String grupos) {
        byte[] excelBytes = clienteCuentaBancariaExcelService.exportarExcelCCI(estados, grupos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ReporteCCI.xlsx\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
