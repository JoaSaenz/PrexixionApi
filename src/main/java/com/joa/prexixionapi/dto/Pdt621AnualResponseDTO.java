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
public class Pdt621AnualResponseDTO {
    private List<Pdt621ReportAnualDTO> list;
    private TotalesDTO totales;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalesDTO {
        private double tVentasG;
        private double tComprasG;
        private double tVentasNetas10;
        private double tCompraNetas10;
        
        private double tMesIgv;
        private double tAfavorIgv;
        private double tAjusteIgv;
        private double tResultadoIgv;
        
        private double tMesPer;
        private double tAfavorPer;
        private double tCompensaPer;
        private double tAjustePer;
        private double tResultadoPer;
        
        private double tMesRet;
        private double tAfavorRet;
        private double tCompensaRet;
        private double tAjusteRet;
        private double tResultadoRet;
        private double tIgvPorPagar;
        
        private double tExpFactPer;
        private double tVentasNg;
        private double tComprasNgE;
        private double tComprasNg;
        
        private double tIvapVentasGravadas;
        private double tIvapTributo;
        
        private double tBaseRenta;
        private double tMesRenta;
        private double tMesAnteriorRenta;
        private double tAnualRenta;
        private double tSaldoFavorExportador;
        private double tCItanRenta;
        private double tAjusteRenta;
        private double tTotalDeudaTributariaRenta;
        private double tRentaPorPagar;
    }
}
