package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.AttendanceDTO;
import com.joa.prexixionapi.dto.AttendanceStatsDTO;
import com.joa.prexixionapi.dto.IdNameDTO;
import com.joa.prexixionapi.dto.PersonDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Obtiene la asistencia diaria con filtros opcionales de estado y tipo.
     */
    public List<AttendanceDTO> getDailyAttendance(String fecha, List<Integer> statuses, List<Integer> types) {
        String queryAsistencia = """
            SELECT * FROM (
                SELECT m.pin as dni, CONVERT(date,time) as fecha, CONVERT(time(0),time) as tiempo,
                CASE
                    WHEN '%s' > '2024-05-01' THEN
                        CASE
                            WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'mi'
                            WHEN CONVERT(time, time) < '13:30:00' AND CONVERT(time, time) > '12:00:00' THEN 'ms'
                            WHEN CONVERT(time, time) < '15:00:00' AND CONVERT(time, time) > '13:31:00' THEN 'ti'
                            WHEN CONVERT(time, time) >= '17:00:00' THEN 'ts'
                            ELSE 'tarde'
                        END
                    ELSE
                        CASE
                            WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'mi'
                            WHEN CONVERT(time, time) < '13:30:00' AND CONVERT(time, time) > '12:00:00' THEN 'ms'
                            WHEN CONVERT(time, time) < '16:52:00' AND CONVERT(time, time) > '14:00:00' THEN 'ti'
                            WHEN CONVERT(time, time) > '16:59:00' THEN 'ts'
                            ELSE 'tarde'
                        END
                END AS turno
                FROM ZKAccess1.dbo.acc_monitor_log m
                WHERE time >= ? AND time < DATEADD(day, 1, ?)
            ) AS SourceTable PIVOT(min(tiempo) FOR [turno] IN([mi],[ms],[ti],[ts],[tarde])) AS PivotTable;
            """.formatted(fecha);

        // 1. Obtener marcaciones del día
        List<AttendanceDTO> marcaciones = jdbcTemplate.query(queryAsistencia, (rs, rowNum) -> AttendanceDTO.builder()
                .dni(rs.getString("dni"))
                .fecha(rs.getString("fecha"))
                .mi(rs.getString("mi") != null ? rs.getString("mi") : "")
                .ms(rs.getString("ms") != null ? rs.getString("ms") : "")
                .ti(rs.getString("ti") != null ? rs.getString("ti") : "")
                .ts(rs.getString("ts") != null ? rs.getString("ts") : "")
                .tarde(rs.getString("tarde") != null ? rs.getString("tarde") : "")
                .build(), fecha, fecha);

        // 2. Obtener personal activo enriquecido con filtros
        StringBuilder queryPersonal = new StringBuilder("""
            SELECT p.dni, p.nombres, p.apellidos, e.descripcion as empresa, pu.descripcion as puesto, a.descripcion as area, tp.descripcion as tipo,
                   p.idEstado, est.descripcion as estado, p.idTipo
            FROM bty.dbo.personal p
            LEFT JOIN bty.dbo.empresas e ON p.idEmpresa = e.id
            LEFT JOIN bty.dbo.personalPuestos pu ON p.idPuesto = pu.id
            LEFT JOIN bty.dbo.areas a ON p.idArea = a.id
            LEFT JOIN bty.dbo.personalTipos tp ON p.idTipo = tp.id
            LEFT JOIN bty.dbo.personalEstados est ON p.idEstado = est.id
            WHERE 1=1
            """);

        if (statuses != null && !statuses.isEmpty()) {
            queryPersonal.append(" AND p.idEstado IN (").append(statuses.stream().map(String::valueOf).collect(Collectors.joining(","))).append(")");
        } else {
            queryPersonal.append(" AND p.idEstado IN (1, 2)"); // Por defecto activos/vigentes
        }

        if (types != null && !types.isEmpty()) {
            queryPersonal.append(" AND p.idTipo IN (").append(types.stream().map(String::valueOf).collect(Collectors.joining(","))).append(")");
        }

        List<AttendanceDTO> personalActivo = jdbcTemplate.query(queryPersonal.toString(), (rs, rowNum) -> AttendanceDTO.builder()
                .dni(rs.getString("dni"))
                .nombre(rs.getString("nombres"))
                .apellido(rs.getString("apellidos"))
                .empresa(rs.getString("empresa"))
                .puesto(rs.getString("puesto"))
                .area(rs.getString("area"))
                .tipo(rs.getString("tipo"))
                .idEstado(rs.getInt("idEstado"))
                .estado(rs.getString("estado"))
                .idTipo(rs.getInt("idTipo"))
                .mi("").ms("").ti("").ts("").tarde("")
                .build());

        // 3. Cruzar datos (Optimizado con Map)
        Map<String, AttendanceDTO> marcacionesMap = marcaciones.stream()
                .filter(m -> m.getDni() != null)
                .collect(Collectors.toMap(m -> m.getDni().trim(), m -> m, (m1, m2) -> m1));

        log.info("Matching markings for {} personnel. Total markings found: {}", personalActivo.size(), marcaciones.size());

        for (AttendanceDTO p : personalActivo) {
            String dniKey = p.getDni() != null ? p.getDni().trim() : "";
            AttendanceDTO m = marcacionesMap.get(dniKey);
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

    private int calcularTardanza(AttendanceDTO a, String fecha) {
        int total = 0;
        try {
            if (a.getMi() != null && !a.getMi().isEmpty()) {
                // Lógica de 09:00:00
                int h = Integer.parseInt(a.getMi().substring(0, 2));
                int m = Integer.parseInt(a.getMi().substring(3, 5));
                if (h == 9 && m > 0) total += m;
                else if (h > 9) total += (h - 9) * 60 + m;
            }
            // Tarde (15:00 o 14:00 según fecha)
            if (a.getTi() != null && !a.getTi().isEmpty()) {
                int limitH = (fecha.compareTo("2024-05-01") > 0) ? 14 : 15;
                int h = Integer.parseInt(a.getTi().substring(0, 2));
                int m = Integer.parseInt(a.getTi().substring(3, 5));
                if (h == limitH && m > 0) total += m;
                else if (h > limitH) total += (h - limitH) * 60 + m;
            }
        } catch (Exception e) {
            log.warn("Error calculando tardanza para {}: {}", a.getDni(), e.getMessage());
        }
        return total;
    }

    private int calcularMinutosTrabajados(String mi, String ms, String ti, String ts) {
        try {
            if (mi == null || mi.isEmpty()) {
                if (ti == null || ti.isEmpty()) return 0;
                mi = ti; // Si no hay entrada de mañana, usamos la de tarde como inicio
            }
            if (ts == null || ts.isEmpty()) {
                if (ms == null || ms.isEmpty()) return 0;
                ts = ms; // Si no hay salida de tarde, usamos la de mañana como fin
            }

            java.time.LocalTime start = java.time.LocalTime.parse(mi);
            java.time.LocalTime end = java.time.LocalTime.parse(ts);

            if (end.isBefore(start)) return 0;

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
            
            // Regla heurística: Si el turno es >= 6 horas (360 min), descontar 1 hora de almuerzo
            if (total >= 360) {
                total -= 60;
            }
            
            return (int) total;
        } catch (Exception e) {
            return 0;
        }
    }
    public List<AttendanceDTO> getMonthlyAttendance(String dni, String fechaI, String fechaF) {
        log.info("Requesting monthly attendance for DNI: {} from {} to {}", dni, fechaI, fechaF);
        
        String query = """
            SELECT * FROM (
                SELECT m.pin as dni, CONVERT(date,time) as fecha, CONVERT(time(0),time) as tiempo,
                CASE
                    WHEN time > '2024-05-01' THEN
                        CASE
                            WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'mi'
                            WHEN CONVERT(time, time) < '13:30:00' AND CONVERT(time, time) > '12:00:00' THEN 'ms'
                            WHEN CONVERT(time, time) < '15:00:00' AND CONVERT(time, time) > '13:31:00' THEN 'ti'
                            WHEN CONVERT(time, time) >= '17:00:00' THEN 'ts'
                            ELSE 'tarde'
                        END
                    ELSE
                        CASE
                            WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'mi'
                            WHEN CONVERT(time, time) < '13:30:00' AND CONVERT(time, time) > '12:00:00' THEN 'ms'
                            WHEN CONVERT(time, time) < '16:52:00' AND CONVERT(time, time) > '14:00:00' THEN 'ti'
                            WHEN CONVERT(time, time) > '16:59:00' THEN 'ts'
                            ELSE 'tarde'
                        END
                END AS turno
                FROM ZKAccess1.dbo.acc_monitor_log m
                WHERE time >= ? AND time < DATEADD(day, 1, ?) AND pin = ?
            ) AS SourceTable PIVOT(min(tiempo) FOR [turno] IN([mi],[ms],[ti],[ts],[tarde])) AS PivotTable
            ORDER BY fecha ASC;
            """;

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            AttendanceDTO dto = AttendanceDTO.builder()
                    .dni(rs.getString("dni"))
                    .fecha(rs.getString("fecha"))
                    .mi(rs.getString("mi") != null ? rs.getString("mi") : "")
                    .ms(rs.getString("ms") != null ? rs.getString("ms") : "")
                    .ti(rs.getString("ti") != null ? rs.getString("ti") : "")
                    .ts(rs.getString("ts") != null ? rs.getString("ts") : "")
                    .tarde(rs.getString("tarde") != null ? rs.getString("tarde") : "")
                    .build();
            
            // Cálculos
            dto.setMinutosTardanza(calcularTardanza(dto, dto.getFecha()));
            dto.setMinutosTrabajados(calcularMinutosTrabajados(dto.getMi(), dto.getMs(), dto.getTi(), dto.getTs()));
            
            return dto;
        }, fechaI, fechaF, dni);
    }


    public AttendanceStatsDTO getStats(String fechaI, String fechaF) {
        log.info("Calculating stats from {} to {}", fechaI, fechaF);

        // 1. Ranking de Personas (Top Tardones)
        String queryRankingPersonas = """
            SELECT
                p.dni, p.nombres + ' ' + p.apellidos as nombreCompleto, a.descripcion as area,
                SUM(CASE
                    WHEN PivotTable.mi > '09:00:59' AND PivotTable.mi <= '10:00:00'
                    THEN DATEDIFF(MINUTE, '09:00:00', PivotTable.mi)
                    ELSE 0
                END) +
                SUM(CASE
                    WHEN PivotTable.fecha > '2024-05-01' THEN
                        CASE WHEN PivotTable.ti > '14:00:59' AND PivotTable.ti <= '15:00:00' THEN DATEDIFF(MINUTE, '14:00:00', PivotTable.ti) ELSE 0 END
                    ELSE
                        CASE WHEN PivotTable.ti > '15:00:59' AND PivotTable.ti <= '16:00:00' THEN DATEDIFF(MINUTE, '15:00:00', PivotTable.ti) ELSE 0 END
                END) AS minutosTotal
            FROM (
                SELECT pin, CONVERT(date, time) AS fecha, CONVERT(time(0), time) AS tiempo,
                CASE WHEN time > '2024-05-01' THEN
                    CASE
                        WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'mi'
                        WHEN CONVERT(time, time) < '15:00:00' AND CONVERT(time, time) > '13:31:00' THEN 'ti'
                        ELSE 'otro'
                    END
                ELSE
                    CASE
                        WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'mi'
                        WHEN CONVERT(time, time) < '16:57:00' AND CONVERT(time, time) > '14:00:00' THEN 'ti'
                        ELSE 'otro'
                    END
                END AS turno
                FROM ZKAccess1.dbo.acc_monitor_log
                WHERE time >= ? AND time < ?
            ) AS SourceTable PIVOT ( MIN(tiempo) FOR turno IN ([mi], [ti]) ) AS PivotTable
            LEFT JOIN bty.dbo.personal p ON PivotTable.pin = p.dni COLLATE DATABASE_DEFAULT
            LEFT JOIN bty.dbo.areas a ON p.idArea = a.id
            GROUP BY p.dni, p.nombres, p.apellidos, a.descripcion
            HAVING SUM(CASE WHEN PivotTable.mi > '09:00:59' THEN 1 ELSE 0 END) > 0 OR SUM(CASE WHEN PivotTable.ti > '14:00:59' THEN 1 ELSE 0 END) > 0
            ORDER BY minutosTotal DESC
            """;

        List<AttendanceStatsDTO.PersonLatenessDTO> topTardones = jdbcTemplate.query(
                queryRankingPersonas,
                (rs, rowNum) -> AttendanceStatsDTO.PersonLatenessDTO.builder()
                        .dni(rs.getString("dni"))
                        .nombreCompleto(rs.getString("nombreCompleto"))
                        .area(rs.getString("area"))
                        .minutosTardanza(rs.getInt("minutosTotal"))
                        .build(),
                fechaI, fechaF
        );

        // 2. Ranking de Áreas
        Map<String, List<AttendanceStatsDTO.PersonLatenessDTO>> groupedByArea = topTardones.stream()
                .filter(p -> p.getArea() != null)
                .collect(Collectors.groupingBy(AttendanceStatsDTO.PersonLatenessDTO::getArea));

        List<AttendanceStatsDTO.AreaLatenessDTO> areasTardonas = groupedByArea.entrySet().stream()
                .map(entry -> AttendanceStatsDTO.AreaLatenessDTO.builder()
                        .nombreArea(entry.getKey())
                        .totalMinutos(entry.getValue().stream().mapToInt(AttendanceStatsDTO.PersonLatenessDTO::getMinutosTardanza).sum())
                        .cantidadPersonas(entry.getValue().size())
                        .build())
                .sorted((a1, a2) -> Integer.compare(a2.getTotalMinutos(), a1.getTotalMinutos()))
                .collect(Collectors.toList());

        return AttendanceStatsDTO.builder()
                .topTardones(topTardones.stream().limit(10).collect(Collectors.toList()))
                .areasTardonas(areasTardonas)
                .totalTardanzas(topTardones.size())
                .build();
    }
    public List<IdNameDTO> getPersonalStatuses() {
        return jdbcTemplate.query("SELECT id, descripcion FROM bty.dbo.personalEstados ORDER BY id",
                (rs, rowNum) -> IdNameDTO.builder().id(rs.getInt("id")).name(rs.getString("descripcion")).build());
    }

    public List<IdNameDTO> getPersonalTypes() {
        return jdbcTemplate.query("SELECT id, descripcion FROM bty.dbo.personalTipos ORDER BY id",
                (rs, rowNum) -> IdNameDTO.builder().id(rs.getInt("id")).name(rs.getString("descripcion")).build());
    }

    /**
     * Obtiene el historial completo de marcaciones de un trabajador en una fecha específica.
     */
    public List<Map<String, Object>> getRawLogs(String dni, String fecha) {
        String query = """
            SELECT FORMAT(time, 'HH:mm:ss') as tiempo, 
                   'Marcación' as evento
            FROM ZKAccess1.dbo.acc_monitor_log
            WHERE pin = ? AND CONVERT(date, time) = ?
            ORDER BY time ASC
            """;
        return jdbcTemplate.queryForList(query, dni, fecha);
    }

    public List<PersonDTO> searchPersonal(String query) {
        log.info("Searching personal with query: {}", query);
        String sql = """
            SELECT TOP 10 
                   p.dni, 
                   p.nombres + ' ' + p.apellidos as nombreCompleto, 
                   a.descripcion as area,
                   pu.descripcion as puesto
            FROM bty.dbo.personal p
            LEFT JOIN bty.dbo.areas a ON p.idArea = a.id
            LEFT JOIN bty.dbo.puestos pu ON p.idPuesto = pu.id
            WHERE p.dni LIKE ? 
               OR p.nombres LIKE ? 
               OR p.apellidos LIKE ?
               OR (p.nombres + ' ' + p.apellidos) LIKE ?
            ORDER BY p.idEstado ASC, p.apellidos ASC
            """;
        
        String searchTerm = "%" + query.replace(" ", "%") + "%";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> PersonDTO.builder()
                .dni(rs.getString("dni"))
                .nombreCompleto(rs.getString("nombreCompleto"))
                .area(rs.getString("area"))
                .puesto(rs.getString("puesto"))
                .build(), 
                "%" + query + "%", searchTerm, searchTerm, searchTerm);
    }
}
