package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.ApiResponse;
import com.joa.prexixionapi.dto.SignerOsceDTO;
import com.joa.prexixionapi.dto.SignerOsceListDTO;
import com.joa.prexixionapi.dto.SignerOsceRequest;
import com.joa.prexixionapi.services.SignerOsceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/signer-osce")
@RequiredArgsConstructor
public class SignerOsceController {

    private final SignerOsceService service;
    private final com.joa.prexixionapi.services.SignerOsceExcelService excelService;

    @GetMapping
    public ResponseEntity<List<SignerOsceListDTO>> list(SignerOsceRequest request) {
        return ResponseEntity.ok(service.list(request));
    }
    
    @GetMapping("/excel")
    public ResponseEntity<byte[]> listExcel(SignerOsceRequest request) {
        byte[] excelBytes = excelService.exportarExcel(request);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Signer_OSCE.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(excelBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<SignerOsceDTO> getOne(@PathVariable String idCliente) {
        SignerOsceDTO dto = service.getOne(idCliente);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/check/{idCliente}")
    public ResponseEntity<Boolean> checkIdCliente(@PathVariable String idCliente) {
        return ResponseEntity.ok(service.exist(idCliente));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody SignerOsceDTO dto) {
        int result = service.insertUpdate(dto);
        String message = result == 1 ? "Registrado correctamente" : "Actualizado correctamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable String idCliente) {
        int result = service.delete(idCliente);
        return ResponseEntity.ok(new ApiResponse<>(true, "Eliminado correctamente", result));
    }
}
