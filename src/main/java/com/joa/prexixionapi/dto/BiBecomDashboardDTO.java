package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiBecomDashboardDTO {
    private BiBecomHeaderDTO header;
    private BiBecomDevolucionesDTO devoluciones;
    private BiBecomActualizacionesDTO actualizaciones;
}
