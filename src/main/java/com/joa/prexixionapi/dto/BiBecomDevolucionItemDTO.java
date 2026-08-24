package com.joa.prexixionapi.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiBecomDevolucionItemDTO {
    private String ruc;
    private String signer;
    private String procedimiento;
    private String tipo;
    private Integer idEstado;
    private String estado;
    private Integer idResultado;
    private String resultado;
    private String periodo;
    private String fechaSolicitud;
    private BigDecimal montoSolicitado;
    private BigDecimal montoAprobado;
    private BigDecimal importe;
    private BigDecimal saldoBn;
    private BigDecimal diferencia;
    private Integer idEstadoCheque;
    private String recoge;
}
