package com.joa.prexixion.repositories;

import java.time.LocalDateTime;

public interface JobStatusLogProjection {
    Long getId();

    String getRuc();

    String getY();

    String getResultado();

    String getMensaje();

    Long getDuracionMs();

    LocalDateTime getFechaRegistro();

    Integer getNuevasNotificaciones();

    String getEstadoCliente();
}
