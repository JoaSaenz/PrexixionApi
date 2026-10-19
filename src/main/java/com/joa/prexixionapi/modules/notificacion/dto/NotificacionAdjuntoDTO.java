package com.joa.prexixionapi.modules.notificacion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificacionAdjuntoDTO {

    private Long id;

    private String nombre;

    private byte[] contenido;

    private NotificacionDTO notificacion;
}
