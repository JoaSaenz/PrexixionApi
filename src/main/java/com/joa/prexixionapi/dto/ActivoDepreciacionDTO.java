package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoDepreciacionDTO {
    private String idCliente;
    private String idBien;
    private String anio;
    private Integer idTipo;
    private Double activoSaldoInicial;
    private Double activoCompras;
    private Double activoRetiros;
    private Double activoSaldoFinal;

    private Double inicial;
    private Double ene;
    private Double feb;
    private Double mar;
    private Double abr;
    private Double may;
    private Double jun;
    private Double jul;
    private Double ago;
    private Double sep;
    private Double oct;
    private Double nov;
    private Double dec;
    private Double total;
    private Double retiros;
    private Double saldoFinal;
    private Double activoFijo;
    
    // Para el resumen / Análisis
    private String idBienTipo;
    private String idBienTipoDescripcion;
    
    private Double inicialTrib;
    private Double eneTrib;
    private Double febTrib;
    private Double marTrib;
    private Double abrTrib;
    private Double mayTrib;
    private Double junTrib;
    private Double julTrib;
    private Double agoTrib;
    private Double sepTrib;
    private Double octTrib;
    private Double novTrib;
    private Double decTrib;
    private Double totalTrib;
    private Double retirosTrib;
    private Double saldoFinalTrib;
    private Double activoFijoTrib;
    
    private Double adicion;
    private Double deduccion;
    private Double totalAnalisis;
    
    // Excel Activo Fijo
    private Double valorHistorico;
    private Double acumuladoHistorico;
}
