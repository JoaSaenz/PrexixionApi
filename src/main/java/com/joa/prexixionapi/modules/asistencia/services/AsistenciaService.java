package com.joa.prexixionapi.modules.asistencia.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaStatsDTO;
import com.joa.prexixionapi.modules.asistencia.repositories.AsistenciaRepository;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;

    /**
     * Obtiene la asistencia diaria con filtros opcionales de estado y tipo.
     */
    public List<AsistenciaDTO> list(String fecha, List<Integer> statuses, List<Integer> types) {
        // 1. Obtener marcaciones del día
        List<AsistenciaDTO> marcaciones = asistenciaRepository.getMarcacionesDiarias(fecha);

        // 2. Obtener personal activo enriquecido con filtros
        List<AsistenciaDTO> personalActivo = asistenciaRepository.getPersonalActivo(statuses, types);

        // 3. Cruzar datos (Optimizado con Map)
        Map<String, AsistenciaDTO> marcacionesMap = marcaciones.stream()
                .filter(m -> m.getDni() != null)
                .collect(Collectors.toMap(m -> m.getDni().trim(), m -> m, (m1, m2) -> m1));

        log.info("Matching markings for {} personnel. Total markings found: {}", personalActivo.size(),
                marcaciones.size());

        for (AsistenciaDTO p : personalActivo) {
            String dniKey = p.getDni() != null ? p.getDni().trim() : "";
            AsistenciaDTO m = marcacionesMap.get(dniKey);
            if (m != null) {
                p.setMi(m.getMi());
                p.setMs(m.getMs());
                p.setTi(m.getTi());
                p.setTs(m.getTs());
                p.setTarde(m.getTarde());
                p.setFecha(m.getFecha());
            } else {
                p.setFecha(fecha);
            }
            // Calcular tardanza básica
            p.setMinutosTardanza(calcularTardanza(p, fecha));
        }

        return personalActivo;
    }

    private int calcularTardanza(AsistenciaDTO a, String fecha) {
        int total = 0;
        try {
            if (a.getMi() != null && !a.getMi().isEmpty()) {
                // Lógica de 09:00:00
                int h = Integer.parseInt(a.getMi().substring(0, 2));
                int m = Integer.parseInt(a.getMi().substring(3, 5));
                if (h == 9 && m > 0)
                    total += m;
                else if (h > 9)
                    total += (h - 9) * 60 + m;
            }
            // Tarde (15:00 o 14:00 según fecha)
            if (a.getTi() != null && !a.getTi().isEmpty()) {
                int limitH = (fecha.compareTo("2024-05-01") > 0) ? 14 : 15;
                int h = Integer.parseInt(a.getTi().substring(0, 2));
                int m = Integer.parseInt(a.getTi().substring(3, 5));
                if (h == limitH && m > 0)
                    total += m;
                else if (h > limitH)
                    total += (h - limitH) * 60 + m;
            }
        } catch (Exception e) {
            log.warn("Error calculando tardanza para {}: {}", a.getDni(), e.getMessage());
        }
        return total;
    }

    private int calcularMinutosTrabajados(String mi, String ms, String ti, String ts) {
        try {
            if (mi == null || mi.isEmpty()) {
                if (ti == null || ti.isEmpty())
                    return 0;
                mi = ti; // Si no hay entrada de mañana, usamos la de tarde como inicio
            }
            if (ts == null || ts.isEmpty()) {
                if (ms == null || ms.isEmpty())
                    return 0;
                ts = ms; // Si no hay salida de tarde, usamos la de mañana como fin
            }

            java.time.LocalTime start = java.time.LocalTime.parse(mi);
            java.time.LocalTime end = java.time.LocalTime.parse(ts);

            if (end.isBefore(start))
                return 0;

            // Caso 1: Tiene marcaciones de almuerzo (4 marcas)
            if (ms != null && !ms.isEmpty() && ti != null && !ti.isEmpty()) {
                java.time.LocalTime msTime = java.time.LocalTime.parse(ms);
                java.time.LocalTime tiTime = java.time.LocalTime.parse(ti);

                if (msTime.isAfter(start) && tiTime.isAfter(msTime) && end.isAfter(tiTime)) {
                    long mañana = java.time.Duration.between(start, msTime).toMinutes();
                    long tarde = java.time.Duration.between(tiTime, end).toMinutes();
                    return (int) (mañana + tarde);
                }
            }

            // Caso 2: Solo entrada y salida (o 3 marcas)
            long total = java.time.Duration.between(start, end).toMinutes();

            // Regla heurística: Si el turno es >= 6 horas (360 min), descontar 1 hora de
            // almuerzo
            if (total >= 360) {
                total -= 60;
            }

            return (int) total;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<AsistenciaDTO> mensual(String dni, String fechaI, String fechaF) {
        log.info("Requesting monthly asistencia for DNI: {} from {} to {}", dni, fechaI, fechaF);

        List<AsistenciaDTO> rawList = asistenciaRepository.getMonthlyAsistenciaBase(dni, fechaI, fechaF);

        for (AsistenciaDTO dto : rawList) {
            // Cálculos
            dto.setMinutosTardanza(calcularTardanza(dto, dto.getFecha()));
            dto.setMinutosTrabajados(calcularMinutosTrabajados(dto.getMi(), dto.getMs(), dto.getTi(), dto.getTs()));
        }
        return rawList;
    }

    public AsistenciaStatsDTO stats(String fechaI, String fechaF) {
        log.info("Calculating stats from {} to {}", fechaI, fechaF);

        // 1. Ranking de Personas (Top Tardones)
        List<AsistenciaStatsDTO.PersonLatenessDTO> topTardones = asistenciaRepository.getTopTardones(fechaI, fechaF);

        // 2. Ranking de Áreas
        Map<String, List<AsistenciaStatsDTO.PersonLatenessDTO>> groupedByArea = topTardones.stream()
                .filter(p -> p.getArea() != null)
                .collect(Collectors.groupingBy(AsistenciaStatsDTO.PersonLatenessDTO::getArea));

        List<AsistenciaStatsDTO.AreaLatenessDTO> areasTardonas = groupedByArea.entrySet().stream()
                .map(entry -> AsistenciaStatsDTO.AreaLatenessDTO.builder()
                        .nombreArea(entry.getKey())
                        .totalMinutos(entry.getValue().stream()
                                .mapToInt(AsistenciaStatsDTO.PersonLatenessDTO::getMinutosTardanza).sum())
                        .cantidadPersonas(entry.getValue().size())
                        .build())
                .sorted((a1, a2) -> Integer.compare(a2.getTotalMinutos(), a1.getTotalMinutos()))
                .collect(Collectors.toList());

        return AsistenciaStatsDTO.builder()
                .topTardones(topTardones.stream().limit(10).collect(Collectors.toList()))
                .areasTardonas(areasTardonas)
                .totalTardanzas(topTardones.size())
                .build();
    }


    /**
     * Obtiene el historial completo de marcaciones de un trabajador en una fecha
     * específica.
     */
    public List<Map<String, Object>> getRawLogs(String dni, String fecha) {
        return asistenciaRepository.getRawLogs(dni, fecha);
    }

}
