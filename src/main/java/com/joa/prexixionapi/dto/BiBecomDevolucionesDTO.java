package com.joa.prexixionapi.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiBecomDevolucionesDTO {
    private int casosTotales;
    private BigDecimal montoSolicitado;
    private BigDecimal tasaAprobacionPct;
    private BigDecimal montoAprobado;
    private BigDecimal casosFinalizadosPct;
    private int casosPendientes;
    private int antiguedadPromedioDias;
    private BigDecimal montoTotalDenegado;
    private BigDecimal montoTotalPendiente;
    private List<BiBecomDevolucionItemDTO> denegadasList;
    private List<BiBecomDevolucionItemDTO> pendientesList;
}
