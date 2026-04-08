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
public class ActivoExcelDTO {
    private List<ActivoDTO> activos;
    private String minFechaCompra;
    private String maxFechaContableTributaria;
}
