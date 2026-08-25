package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiBecomHeaderDTO {
    private String responsableNombre;
    private String fechaEmision;
    private String periodoNombre;
    private String anio;
    private String ecosistemaAreas;
}
