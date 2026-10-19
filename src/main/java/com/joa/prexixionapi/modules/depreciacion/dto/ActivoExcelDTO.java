package com.joa.prexixionapi.modules.depreciacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoExcelDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoExcelDTO {
    private List<ActivoDTO> activos;
    private String minFechaCompra;
    private String maxFechaContableTributaria;
}
