package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepreciacionResumenDTO {
    private String ruc;
    private String razonSocial;
    private String y;
    private Integer idEstado;
    private String estado;
    private String anioInicio;
    private String anioFin;
    
    // Valores numéricos sumados
    private Double activoSaldoInicial;
    private Double activoCompras;
    private Double activoRetiros;
    private Double activoSaldoFinal;
    
    private Double inicial;
    private Double total;
    private Double retiros;
    private Double saldoFinal;
    private Double activoFijo;
}
