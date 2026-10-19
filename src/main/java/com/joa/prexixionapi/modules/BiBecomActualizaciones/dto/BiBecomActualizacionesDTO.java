package com.joa.prexixionapi.modules.BiBecomActualizaciones.dto;

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
    private int casosActivos;
    private int casosRetirados;
    private List<BiBecomActualizacionItemDTO> activosList;
    private List<BiBecomActualizacionItemDTO> retiradosList;
}
