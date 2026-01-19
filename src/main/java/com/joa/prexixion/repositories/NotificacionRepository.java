package com.joa.prexixion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.Notificacion;

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

}
