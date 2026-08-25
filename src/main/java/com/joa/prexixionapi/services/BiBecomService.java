package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.*;
import com.joa.prexixionapi.repositories.BiBecomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.utils.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiBecomService {

        private final BiBecomRepository biBecomRepository;

        public BiBecomDashboardDTO getDashboardData(String periodo) {
                log.info("Obteniendo datos del dashboard BI - BECOM para periodo: {}", periodo);

                // 1. Extraer año y mes del periodo (ej: de "2026-08" o "202608" se obtiene
                // anio="2026" y mes="08")
                LocalDate now = LocalDate.now();
                String anio = String.valueOf(now.getYear());
                String mesNum = String.format("%02d", now.getMonthOfYear());

                if (periodo != null && !periodo.trim().isEmpty()) {
                        String cleanPeriodo = periodo.trim();
                        if (cleanPeriodo.length() >= 4) {
                                anio = cleanPeriodo.substring(0, 4);
                        }
                        if (cleanPeriodo.contains("-") && cleanPeriodo.length() >= 7) {
                                mesNum = cleanPeriodo.substring(5, 7);
                        } else if (!cleanPeriodo.contains("-") && cleanPeriodo.length() >= 6) {
                                mesNum = cleanPeriodo.substring(4, 6);
                        }
                }

                String mesSt = DateUtils.getNameStMonth(mesNum);
                String periodoNombre = !mesSt.isEmpty() ? mesSt + " - " + anio
                                : (periodo != null && !periodo.trim().isEmpty() ? periodo : "Jul - 2026");

                // 2. Cabecera
                String responsableNombre = biBecomRepository.findResponsableNombre();
                String fechaEmision = LocalDate.now().toString("dd/MM/yyyy");

                BiBecomHeaderDTO headerDTO = BiBecomHeaderDTO.builder()
                                .responsableNombre(responsableNombre)
                                .fechaEmision(fechaEmision)
                                .periodoNombre(periodoNombre)
                                .anio(anio)
                                .ecosistemaAreas("TAXCOM · ENTERCOM · BOXCOM · STORECOM · FENDERCOM")
                                .build();

                // 3. Devoluciones (Sección 3) - Consulta UNION ALL Unificada
                List<BiBecomDevolucionItemDTO> allDevoluciones = biBecomRepository.findAllDevolucionesByAnio(anio);

                List<BiBecomDevolucionItemDTO> denegadasList = allDevoluciones.stream()
                                .filter(item -> item.getIdEstado() != null
                                                && (item.getIdEstado() == 2 || item.getIdEstado() == 5)
                                                && item.getIdResultado() != null
                                                && (item.getIdResultado() == 4 || item.getIdResultado() == 5))
                                .collect(Collectors.toList());

                List<BiBecomDevolucionItemDTO> pendientesList = allDevoluciones.stream()
                                .filter(item -> item.getIdEstado() != null && item.getIdEstado() == 1
                                                && item.getIdResultado() != null && item.getIdResultado() == 1)
                                .collect(Collectors.toList());

                int casosTotales = allDevoluciones.size();

                BigDecimal montoSolicitadoTotal = allDevoluciones.stream()
                                .map(BiBecomDevolucionItemDTO::getMontoSolicitado)
                                .filter(m -> m != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal montoAprobadoTotal = allDevoluciones.stream()
                                .map(BiBecomDevolucionItemDTO::getMontoAprobado)
                                .filter(m -> m != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal tasaAprobacionPct = BigDecimal.ZERO;
                if (montoSolicitadoTotal.compareTo(BigDecimal.ZERO) > 0) {
                        tasaAprobacionPct = montoAprobadoTotal
                                        .multiply(new BigDecimal("100"))
                                        .divide(montoSolicitadoTotal, 1, RoundingMode.HALF_UP);
                }

                long casosFinalizadosCount = allDevoluciones.stream()
                                .filter(item -> item.getIdEstado() != null
                                                && (item.getIdEstado() == 2 || item.getIdEstado() == 5))
                                .count();

                BigDecimal casosFinalizadosPct = BigDecimal.ZERO;
                if (casosTotales > 0) {
                        casosFinalizadosPct = BigDecimal.valueOf(casosFinalizadosCount)
                                        .multiply(new BigDecimal("100"))
                                        .divide(BigDecimal.valueOf(casosTotales), 1, RoundingMode.HALF_UP);
                }

                BigDecimal montoTotalDenegado = denegadasList.stream()
                                .map(BiBecomDevolucionItemDTO::getMontoSolicitado)
                                .filter(m -> m != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal montoTotalPendiente = pendientesList.stream()
                                .map(BiBecomDevolucionItemDTO::getMontoSolicitado)
                                .filter(m -> m != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                int antiguedadPromedioDias = 0;
                if (!pendientesList.isEmpty()) {
                        long totalDiasAcc = 0;
                        int countFechasValidas = 0;
                        LocalDate today = LocalDate.now();
                        for (BiBecomDevolucionItemDTO item : pendientesList) {
                                if (item.getFechaSolicitud() != null && !item.getFechaSolicitud().trim().isEmpty()) {
                                        try {
                                                LocalDate fSol = LocalDate.parse(item.getFechaSolicitud().trim());
                                                int d = Days.daysBetween(fSol, today).getDays();
                                                if (d >= 0) {
                                                        totalDiasAcc += d;
                                                        countFechasValidas++;
                                                }
                                        } catch (Exception ignored) {
                                        }
                                }
                        }
                        if (countFechasValidas > 0) {
                                antiguedadPromedioDias = (int) (totalDiasAcc / countFechasValidas);
                        }
                }

                BiBecomDevolucionesDTO devolucionesDTO = BiBecomDevolucionesDTO.builder()
                                .casosTotales(casosTotales)
                                .montoSolicitado(montoSolicitadoTotal)
                                .tasaAprobacionPct(tasaAprobacionPct)
                                .montoAprobado(montoAprobadoTotal)
                                .casosFinalizadosPct(casosFinalizadosPct)
                                .casosDenegados(denegadasList.size())
                                .casosPendientes(pendientesList.size())
                                .antiguedadPromedioDias(antiguedadPromedioDias)
                                .montoTotalDenegado(montoTotalDenegado)
                                .montoTotalPendiente(montoTotalPendiente)
                                .denegadasList(denegadasList)
                                .pendientesList(pendientesList)
                                .build();

                // 4. Actualizaciones (Sección 4) - Consulta Real a BD sin Filtros
                List<BiBecomActualizacionItemDTO> allActualizaciones = biBecomRepository.findAllActualizaciones();

                // KPI 1: Total de registros
                int totalRegistrosAct = allActualizaciones.size();

                // KPI 2: Clientes Activos (idEstadoCliente=1) & Actualización Terminada
                // (idEstado=4)
                int totalTerminadosAct = (int) allActualizaciones.stream()
                                .filter(item -> item.getIdEstadoCliente() != null && item.getIdEstadoCliente() == 1
                                                && item.getIdEstado() != null && item.getIdEstado() == 4)
                                .count();

                // KPI 3: Clientes Activos (idEstadoCliente=1) & Actualización En Proceso
                // (idEstado=3)
                int totalEnProcesoAct = (int) allActualizaciones.stream()
                                .filter(item -> item.getIdEstadoCliente() != null && item.getIdEstadoCliente() == 1
                                                && item.getIdEstado() != null && item.getIdEstado() == 3)
                                .count();

                // KPI 4: Clientes Activos (idEstadoCliente=1) & Actualización Pendiente
                // (idEstado=2)
                int totalPendientesAct = (int) allActualizaciones.stream()
                                .filter(item -> item.getIdEstadoCliente() != null && item.getIdEstadoCliente() == 1
                                                && item.getIdEstado() != null && item.getIdEstado() == 2)
                                .count();

                // Tabla 1: Clientes Activos (idEstadoCliente=1) con estados Terminado(4), En
                // Proceso(3) o Pendiente(2)
                List<BiBecomActualizacionItemDTO> activosList = allActualizaciones.stream()
                                .filter(item -> item.getIdEstadoCliente() != null && item.getIdEstadoCliente() == 1
                                                && item.getIdEstado() != null
                                                && (item.getIdEstado() == 4 || item.getIdEstado() == 3
                                                                || item.getIdEstado() == 2))
                                .collect(Collectors.toList());

                // Tabla 2: Clientes Retirados (idEstadoCliente=4) con estado Terminado(4)
                List<BiBecomActualizacionItemDTO> retiradosList = allActualizaciones.stream()
                                .filter(item -> item.getIdEstadoCliente() != null && item.getIdEstadoCliente() == 4
                                                && item.getIdEstado() != null && item.getIdEstado() == 4)
                                .collect(Collectors.toList());

                BiBecomActualizacionesDTO actualizacionesDTO = BiBecomActualizacionesDTO.builder()
                                .totalRegistros(totalRegistrosAct)
                                .totalTerminados(totalTerminadosAct)
                                .totalEnProceso(totalEnProcesoAct)
                                .totalPendientes(totalPendientesAct)
                                .casosActivos(activosList.size())
                                .casosRetirados(retiradosList.size())
                                .activosList(activosList)
                                .retiradosList(retiradosList)
                                .build();

                return BiBecomDashboardDTO.builder()
                                .header(headerDTO)
                                .devoluciones(devolucionesDTO)
                                .actualizaciones(actualizacionesDTO)
                                .build();
        }
}
