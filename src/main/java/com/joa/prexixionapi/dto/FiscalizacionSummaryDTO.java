package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiscalizacionSummaryDTO {
    private String razonSocial;
    private String tipo;
    private String estado;
    private String fechaLimite;
    private String responsable;
}
