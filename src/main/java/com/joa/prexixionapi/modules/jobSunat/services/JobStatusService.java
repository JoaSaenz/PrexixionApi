package com.joa.prexixionapi.modules.jobSunat.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusDTO;
import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusLogDTO;
import com.joa.prexixionapi.modules.notificacion.dto.NotificacionDTO;
import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusLogProjection;
import com.joa.prexixionapi.modules.jobSunat.repositories.JobStatusLogRepository;
import com.joa.prexixionapi.modules.jobSunat.repositories.JobStatusRepository;
import com.joa.prexixionapi.modules.notificacion.dto.NotificacionProjection;
import com.joa.prexixionapi.modules.notificacion.repositories.NotificacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobStatusService {
    private final JobStatusRepository repo;
    private final JobStatusLogRepository logRepo;
    private final NotificacionRepository notificacionRepo;

    public JobStatusDTO obtener(String nombreJob) {
        return repo.findTopByNombreJobOrderByHoraInicioDesc(nombreJob)
                .orElse(JobStatusDTO.builder()
                        .nombreJob(nombreJob)
                        .estado("SIN_INICIAR")
                        .progreso(0.0)
                        .mensaje("Esperando ejecución...")
                        .build());
    }

    public List<JobStatusDTO> listarPorNombreJob(String nombreJob) {
        return repo.findTop7ByNombreJobOrderByHoraInicioDesc(nombreJob);
    }

    public JobStatusDTO obtenerPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("JobStatusDTO no encontrado"));
    }

    public List<JobStatusLogDTO> obtenerLogs(Long jobStatusId) {
        List<JobStatusLogProjection> projections = logRepo.findLogsWithEstadoByJobStatus(jobStatusId);
        log.info("Proyecciones de Logs obtenidas para jobStatusId {}: {}", jobStatusId, projections.size());

        return projections.stream().map(p -> {
            JobStatusLogDTO logEntry = new JobStatusLogDTO();
            logEntry.setId(p.getId());
            logEntry.setRuc(p.getRuc());
            logEntry.setY(p.getY());
            logEntry.setResultado(p.getResultado());
            logEntry.setMensaje(p.getMensaje());
            logEntry.setDuracionMs(p.getDuracionMs());
            logEntry.setFechaRegistro(p.getFechaRegistro());
            logEntry.setNuevasNotificaciones(p.getNuevasNotificaciones());
            logEntry.setEstadoCliente(p.getEstadoCliente());
            logEntry.setRazonSocial(p.getRazonSocial());
            logEntry.setIdEstado(p.getIdEstado());
            return logEntry;
        }).collect(java.util.stream.Collectors.toList());
    }

    public List<NotificacionDTO> obtenerNotificaciones(Long jobStatusId) {
        List<NotificacionProjection> projections = notificacionRepo
                .findNotificacionesWithEstadoByJobStatus(jobStatusId);
        log.info("Proyecciones de Notificaciones obtenidas para jobStatusId {}: {}", jobStatusId, projections.size());

        return projections.stream().map(p -> {
            NotificacionDTO n = new NotificacionDTO();
            n.setId(p.getId());
            n.setRuc(p.getRuc());
            n.setIdSunat(p.getIdSunat());
            n.setTitulo(p.getTitulo());
            n.setFecha(p.getFecha());
            n.setJobStatusId(p.getJobStatusId());
            n.setEstadoCliente(p.getEstadoCliente());
            n.setTipo(p.getTipo());
            n.setNombreCorto(p.getNombreCorto());
            n.setY(p.getY());
            n.setRazonSocial(p.getRazonSocial());
            n.setIdEstado(p.getIdEstado());
            return n;
        }).collect(java.util.stream.Collectors.toList());
    }
}
