package com.joa.prexixionapi.common.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTablesResponseDTO<T> {
    private List<T> data;
    private List<IndicadorDTO> indicadores;
}
