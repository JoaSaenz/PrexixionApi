package com.joa.prexixionapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.joa.prexixionapi.entities.JobStatus;
import com.joa.prexixionapi.entities.JobStatusLog;
import com.joa.prexixionapi.entities.Notificacion;
import com.joa.prexixionapi.services.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/job-status")
@RequiredArgsConstructor
@Slf4j
public class JobStatusController {

    private final JobStatusService service;

    @GetMapping("/{nombreJob}")
    public JobStatus obtenerEstado(@PathVariable String nombreJob) {
        try {
            return service.obtener(nombreJob);
        } catch (Exception e) {
            log.error("Error al obtener estado para job: {}", nombreJob, e);
            throw e;
        }
    }

    @GetMapping("/detail/{id}")
    public JobStatus obtenerPorId(@PathVariable Long id) {
        try {
            return service.obtenerPorId(id);
        } catch (Exception e) {
            log.error("Error al obtener job por id: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/list/{nombreJob}")
    public List<JobStatus> listarEjecuciones(@PathVariable String nombreJob) {
        try {
            return service.listarPorNombreJob(nombreJob);
        } catch (Exception e) {
            log.error("Error al listar ejecuciones para job: {}", nombreJob, e);
            throw e;
        }
    }

    @GetMapping("/{jobStatusId}/logs")
    public List<JobStatusLog> obtenerLogs(@PathVariable Long jobStatusId) {
        try {
            return service.obtenerLogs(jobStatusId);
        } catch (Exception e) {
            log.error("Error al obtener logs para jobStatusId: {}", jobStatusId, e);
            throw e;
        }
    }

    @GetMapping("/{jobStatusId}/notificaciones")
    public List<Notificacion> obtenerNotificaciones(@PathVariable Long jobStatusId) {
        try {
            return service.obtenerNotificaciones(jobStatusId);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones para jobStatusId: {}", jobStatusId, e);
            throw e;
        }
    }
}
