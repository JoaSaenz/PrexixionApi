package com.joa.prexixionapi.modules.jobSunat.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusDTO;
import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusLogDTO;
import com.joa.prexixionapi.modules.notificacion.dto.NotificacionDTO;
import com.joa.prexixionapi.modules.jobSunat.services.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/job-status")
@RequiredArgsConstructor
@Slf4j
public class JobStatusController {

    private final JobStatusService service;

    @GetMapping("/{nombreJob}")
    public ResponseEntity<JobStatusDTO> obtenerEstado(@PathVariable String nombreJob) throws Exception  {
        JobStatusDTO status = service.obtener(nombreJob);
        return status != null ? ResponseEntity.ok(status) : ResponseEntity.notFound().build();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<JobStatusDTO> obtenerPorId(@PathVariable Long id) throws Exception  {
        JobStatusDTO status = service.obtenerPorId(id);
        return status != null ? ResponseEntity.ok(status) : ResponseEntity.notFound().build();
    }

    @GetMapping("/list/{nombreJob}")
    public ResponseEntity<List<JobStatusDTO>> listarEjecuciones(@PathVariable String nombreJob) {
        return ResponseEntity.ok(service.listarPorNombreJob(nombreJob));
    }

    @GetMapping("/{jobStatusId}/logs")
    public ResponseEntity<List<JobStatusLogDTO>> obtenerLogs(@PathVariable Long jobStatusId) {
        return ResponseEntity.ok(service.obtenerLogs(jobStatusId));
    }

    @GetMapping("/{jobStatusId}/notificaciones")
    public ResponseEntity<List<NotificacionDTO>> obtenerNotificaciones(@PathVariable Long jobStatusId) {
        return ResponseEntity.ok(service.obtenerNotificaciones(jobStatusId));
    }
}
