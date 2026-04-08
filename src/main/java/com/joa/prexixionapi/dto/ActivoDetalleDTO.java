package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoDetalleDTO {
    private ActivoDTO activo;
    private List<ActivoDepreciacionDTO> depreciacionesContables;
    private List<ActivoDepreciacionDTO> depreciacionesTributarias;
}
