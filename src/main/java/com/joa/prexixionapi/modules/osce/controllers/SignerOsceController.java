package com.joa.prexixionapi.modules.osce.controllers;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.modules.osce.dto.SignerOsceDTO;
import com.joa.prexixionapi.modules.osce.dto.SignerOsceListDTO;
import com.joa.prexixionapi.modules.osce.dto.SignerOsceRequest;
import com.joa.prexixionapi.modules.osce.services.SignerOsceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.joa.prexixionapi.modules.osce.services.SignerOsceExcelService;

@Slf4j
@RestController
@RequestMapping("/api/signer-osce")
@RequiredArgsConstructor
public class SignerOsceController {

    private final SignerOsceService service;
    private final SignerOsceExcelService excelService;

    @PostMapping("/list")
    public ResponseEntity<List<SignerOsceListDTO>> list(@RequestBody SignerOsceRequest request) {
        return ResponseEntity.ok(service.list(request));
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(SignerOsceRequest request) throws Exception {
        byte[] excelBytes = excelService.excel(request);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType
                .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Signer_OSCE.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(excelBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/getOne/{idCliente}")
    public ResponseEntity<SignerOsceDTO> getOne(@PathVariable String idCliente) throws Exception {
        SignerOsceDTO dto = service.getOne(idCliente);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/exist/{idCliente}")
    public ResponseEntity<Boolean> exist(@PathVariable String idCliente) throws Exception {
        return ResponseEntity.ok(service.exist(idCliente));
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody SignerOsceDTO dto) {
        int result = service.insertUpdate(dto);
        String message = result == 1 ? "Registrado correctamente" : "Actualizado correctamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @DeleteMapping("/delete/{idCliente}")
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable String idCliente) {
        int result = service.delete(idCliente);
        return ResponseEntity.ok(new ApiResponse<>(true, "Eliminado correctamente", result));
    }
}
