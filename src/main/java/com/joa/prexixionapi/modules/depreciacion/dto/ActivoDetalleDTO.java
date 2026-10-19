package com.joa.prexixionapi.modules.depreciacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoDetalleDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoDetalleDTO {
    private ActivoDTO activo;
    private List<ActivoDepreciacionDTO> depreciacionesContables;
    private List<ActivoDepreciacionDTO> depreciacionesTributarias;
}
