package com.joa.prexixionapi.modules.pdt621.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdt621AnualResponseDTO {
    private List<Pdt621ReportAnualDTO> list;
    private TotalesDTO totales;

    // Lombok genera getTVentasG() y Jackson lo serializaría como "tventasG"; se fuerza el nombre exacto desde el campo.
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalesDTO {
        @JsonProperty("tVentasG") private double tVentasG;
        @JsonProperty("tComprasG") private double tComprasG;
        @JsonProperty("tVentasNetas10") private double tVentasNetas10;
        @JsonProperty("tCompraNetas10") private double tCompraNetas10;
        
        @JsonProperty("tMesIgv") private double tMesIgv;
        @JsonProperty("tAfavorIgv") private double tAfavorIgv;
        @JsonProperty("tAjusteIgv") private double tAjusteIgv;
        @JsonProperty("tResultadoIgv") private double tResultadoIgv;
        
        @JsonProperty("tMesPer") private double tMesPer;
        @JsonProperty("tAfavorPer") private double tAfavorPer;
        @JsonProperty("tCompensaPer") private double tCompensaPer;
        @JsonProperty("tAjustePer") private double tAjustePer;
        @JsonProperty("tResultadoPer") private double tResultadoPer;
        
        @JsonProperty("tMesRet") private double tMesRet;
        @JsonProperty("tAfavorRet") private double tAfavorRet;
        @JsonProperty("tCompensaRet") private double tCompensaRet;
        @JsonProperty("tAjusteRet") private double tAjusteRet;
        @JsonProperty("tResultadoRet") private double tResultadoRet;
        @JsonProperty("tIgvPorPagar") private double tIgvPorPagar;
        
        @JsonProperty("tExpFactPer") private double tExpFactPer;
        @JsonProperty("tVentasNg") private double tVentasNg;
        @JsonProperty("tComprasNgE") private double tComprasNgE;
        @JsonProperty("tComprasNg") private double tComprasNg;
        
        @JsonProperty("tIvapVentasGravadas") private double tIvapVentasGravadas;
        @JsonProperty("tIvapTributo") private double tIvapTributo;
        
        @JsonProperty("tBaseRenta") private double tBaseRenta;
        @JsonProperty("tMesRenta") private double tMesRenta;
        @JsonProperty("tMesAnteriorRenta") private double tMesAnteriorRenta;
        @JsonProperty("tAnualRenta") private double tAnualRenta;
        @JsonProperty("tSaldoFavorExportador") private double tSaldoFavorExportador;
        @JsonProperty("tCItanRenta") private double tCItanRenta;
        @JsonProperty("tAjusteRenta") private double tAjusteRenta;
        @JsonProperty("tTotalDeudaTributariaRenta") private double tTotalDeudaTributariaRenta;
        @JsonProperty("tRentaPorPagar") private double tRentaPorPagar;
    }
}
