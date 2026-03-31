package com.joa.prexixionapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.entities.NotificacionAdjunto;

@Repository
public interface NotificacionAdjuntoRepository extends JpaRepository<NotificacionAdjunto, Long> {
}
