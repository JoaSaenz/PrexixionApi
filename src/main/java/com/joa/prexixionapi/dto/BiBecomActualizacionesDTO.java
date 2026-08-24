package com.joa.prexixionapi.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiBecomActualizacionesDTO {
    private int totalRegistros;
    private int totalTerminados;
    private int totalEnProceso;
    private int totalPendientes;
    private int totalRetiradosSinPago;
    private List<BiBecomActualizacionItemDTO> activosList;
    private List<BiBecomActualizacionItemDTO> retiradosList;
}
