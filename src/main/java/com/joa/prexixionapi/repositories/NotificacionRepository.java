package com.joa.prexixionapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.entities.Notificacion;
import com.joa.prexixionapi.dto.NotificacionProjection;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

        List<Notificacion> findByJobStatusIdOrderByFechaDesc(Long jobStatusId);

        @org.springframework.data.jpa.repository.Query(value = """
                        SELECT n.*, ce.descripcion as estadoCliente
                        FROM sunatBuzonNotificacion n
                        LEFT JOIN cliente c ON n.ruc = c.ruc
                        LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
                        WHERE n.jobStatusId = :jobStatusId
                        ORDER BY n.fecha DESC
                        """, nativeQuery = true)
        List<NotificacionProjection> findNotificacionesWithEstadoByJobStatus(
                        @org.springframework.data.repository.query.Param("jobStatusId") Long jobStatusId);


        @org.springframework.data.jpa.repository.Query(value = """
                        SELECT n.id, n.ruc, n.idSunat, n.titulo, n.fecha, n.revisado, 
                               (CASE WHEN EXISTS (SELECT 1 FROM sunatBuzonAdjunto a WHERE a.notificacion_id = n.id) THEN 1 ELSE 0 END) as tieneAdjuntos
                        FROM sunatBuzonNotificacion n
                        WHERE n.ruc = :ruc
                        ORDER BY n.fecha DESC
                        """, nativeQuery = true)
        List<NotificacionProjection> findResumenByRuc(@org.springframework.data.repository.query.Param("ruc") String ruc);

        List<Notificacion> findByRucAndFechaBetween(String ruc, java.time.LocalDateTime inicio,
                        java.time.LocalDateTime fin);

}
