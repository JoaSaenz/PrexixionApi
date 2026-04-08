package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoTipoDTO {
    private String id;
    private String descripcion;
    private Double depreciacionContable;
    private Double depreciacionTributaria;
}
