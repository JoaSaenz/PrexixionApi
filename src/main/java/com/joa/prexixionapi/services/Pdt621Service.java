package com.joa.prexixionapi.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.Pdt621AnualResponseDTO;
import com.joa.prexixionapi.dto.Pdt621ReportAnualDTO;
import com.joa.prexixionapi.repositories.Pdt621Repository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Pdt621Service {

    private final Pdt621Repository pdt621Repository;

    public Pdt621AnualResponseDTO getPdt621ReportAnual(String ruc, List<String> anios) {
        List<Pdt621ReportAnualDTO> listTaxReview = new ArrayList<>();
        List<Pdt621ReportAnualDTO> listPdt621 = new ArrayList<>();

        for (String anio : anios) {
            listTaxReview.addAll(pdt621Repository.listTaxReviewPDT621(ruc, anio));
            listPdt621.addAll(pdt621Repository.listAnualPorAnio(ruc, anio));
        }

        List<Pdt621ReportAnualDTO> list = combinarListas(listTaxReview, listPdt621);

        // COMPLETAR PERIODOS QUE FALTAN
        for (String anio : anios) {
            for (int i = 1; i < 13; i++) {
                String mes = i < 10 ? "0" + i : String.valueOf(i);
                boolean exists = list.stream()
                        .anyMatch(e -> e.getAnio().equals(anio) && e.getMes().equals(mes));

                if (!exists) {
                    list.add(Pdt621ReportAnualDTO.builder()
                            .ruc(ruc)
                            .anio(anio)
                            .mes(mes)
                            .registros(new ArrayList<>())
                            .build());
                }
            }
        }

        // CALCULAR SUBTOTALES (MES 13)
        for (String anio : anios) {
            List<Pdt621ReportAnualDTO> yearData = list.stream()
                    .filter(e -> e.getAnio().equals(anio))
                    .collect(Collectors.toList());

            Pdt621ReportAnualDTO subtotal = Pdt621ReportAnualDTO.builder()
                    .ruc(ruc)
                    .anio(anio)
                    .mes("13")
                    .ventasG(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getVentasG).sum())
                    .comprasG(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasG).sum())
                    .ventasNetas10(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getVentasNetas10).sum())
                    .comprasNetas10(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasNetas10).sum())
                    .validerVentas(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getValiderVentas).sum())
                    .validerCompras(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getValiderCompras).sum())
                    .mesIgv(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesIgv).sum())
                    .mesAnteriorIgv(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorIgv).sum())
                    .ajusteIgv(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjusteIgv).sum())
                    .resultadoIgv(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getResultadoIgv).sum())
                    .mesPer(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesPer).sum())
                    .mesAnteriorPer(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorPer).sum())
                    .compensacionPer(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getCompensacionPer).sum())
                    .ajustePer(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjustePer).sum())
                    .resultadoPer(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getResultadoPer).sum())
                    .mesRet(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesRet).sum())
                    .mesAnteriorRet(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorRet).sum())
                    .compensacionRet(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getCompensacionRet).sum())
                    .ajusteRet(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjusteRet).sum())
                    .resultadoRet(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getResultadoRet).sum())
                    .igvPorPagar(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getIgvPorPagar).sum())
                    .expFactPer(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getExpFactPer).sum())
                    .ventasNg(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getVentasNg).sum())
                    .comprasNgE(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasNgE).sum())
                    .comprasNg(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasNg).sum())
                    .ivapVentasGravadas(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getIvapVentasGravadas).sum())
                    .ivapTributo(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getIvapTributo).sum())
                    .baseRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getBaseRenta).sum())
                    .mesRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesRenta).sum())
                    .mesAnteriorRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorRenta).sum())
                    .anualRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getAnualRenta).sum())
                    .saldoFavorExportador(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getSaldoFavorExportador).sum())
                    .cItanRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getCItanRenta).sum())
                    .ajusteRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjusteRenta).sum())
                    .totalDeudaTributariaRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getTotalDeudaTributariaRenta).sum())
                    .rentaPorPagar(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getRentaPorPagar).sum())
                    .registros(new ArrayList<>())
                    .build();
            list.add(subtotal);
        }

        // ORDENAR LISTA
        list.sort(Comparator.comparing(Pdt621ReportAnualDTO::getMes));
        list.sort(Comparator.comparing(Pdt621ReportAnualDTO::getAnio));

        // CALCULAR TOTALES GRANDES
        List<Pdt621ReportAnualDTO> nonSubtotalData = list.stream()
                .filter(e -> !e.getMes().equals("13"))
                .collect(Collectors.toList());

        Pdt621AnualResponseDTO.TotalesDTO totales = Pdt621AnualResponseDTO.TotalesDTO.builder()
                .tVentasG(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getVentasG).sum())
                .tComprasG(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasG).sum())
                .tVentasNetas10(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getVentasNetas10).sum())
                .tCompraNetas10(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasNetas10).sum())
                .tMesIgv(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesIgv).sum())
                .tAfavorIgv(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorIgv).sum())
                .tAjusteIgv(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjusteIgv).sum())
                .tResultadoIgv(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getResultadoIgv).sum())
                .tMesPer(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesPer).sum())
                .tAfavorPer(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorPer).sum())
                .tCompensaPer(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getCompensacionPer).sum())
                .tAjustePer(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjustePer).sum())
                .tResultadoPer(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getResultadoPer).sum())
                .tMesRet(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesRet).sum())
                .tAfavorRet(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorRet).sum())
                .tCompensaRet(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getCompensacionRet).sum())
                .tAjusteRet(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjusteRet).sum())
                .tResultadoRet(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getResultadoRet).sum())
                .tIgvPorPagar(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getIgvPorPagar).sum())
                .tExpFactPer(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getExpFactPer).sum())
                .tVentasNg(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getVentasNg).sum())
                .tComprasNgE(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasNgE).sum())
                .tComprasNg(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getComprasNg).sum())
                .tIvapVentasGravadas(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getIvapVentasGravadas).sum())
                .tIvapTributo(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getIvapTributo).sum())
                .tBaseRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getBaseRenta).sum())
                .tMesRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesRenta).sum())
                .tMesAnteriorRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getMesAnteriorRenta).sum())
                .tAnualRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getAnualRenta).sum())
                .tSaldoFavorExportador(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getSaldoFavorExportador).sum())
                .tCItanRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getCItanRenta).sum())
                .tAjusteRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getAjusteRenta).sum())
                .tTotalDeudaTributariaRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getTotalDeudaTributariaRenta).sum())
                .tRentaPorPagar(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getRentaPorPagar).sum())
                .build();

        return Pdt621AnualResponseDTO.builder()
                .list(list)
                .totales(totales)
                .build();
    }

    private List<Pdt621ReportAnualDTO> combinarListas(List<Pdt621ReportAnualDTO> lista1, List<Pdt621ReportAnualDTO> lista2) {
        Map<String, Pdt621ReportAnualDTO> mapa = new LinkedHashMap<>();

        for (Pdt621ReportAnualDTO c : lista1) {
            mapa.put(c.getAnio() + "-" + c.getMes(), c);
        }

        for (Pdt621ReportAnualDTO c : lista2) {
            mapa.put(c.getAnio() + "-" + c.getMes(), c);
        }

        return new ArrayList<>(mapa.values());
    }
}
