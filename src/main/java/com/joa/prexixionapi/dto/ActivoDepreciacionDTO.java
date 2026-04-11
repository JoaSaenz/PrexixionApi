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
    @Builder.Default private Double activoSaldoInicial = 0.0;
    @Builder.Default private Double activoCompras = 0.0;
    @Builder.Default private Double activoRetiros = 0.0;
    @Builder.Default private Double activoSaldoFinal = 0.0;

    @Builder.Default private Double inicial = 0.0;
    @Builder.Default private Double ene = 0.0;
    @Builder.Default private Double feb = 0.0;
    @Builder.Default private Double mar = 0.0;
    @Builder.Default private Double abr = 0.0;
    @Builder.Default private Double may = 0.0;
    @Builder.Default private Double jun = 0.0;
    @Builder.Default private Double jul = 0.0;
    @Builder.Default private Double ago = 0.0;
    @Builder.Default private Double sep = 0.0;
    @Builder.Default private Double oct = 0.0;
    @Builder.Default private Double nov = 0.0;
    @Builder.Default private Double dec = 0.0;
    @Builder.Default private Double total = 0.0;
    @Builder.Default private Double retiros = 0.0;
    @Builder.Default private Double saldoFinal = 0.0;
    @Builder.Default private Double activoFijo = 0.0;
    
    // Para el resumen / Análisis
    private String idBienTipo;
    private String idBienTipoDescripcion;
    
    @Builder.Default private Double inicialTrib = 0.0;
    @Builder.Default private Double eneTrib = 0.0;
    @Builder.Default private Double febTrib = 0.0;
    @Builder.Default private Double marTrib = 0.0;
    @Builder.Default private Double abrTrib = 0.0;
    @Builder.Default private Double mayTrib = 0.0;
    @Builder.Default private Double junTrib = 0.0;
    @Builder.Default private Double julTrib = 0.0;
    @Builder.Default private Double agoTrib = 0.0;
    @Builder.Default private Double sepTrib = 0.0;
    @Builder.Default private Double octTrib = 0.0;
    @Builder.Default private Double novTrib = 0.0;
    @Builder.Default private Double decTrib = 0.0;
    @Builder.Default private Double totalTrib = 0.0;
    @Builder.Default private Double retirosTrib = 0.0;
    @Builder.Default private Double saldoFinalTrib = 0.0;
    @Builder.Default private Double activoFijoTrib = 0.0;
    
    @Builder.Default private Double adicion = 0.0;
    @Builder.Default private Double deduccion = 0.0;
    @Builder.Default private Double totalAnalisis = 0.0;
    
    // Excel Activo Fijo
    @Builder.Default private Double valorHistorico = 0.0;
    @Builder.Default private Double acumuladoHistorico = 0.0;
}
