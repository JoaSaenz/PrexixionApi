package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.ActivoDTO;
import com.joa.prexixionapi.dto.ActivoDepreciacionDTO;
import com.joa.prexixionapi.dto.DepreciacionResumenDTO;
import com.joa.prexixionapi.repositories.DepreciacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepreciacionService {

    private final DepreciacionRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // --- Métodos de Lectura (Resúmenes) ---

    /**
     * Devuelve el resumen de depreciaciones agrupado por cliente/tipo para un año dado.
     * Usado por el endpoint de listado del módulo de depreciaciones.
     */
    public List<DepreciacionResumenDTO> getResumenByAnio(String anio) {
        List<Object[]> rawList = repository.findResumenByAnioRaw(anio);

        return rawList.stream().map(row -> DepreciacionResumenDTO.builder()
                .ruc((String) row[0])
                .razonSocial((String) row[1])
                .y((String) row[2])
                .idEstado((Integer) row[3])
                .estado((String) row[4])
                .anioInicio((String) row[5])
                .anioFin((String) row[6])
                .activoSaldoInicial(toDouble(row[7]))
                .activoCompras(toDouble(row[8]))
                .activoRetiros(toDouble(row[9]))
                .activoSaldoFinal(toDouble(row[10]))
                .inicial(toDouble(row[11]))
                .total(toDouble(row[12]))
                .retiros(toDouble(row[13]))
                .saldoFinal(toDouble(row[14]))
                .activoFijo(toDouble(row[15]))
                .build()
        ).collect(Collectors.toList());
    }

    private Double toDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return 0.0;
    }

    // --- Métodos de Cálculo (Depreciar) ---

    /**
     * Rellena los años faltantes en una lista de depreciaciones hasta {@code limitYear}.
     * <ul>
     *   <li>Si la lista está vacía (activo con 0% de depreciación) genera registros
     *       sintéticos con costo inicial pero depreciación cero.</li>
     *   <li>Si la lista tiene datos, propaga el saldo final del último año calculado
     *       hacia los años posteriores, respetando la fecha de baja del activo.</li>
     * </ul>
     *
     * @param list      lista mutada con los registros de depreciación
     * @param obj       activo dueño de la lista
     * @param limitYear año máximo del reporte
     * @param idTipo    1 = contable, 2 = tributario
     */

    public void completarAniosFaltantes(List<ActivoDepreciacionDTO> list, ActivoDTO obj, Integer limitYear, int idTipo) {
        if (limitYear == null || list == null) return;

        int targetYear = limitYear;
        Integer anioBaja = extraerAnio(obj.getFechaBaja());
        if (obj.getIdEstado() == 2 && anioBaja != null) {
            targetYear = Math.min(limitYear, anioBaja);
        }

        if (list.isEmpty()) {
            Integer anioCompra = extraerAnio(obj.getFechaCompra());
            if (anioCompra == null) return;

            for (int anio = anioCompra; anio <= targetYear; anio++) {
                boolean esPrimerAnio = (anio == anioCompra);

                ActivoDepreciacionDTO d = ActivoDepreciacionDTO.builder()
                        .idCliente(obj.getIdCliente())
                        .idBien(obj.getId())
                        .anio(String.valueOf(anio))
                        .idTipo(idTipo)
                        .activoSaldoInicial(esPrimerAnio ? 0.0 : obj.getCostoInicial())
                        .activoCompras(esPrimerAnio ? obj.getCostoInicial() : 0.0)
                        .activoRetiros(0.0)
                        .activoSaldoFinal(obj.getCostoInicial())
                        .inicial(0.0).ene(0.0).feb(0.0).mar(0.0).abr(0.0).may(0.0).jun(0.0)
                        .jul(0.0).ago(0.0).sep(0.0).oct(0.0).nov(0.0).dec(0.0)
                        .total(0.0).retiros(0.0).saldoFinal(0.0).activoFijo(0.0)
                        .build();
                list.add(d);
            }
            return;
        }

        int lastCalculatedAnio = Integer.parseInt(list.get(list.size() - 1).getAnio());
        while (lastCalculatedAnio < targetYear) {
            ActivoDepreciacionDTO adAnterior = list.get(list.size() - 1);

            ActivoDepreciacionDTO d = ActivoDepreciacionDTO.builder()
                    .idCliente(adAnterior.getIdCliente())
                    .idBien(adAnterior.getIdBien())
                    .anio(String.valueOf(lastCalculatedAnio + 1))
                    .idTipo(idTipo)
                    .activoSaldoInicial(obj.getCostoInicial())
                    .activoCompras(0.0)
                    .activoRetiros(0.0)
                    .activoSaldoFinal(obj.getCostoInicial())
                    .inicial(adAnterior.getInicial() + adAnterior.getTotal())
                    .ene(0.0).feb(0.0).mar(0.0).abr(0.0).may(0.0).jun(0.0)
                    .jul(0.0).ago(0.0).sep(0.0).oct(0.0).nov(0.0).dec(0.0)
                    .total(0.0)
                    .retiros(0.0)
                    .saldoFinal(adAnterior.getInicial() + adAnterior.getTotal())
                    .activoFijo(adAnterior.getActivoFijo())
                    .build();
            list.add(d);
            lastCalculatedAnio++;
        }
    }

    /**
     * Calcula la lista completa de depreciaciones anuales para un activo dado.
     * <ul>
     *   <li>Activos con 0% de depreciación o sin fecha fin devuelven lista vacía.</li>
     *   <li>Activos dados de baja (idEstado == 2) truncan el cálculo en {@code fechaBaja}.</li>
     *   <li>Activos activos se extienden hasta {@code limitYear} propagando el saldo.</li>
     * </ul>
     *
     * @param obj       activo a depreciar
     * @param tipo      1 = contable, 2 = tributario
     * @param limitYear año hasta el que extrapolar (null = solo hasta fecha fin)
     */
    public List<ActivoDepreciacionDTO> calcularDepreciacion(ActivoDTO obj, int tipo, Integer limitYear) {
        List<ActivoDepreciacionDTO> list = new ArrayList<>();

        double porcentajeDepreciacion = (tipo == 1)
                ? obj.getPorcentajeDepreciacionContable()
                : obj.getPorcentajeDepreciacionTributaria();

        String fechaFinStr = (tipo == 1)
                ? obj.getFechaFinContable()
                : obj.getFechaFinTributaria();
        
        if (porcentajeDepreciacion == 0) {
            return new ArrayList<>();
        }

        if (fechaFinStr == null || fechaFinStr.isEmpty()) {
            return list;
        }

        LocalDate fechaInicio = LocalDate.parse(obj.getFechaInicio().substring(0, 10), formatter);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr.substring(0, 10), formatter);
        
        double costoInicial = obj.getCostoInicial();
        double depreciacionMensual = calcDpMensual(costoInicial, porcentajeDepreciacion);

        // Caso: BAJA
        Integer anioBaja = extraerAnio(obj.getFechaBaja());
        if (obj.getIdEstado() == 2 && anioBaja != null) {
            LocalDate fechaBaja = LocalDate.parse(obj.getFechaBaja().substring(0, 10), formatter);
            int anioCompra = extraerAnio(obj.getFechaCompra());

            for (int anio = anioCompra; anio <= anioBaja; anio++) {
                ActivoDepreciacionDTO d = ActivoDepreciacionDTO.builder()
                        .idCliente(obj.getIdCliente())
                        .idBien(obj.getId())
                        .anio(String.valueOf(anio))
                        .idTipo(tipo)
                        .activoSaldoInicial(anio == anioCompra ? 0.0 : costoInicial)
                        .activoCompras(anio == anioCompra ? costoInicial : 0.0)
                        .activoRetiros(anio == anioBaja ? costoInicial * -1 : 0.0)
                        .activoSaldoFinal(anio == anioBaja ? 0.0 : costoInicial)
                        .ene(0.0).feb(0.0).mar(0.0).abr(0.0).may(0.0).jun(0.0)
                        .jul(0.0).ago(0.0).sep(0.0).oct(0.0).nov(0.0).dec(0.0)
                        .total(0.0).retiros(0.0)
                        .build();
                list.add(d);
            }

            LocalDate fechaCalculoFin = (fechaFin.isBefore(fechaBaja)) ? fechaFin : fechaBaja;
            List<LocalDate> meses = getMesesEntre(fechaInicio, fechaCalculoFin);

            double acumuladoTotal = 0.0;
            for (int i = 0; i < list.size(); i++) {
                ActivoDepreciacionDTO ad = list.get(i);
                ActivoDepreciacionDTO adAnterior = (i > 0) ? list.get(i - 1) : null;
                
                for (LocalDate fecha : meses) {
                    if (fecha.getYear() == Integer.parseInt(ad.getAnio())) {
                        double val;
                        if (fecha.isEqual(fechaInicio)) {
                            val = calcDpParcial(fechaInicio, costoInicial, porcentajeDepreciacion, fecha);
                        } else if (fecha.isEqual(fechaCalculoFin)) {
                            val = round(costoInicial - acumuladoTotal);
                        } else {
                            val = depreciacionMensual;
                            if (round(acumuladoTotal + val) > costoInicial) {
                                val = round(costoInicial - acumuladoTotal);
                            }
                        }
                        setMesValue(ad, fecha.getMonthValue(), val);
                        acumuladoTotal = round(acumuladoTotal + val);
                    }
                }
                calcularTotalesYSaldo(ad, adAnterior, costoInicial);
            }

            // --- AJUSTE FINAL PARA EL AÑO DE BAJA (Retirar Depreciación y Saldo Final) ---
            if (!list.isEmpty()) {
                ActivoDepreciacionDTO adUltimo = list.get(list.size() - 1);
                // Si es el año de baja, retiramos el saldo acumulado
                adUltimo.setRetiros(round(adUltimo.getInicial() + adUltimo.getTotal()));
                adUltimo.setSaldoFinal(0.0);
                adUltimo.setActivoFijo(0.0);
            }

        } else {
            // Caso: ACTIVO
            int anioCompra = extraerAnio(obj.getFechaCompra());
            int anioFin = fechaFin.getYear();

            for (int anio = anioCompra; anio <= anioFin; anio++) {
                ActivoDepreciacionDTO d = ActivoDepreciacionDTO.builder()
                        .idCliente(obj.getIdCliente())
                        .idBien(obj.getId())
                        .anio(String.valueOf(anio))
                        .idTipo(tipo)
                        .activoSaldoInicial(anio == anioCompra ? 0.0 : costoInicial)
                        .activoCompras(anio == anioCompra ? costoInicial : 0.0)
                        .activoRetiros(0.0)
                        .activoSaldoFinal(costoInicial)
                        .ene(0.0).feb(0.0).mar(0.0).abr(0.0).may(0.0).jun(0.0)
                        .jul(0.0).ago(0.0).sep(0.0).oct(0.0).nov(0.0).dec(0.0)
                        .total(0.0).retiros(0.0)
                        .build();
                list.add(d);
            }

            List<LocalDate> meses = getMesesEntre(fechaInicio, fechaFin);

            double acumuladoTotal = 0.0;
            for (int i = 0; i < list.size(); i++) {
                ActivoDepreciacionDTO ad = list.get(i);
                ActivoDepreciacionDTO adAnterior = (i > 0) ? list.get(i - 1) : null;

                for (LocalDate fecha : meses) {
                    if (fecha.getYear() == Integer.parseInt(ad.getAnio())) {
                        double val;
                        if (fecha.isEqual(fechaInicio)) {
                            val = calcDpParcial(fechaInicio, costoInicial, porcentajeDepreciacion, fecha);
                        } else if (fecha.isEqual(fechaFin)) {
                            val = round(costoInicial - acumuladoTotal);
                        } else {
                            val = depreciacionMensual;
                            if (round(acumuladoTotal + val) > costoInicial) {
                                val = round(costoInicial - acumuladoTotal);
                            }
                        }
                        setMesValue(ad, fecha.getMonthValue(), val);
                        acumuladoTotal = round(acumuladoTotal + val);
                    }
                }
                calcularTotalesYSaldo(ad, adAnterior, costoInicial);
            }

            // --- LLENAR AÑOS RESTANTES HASTA EL limitYear PARA BIENES ACTIVOS ---
            if (limitYear != null && !list.isEmpty()) {
                int lastCalculatedAnio = Integer.parseInt(list.get(list.size() - 1).getAnio());
                while (lastCalculatedAnio < limitYear) {
                    ActivoDepreciacionDTO adAnterior = list.get(list.size() - 1);
                    ActivoDepreciacionDTO d = ActivoDepreciacionDTO.builder()
                            .idCliente(obj.getIdCliente())
                            .idBien(obj.getId())
                            .anio(String.valueOf(lastCalculatedAnio + 1))
                            .idTipo(tipo)
                            .activoSaldoInicial(costoInicial)
                            .activoCompras(0.0)
                            .activoRetiros(0.0)
                            .activoSaldoFinal(costoInicial)
                            .inicial(adAnterior.getInicial() + adAnterior.getTotal())
                            .ene(0.0).feb(0.0).mar(0.0).abr(0.0).may(0.0).jun(0.0)
                            .jul(0.0).ago(0.0).sep(0.0).oct(0.0).nov(0.0).dec(0.0)
                            .total(0.0)
                            .retiros(0.0)
                            .saldoFinal(adAnterior.getInicial() + adAnterior.getTotal())
                            .activoFijo(adAnterior.getActivoFijo())
                            .build();
                    list.add(d);
                    lastCalculatedAnio++;
                }
            }
        }

        return list;
    }

    private double calcDpMensual(double costoInicial, double porcentaje) {
        if (porcentaje == 0) return 0.0;
        double tiempoContable = 100.0 / porcentaje;
        return round((costoInicial / tiempoContable) / 12);
    }

    private double calcDpParcial(LocalDate fechaInicio, double costoInicial, double porcentaje, LocalDate fechaActual) {
        double tiempoContable = 100.0 / porcentaje;
        double mensual = (costoInicial / tiempoContable) / 12;
        
        if (fechaInicio.isEqual(fechaActual)) {
            if (fechaInicio.getDayOfMonth() == 1) {
                return round(mensual);
            } else {
                int daysRemaining = fechaInicio.lengthOfMonth() - fechaInicio.getDayOfMonth() + 1;
                return round((mensual / 30.0) * daysRemaining);
            }
        } else {
            if (fechaActual.getDayOfMonth() == fechaActual.lengthOfMonth()) {
                return round(mensual - (mensual / 30.0));
            } else if (fechaActual.getDayOfMonth() == 1) {
                return 0.0;
            } else {
                return round((mensual / 30.0) * (fechaActual.getDayOfMonth() - 1));
            }
        }
    }

    private void setMesValue(ActivoDepreciacionDTO ad, int mes, double val) {
        switch (mes) {
            case 1 -> ad.setEne(val);
            case 2 -> ad.setFeb(val);
            case 3 -> ad.setMar(val);
            case 4 -> ad.setAbr(val);
            case 5 -> ad.setMay(val);
            case 6 -> ad.setJun(val);
            case 7 -> ad.setJul(val);
            case 8 -> ad.setAgo(val);
            case 9 -> ad.setSep(val);
            case 10 -> ad.setOct(val);
            case 11 -> ad.setNov(val);
            case 12 -> ad.setDec(val);
        }
    }

    private void calcularTotalesYSaldo(ActivoDepreciacionDTO ad, ActivoDepreciacionDTO adAnterior, double costoInicial) {
        double inicial = (adAnterior != null) ? toDouble(adAnterior.getSaldoFinal()) : 0.0;
        ad.setInicial(inicial);
        
        double totalAnio = toDouble(ad.getEne()) + toDouble(ad.getFeb()) + toDouble(ad.getMar()) + toDouble(ad.getAbr()) + 
                           toDouble(ad.getMay()) + toDouble(ad.getJun()) + toDouble(ad.getJul()) + toDouble(ad.getAgo()) + 
                           toDouble(ad.getSep()) + toDouble(ad.getOct()) + toDouble(ad.getNov()) + toDouble(ad.getDec());
        
        ad.setTotal(round(totalAnio));
        ad.setSaldoFinal(round(ad.getInicial() + ad.getTotal()));
        ad.setActivoFijo(round(costoInicial - ad.getSaldoFinal()));
    }

    private List<LocalDate> getMesesEntre(LocalDate start, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();
        LocalDate current = start.withDayOfMonth(1);
        LocalDate endFirst = end.withDayOfMonth(1);
        
        list.add(start);
        
        current = current.plusMonths(1);
        while (current.isBefore(endFirst)) {
            list.add(current);
            current = current.plusMonths(1);
        }
        
        if (!endFirst.isEqual(start.withDayOfMonth(1))) {
            list.add(end);
        }
        
        return list;
    }


    private Integer extraerAnio(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            // Soporta formatos como "2024-04-03 00:00:00.0" o "2024-04-03"
            return Integer.parseInt(dateStr.substring(0, 4));
        } catch (Exception e) {
            return null;
        }
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
