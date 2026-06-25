package com.joa.prexixionapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.joa.prexixionapi.dto.ReunionDataTablesRequest;
import com.joa.prexixionapi.dto.ReunionDataTablesResponse;
import com.joa.prexixionapi.dto.ReunionDTO;
import com.joa.prexixionapi.services.ReunionService;
import com.joa.prexixionapi.services.ReunionReportService;

@RestController
@RequestMapping("/api/reuniones")
public class ReunionController {

    @Autowired
    private ReunionService reunionService;

    @Autowired
    private ReunionReportService reunionReportService;

    @GetMapping
    public ReunionDataTablesResponse list(ReunionDataTablesRequest req) {
        return reunionService.list(req);
    }

    @GetMapping("/{idReunion}")
    public ReunionDTO getById(@PathVariable int idReunion) {
        return reunionService.getById(idReunion);
    }

    @PostMapping
    public ReunionDTO save(@RequestBody ReunionDTO reunion) {
        return reunionService.save(reunion);
    }

    @DeleteMapping("/{idReunion}")
    public void delete(@PathVariable int idReunion) {
        reunionService.delete(idReunion);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcel(ReunionDataTablesRequest req) throws Exception {
        byte[] fileBytes = reunionReportService.generateExcel(req);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "REUNIONES.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/{idReunion}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable int idReunion) throws Exception {
        byte[] fileBytes = reunionReportService.generatePdf(idReunion);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ADVICE_REUNION_" + idReunion + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
