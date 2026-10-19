package com.joa.prexixionapi.modules.reunion.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.modules.reunion.dto.ReunionRequest;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionListDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionDTO;
import com.joa.prexixionapi.modules.reunion.services.ReunionService;
import com.joa.prexixionapi.modules.reunion.services.ReunionReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reuniones")
public class ReunionController {

    private final ReunionService reunionService;

    private final ReunionReportService reunionReportService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<ReunionListDTO>> list(@RequestBody ReunionRequest req) {
        return ResponseEntity.ok(reunionService.list(req));
    }

    @GetMapping("/getOne/{idReunion}")
    public ResponseEntity<ReunionDTO> getOne(@PathVariable int idReunion) throws Exception {
        ReunionDTO dto = reunionService.getOne(idReunion);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<ReunionDTO>> insertUpdate(@RequestBody ReunionDTO reunion) {
        boolean isNew = reunion.getId() == 0;
        ReunionDTO saved = reunionService.insertUpdate(reunion);
        String message = isNew ? "Reunión creada exitosamente" : "Reunión actualizada exitosamente";
        return ResponseEntity.ok(new ApiResponse<>(true, message, saved));
    }

    @DeleteMapping("/delete/{idReunion}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int idReunion) {
        reunionService.delete(idReunion);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reunión eliminada exitosamente", null));
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(ReunionRequest req) throws Exception {
        byte[] fileBytes = reunionReportService.generateExcel(req);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "REUNIONES.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/pdf/{idReunion}")
    public ResponseEntity<byte[]> pdf(@PathVariable int idReunion) throws Exception {
        byte[] fileBytes = reunionReportService.pdf(idReunion);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ADVICE_REUNION_" + idReunion + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
