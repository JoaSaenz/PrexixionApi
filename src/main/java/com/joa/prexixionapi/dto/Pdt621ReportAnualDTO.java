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
public class Pdt621ReportAnualDTO {
    private String ruc;
    private String anio;
    private String mes;
    
    private double ventasG;
    private double comprasG;
    private double ventasNetas10;
    private double comprasNetas10;
    
    private double tasa;
    
    private double validerVentas;
    private double validerCompras;
    
    private double mesIgv;
    private double mesAnteriorIgv;
    private double ajusteIgv;
    private double resultadoIgv;
    
    private double mesPer;
    private double mesAnteriorPer;
    private double compensacionPer;
    private double ajustePer;
    private double resultadoPer;
    
    private double mesRet;
    private double mesAnteriorRet;
    private double compensacionRet;
    private double ajusteRet;
    private double resultadoRet;
    
    private double igvPorPagar;
    
    private double expFactPer;
    private double ventasNg;
    private double comprasNgE;
    private double comprasNg;
    
    private double ivapVentasGravadas;
    private double ivapTributo;
    
    private double baseRenta;
    private double mesRenta;
    private double mesAnteriorRenta;
    private double anualRenta;
    private double saldoFavorExportador;
    private double citanRenta;
    private double ajusteRenta;
    private double totalDeudaTributariaRenta;
    private double rentaPorPagar;

    private List<Pdt621RegistroDTO> registros;
    private String abrGestionRegimenTributario;
}
