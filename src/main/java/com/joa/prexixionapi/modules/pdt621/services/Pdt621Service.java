package com.joa.prexixionapi.modules.pdt621.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621AnualResponseDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621DTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621HistorialDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621RegistroDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ReportAnualDTO;
import com.joa.prexixionapi.modules.pdt621.repositories.Pdt621Repository;

import lombok.RequiredArgsConstructor;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621FilterRequestDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ListDTO;

@Service
@RequiredArgsConstructor
public class Pdt621Service {

    private final Pdt621Repository pdt621Repository;

    public String[] getClienteBasico(String ruc) {
        return pdt621Repository.getClienteBasico(ruc);
    }

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
                    .creditoItanRenta(yearData.stream().mapToDouble(Pdt621ReportAnualDTO::getCreditoItanRenta).sum())
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
                .tCItanRenta(nonSubtotalData.stream().mapToDouble(Pdt621ReportAnualDTO::getCreditoItanRenta).sum())
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

    public List<Pdt621ListDTO> getPdt621List(Pdt621FilterRequestDTO req) {
        List<Pdt621ListDTO> list = new ArrayList<>();
        
        String anioI = req.getPeriodoI().substring(0, 4);
        String mesI = req.getPeriodoI().substring(5, 7);
        
        String anioF = req.getPeriodoF().substring(0, 4);
        String mesF = req.getPeriodoF().substring(5, 7);

        int start = Integer.parseInt(anioI + mesI);
        int end = Integer.parseInt(anioF + mesF);

        while (start <= end) {
            list.addAll(pdt621Repository.listByFilters(anioI, mesI, req));

            if (mesI.equals("12")) {
                anioI = String.valueOf(Integer.parseInt(anioI) + 1);
                mesI = "01";
            } else {
                int m = Integer.parseInt(mesI) + 1;
                mesI = m < 10 ? "0" + m : String.valueOf(m);
            }
            start = Integer.parseInt(anioI + mesI);
        }

        // Apply additional memory filters if needed
        // (sire, sireCv, confirmacion, pdt, mail, sus, pagarImpuestos)
        // that were usually handled by client side or by extra parameters.
        return list;
    }

    public DataTablesResponseDTO<Pdt621ListDTO> getPdt621DataTables(Pdt621FilterRequestDTO req) {
        List<Pdt621ListDTO> all = getPdt621List(req);

        int confirmacionSi = 0;
        int confirmacionNo = 0;
        int confirmacionNa = 0;

        int sireCvSi = 0;
        int sireCvNo = 0;
        int sireCvNa = 0;

        int pdtSi = 0;
        int pdtNo = 0;

        for (Pdt621ListDTO c : all) {
            // TAKE (confirmacion)
            if (c.getConfirmacion() != null) {
                if (c.getConfirmacion() == 1) {
                    confirmacionSi++;
                } else if (c.getConfirmacion() == 0) {
                    confirmacionNo++;
                } else if (c.getConfirmacion() == 2) {
                    confirmacionNa++;
                }
            } else {
                confirmacionNo++;
            }

            // SIRE (sireCv)
            if (c.getSireCV() != null) {
                if (c.getSireCV() == 1) {
                    sireCvSi++;
                } else if (c.getSireCV() == 0) {
                    sireCvNo++;
                } else if (c.getSireCV() == 2) {
                    sireCvNa++;
                }
            } else {
                sireCvNo++;
            }

            // PDT
            boolean hasPdt = false;
            if (c.getRegistros() != null && !c.getRegistros().isEmpty()) {
                for (Pdt621RegistroDTO r : c.getRegistros()) {
                    if (r.getNroOrden() != null && !r.getNroOrden().trim().isEmpty()) {
                        hasPdt = true;
                        break;
                    }
                }
            }
            if (hasPdt) {
                pdtSi++;
            } else {
                pdtNo++;
            }
        }

        List<IndicadorDTO> indicadores = new ArrayList<>();
        indicadores.add(new IndicadorDTO("TAKE_CONFIRMED", confirmacionSi + confirmacionNa));
        indicadores.add(new IndicadorDTO("TAKE_PENDING", confirmacionNo));
        indicadores.add(new IndicadorDTO("SIRE_PRESENTED", sireCvSi + sireCvNa));
        indicadores.add(new IndicadorDTO("SIRE_PENDING", sireCvNo));
        indicadores.add(new IndicadorDTO("PDT_PRESENTED", pdtSi));
        indicadores.add(new IndicadorDTO("PDT_PENDING", pdtNo));
        indicadores.add(new IndicadorDTO("TOTAL", all.size()));

        return DataTablesResponseDTO.<Pdt621ListDTO>builder()
                .data(all)
                .indicadores(indicadores)
                .build();
    }

    public String[] getVencimientoLimits(String periodoI, String periodoF) {
        int startAnio = Integer.parseInt(periodoI.substring(0, 4));
        int startMes = Integer.parseInt(periodoI.substring(5, 7));
        int endAnio = Integer.parseInt(periodoF.substring(0, 4));
        int endMes = Integer.parseInt(periodoF.substring(5, 7));

        int currAnio = startAnio;
        int currMes = startMes;

        String globalMin = null;
        String globalMax = null;

        while (currAnio * 100 + currMes <= endAnio * 100 + endMes) {
            String mesStr = currMes < 10 ? "0" + currMes : String.valueOf(currMes);
            String anioStr = String.valueOf(currAnio);

            String[] limits = pdt621Repository.getVencimientoLimits(anioStr, mesStr);

            if (limits[0] != null && !limits[0].isEmpty()) {
                if (globalMin == null || limits[0].compareTo(globalMin) < 0) {
                    globalMin = limits[0];
                }
            }
            if (limits[1] != null && !limits[1].isEmpty()) {
                if (globalMax == null || limits[1].compareTo(globalMax) > 0) {
                    globalMax = limits[1];
                }
            }

            if (currMes == 12) {
                currAnio++;
                currMes = 1;
            } else {
                currMes++;
            }
        }

        return new String[] { globalMin != null ? globalMin : "", globalMax != null ? globalMax : "" };
    }

    public Pdt621DTO getOne(String ruc, String anio, String mes) {
        Pdt621DTO dto = pdt621Repository.getOne(ruc, anio, mes);
        if (dto == null) {
            dto = Pdt621DTO.builder()
                    .ruc(ruc)
                    .anio(anio)
                    .mes(mes)
                    .registros(new ArrayList<>())
                    .build();
        }
        return dto;
    }

    @Transactional
    public int insertUpdate(Pdt621DTO dto) {
        return pdt621Repository.insertUpdate(dto);
    }

    public List<Pdt621HistorialDTO> listHistorial(String idCliente, String periodoInicial, String periodoFinal) {
        return pdt621Repository.listHistorial(idCliente, periodoInicial, periodoFinal);
    }

    public boolean over300UIT(String ruc, String anio, String mes, double ventasG, double ventasNetas10,
            double ventasNg, double expFactPer, double ivapVentasGravadas) {
        return pdt621Repository.over300UIT(ruc, anio, mes, ventasG, ventasNetas10, ventasNg, expFactPer, ivapVentasGravadas);
    }
}
