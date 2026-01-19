package com.joa.prexixion.repositories;

import com.joa.prexixion.entities.JobStatus;
import com.joa.prexixion.entities.JobStatusLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobStatusLogRepository extends JpaRepository<JobStatusLog, Long> {

    List<JobStatusLog> findByJobStatusOrderByFechaRegistroAsc(JobStatus jobStatus);

    @org.springframework.data.jpa.repository.Query(value = """
            SELECT jsl.*, ce.descripcion as estadoCliente
            FROM jobStatusLog jsl
            LEFT JOIN cliente c ON jsl.ruc = c.ruc
            LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
            WHERE jsl.jobStatusId = :jobStatusId
            ORDER BY jsl.fechaRegistro ASC
            """, nativeQuery = true)
    List<JobStatusLogProjection> findLogsWithEstadoByJobStatus(
            @org.springframework.data.repository.query.Param("jobStatusId") Long jobStatusId);
}
