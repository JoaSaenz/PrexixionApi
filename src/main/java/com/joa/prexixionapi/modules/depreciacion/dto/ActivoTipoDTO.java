package com.joa.prexixionapi.modules.depreciacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoTipoDTO;

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
