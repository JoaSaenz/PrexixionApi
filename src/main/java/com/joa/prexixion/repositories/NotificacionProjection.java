package com.joa.prexixion.repositories;

import java.time.LocalDateTime;

public interface NotificacionProjection {
    Long getId();

    String getRuc();

    String getIdSunat();

    String getTitulo();

    LocalDateTime getFecha();

    Long getJobStatusId();

    String getEstadoCliente();
}
