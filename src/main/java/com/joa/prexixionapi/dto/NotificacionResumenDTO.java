package com.joa.prexixionapi.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResumenDTO {
    private Long id;
    private String ruc;
    private String idSunat;
    private String titulo;
    private LocalDateTime fecha;
    private boolean revisado;
    private boolean tieneAdjuntos;
}
