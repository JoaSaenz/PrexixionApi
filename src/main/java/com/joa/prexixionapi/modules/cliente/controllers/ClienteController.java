package com.joa.prexixionapi.modules.cliente.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import com.joa.prexixionapi.modules.cliente.services.ClienteExcelService;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesResponse;
import com.joa.prexixionapi.modules.cliente.services.ClienteClavesExcelService;
import com.joa.prexixionapi.modules.cliente.services.ClienteService;
import com.joa.prexixionapi.modules.cliente.controllers.ClienteController;
import com.joa.prexixionapi.modules.cliente.services.ClienteCuentaBancariaExcelService;
import com.joa.prexixionapi.modules.cliente.services.ClientePersonalExcelService;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cliente")
@Slf4j
public class ClienteController {

        private final ClienteService clienteService;

        private final ClienteExcelService clienteExcelService;

        private final ClienteClavesExcelService clienteClavesExcelService;

        private final ClientePersonalExcelService clientePersonalExcelService;

        private final ClienteCuentaBancariaExcelService clienteCuentaBancariaExcelService;

        @GetMapping({ "", "/server-side" })
        public ResponseEntity<ClienteDataTablesResponse> list(ClienteDataTablesRequest req) throws Exception {
                return ResponseEntity.ok(clienteService.list(req));
        }

        @GetMapping("/selector/empresas")
        public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getEmpresasSelector() {
                return ResponseEntity.ok(clienteService.getEmpresasSelector());
        }

        @GetMapping("/excel-download")
        public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false) String estados,
                        @RequestParam(required = false) String grupos) {
                byte[] excelBytes = clienteExcelService.exportarExcelCliente(estados, grupos);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Clientes.xlsx\"")
                                .contentType(
                                                MediaType.parseMediaType(
                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(excelBytes);
        }

        @GetMapping("/excel-claves-download")
        public ResponseEntity<byte[]> exportExcelClaves(@RequestParam(required = false) String estados,
                        @RequestParam(required = false) String grupos) {
                byte[] excelBytes = clienteClavesExcelService.exportarExcelClaves(estados, grupos);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"Clientes_Claves.xlsx\"")
                                .contentType(
                                                MediaType.parseMediaType(
                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(excelBytes);
        }

        @GetMapping("/excel-personal-download")
        public ResponseEntity<byte[]> exportExcelPersonal(@RequestParam(required = false) String estados,
                        @RequestParam(required = false) String grupos) {
                byte[] excelBytes = clientePersonalExcelService.exportarExcelPersonal(estados, grupos);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"Signers_Personal.xlsx\"")
                                .contentType(
                                                MediaType.parseMediaType(
                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(excelBytes);
        }

        @GetMapping("/excel-cci-download")
        public ResponseEntity<byte[]> exportExcelCCI(@RequestParam(required = false) String estados,
                        @RequestParam(required = false) String grupos) {
                byte[] excelBytes = clienteCuentaBancariaExcelService.exportarExcelCCI(estados, grupos);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ReporteCCI.xlsx\"")
                                .contentType(
                                                MediaType.parseMediaType(
                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(excelBytes);
        }
}
