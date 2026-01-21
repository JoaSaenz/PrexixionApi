package com.joa.prexixion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.joa.prexixion.entities.NotificacionAdjunto;

@Repository
public interface NotificacionAdjuntoRepository extends JpaRepository<NotificacionAdjunto, Long> {
}
