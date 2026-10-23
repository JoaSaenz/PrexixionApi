package com.joa.prexixionapi.modules.gestionSide.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideListDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideRequest;
import com.joa.prexixionapi.modules.gestionSide.services.GestionSideExcelService;
import com.joa.prexixionapi.modules.gestionSide.services.GestionSideService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gestion-side")
@Slf4j
public class GestionSideController {

    private final GestionSideService gestionSideService;
    private final GestionSideExcelService gestionSideExcelService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<GestionSideListDTO>> list(@RequestBody GestionSideRequest req) {
        return ResponseEntity.ok(gestionSideService.list(req));
    }

    @GetMapping("/getOne/{id}")
    public ResponseEntity<GestionSideDTO> getOne(@PathVariable("id") int id) {
        GestionSideDTO dto = gestionSideService.getOne(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestBody GestionSideDTO dto) {
        int result = gestionSideService.insertUpdate(dto);
        String message = result == 1 ? "Registro creado exitosamente" : "Registro actualizado exitosamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, result));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") int id) {
        gestionSideService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Registro eliminado exitosamente", null));
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> excel(@RequestBody GestionSideRequest req) {
        byte[] excelContent = gestionSideExcelService.excel(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "ReporteGeneral_GestionSide.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }
}
