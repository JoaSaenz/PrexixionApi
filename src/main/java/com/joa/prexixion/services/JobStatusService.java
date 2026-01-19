package com.joa.prexixion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.joa.prexixion.entities.JobStatus;
import com.joa.prexixion.entities.JobStatusLog;
import com.joa.prexixion.entities.Notificacion;
import com.joa.prexixion.repositories.JobStatusLogProjection;
import com.joa.prexixion.repositories.JobStatusLogRepository;
import com.joa.prexixion.repositories.JobStatusRepository;
import com.joa.prexixion.repositories.NotificacionProjection;
import com.joa.prexixion.repositories.NotificacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobStatusService {
    private final JobStatusRepository repo;
    private final JobStatusLogRepository logRepo;
    private final NotificacionRepository notificacionRepo;

    public JobStatus obtener(String nombreJob) {
        return repo.findTopByNombreJobOrderByHoraInicioDesc(nombreJob)
                .orElse(JobStatus.builder()
                        .nombreJob(nombreJob)
                        .estado("SIN_INICIAR")
                        .progreso(0.0)
                        .mensaje("Esperando ejecución...")
                        .build());
    }

    public List<JobStatus> listarPorNombreJob(String nombreJob) {
        return repo.findTop7ByNombreJobOrderByHoraInicioDesc(nombreJob);
    }

    public JobStatus obtenerPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("JobStatus no encontrado"));
    }

    public List<JobStatusLog> obtenerLogs(Long jobStatusId) {
        List<JobStatusLogProjection> projections = logRepo.findLogsWithEstadoByJobStatus(jobStatusId);
        log.info("Proyecciones de Logs obtenidas para jobStatusId {}: {}", jobStatusId, projections.size());

        return projections.stream().map(p -> {
            JobStatusLog logEntry = new JobStatusLog();
            logEntry.setId(p.getId());
            logEntry.setRuc(p.getRuc());
            logEntry.setY(p.getY());
            logEntry.setResultado(p.getResultado());
            logEntry.setMensaje(p.getMensaje());
            logEntry.setDuracionMs(p.getDuracionMs());
            logEntry.setFechaRegistro(p.getFechaRegistro());
            logEntry.setNuevasNotificaciones(p.getNuevasNotificaciones());
            logEntry.setEstadoCliente(p.getEstadoCliente());
            return logEntry;
        }).collect(java.util.stream.Collectors.toList());
    }

    public List<Notificacion> obtenerNotificaciones(Long jobStatusId) {
        List<NotificacionProjection> projections = notificacionRepo
                .findNotificacionesWithEstadoByJobStatus(jobStatusId);
        log.info("Proyecciones de Notificaciones obtenidas para jobStatusId {}: {}", jobStatusId, projections.size());

        return projections.stream().map(p -> {
            Notificacion n = new Notificacion();
            n.setId(p.getId());
            n.setRuc(p.getRuc());
            n.setIdSunat(p.getIdSunat());
            n.setTitulo(p.getTitulo());
            n.setFecha(p.getFecha());
            n.setJobStatusId(p.getJobStatusId());
            n.setEstadoCliente(p.getEstadoCliente());
            return n;
        }).collect(java.util.stream.Collectors.toList());
    }
}
