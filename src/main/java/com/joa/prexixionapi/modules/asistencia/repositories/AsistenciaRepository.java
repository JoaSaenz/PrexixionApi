package com.joa.prexixionapi.modules.asistencia.repositories;

import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaDTO;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AsistenciaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<AsistenciaDTO> getMarcacionesDiarias(String fecha) {
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
                    WHERE time >= :fecha AND time < DATEADD(day, 1, :fecha)
                ) AS SourceTable PIVOT(min(tiempo) FOR [turno] IN([mi],[ms],[ti],[ts],[tarde])) AS PivotTable;
                """.formatted(fecha);

        return jdbcTemplate.query(queryAsistencia, new MapSqlParameterSource("fecha", fecha), (rs, rowNum) -> AsistenciaDTO.builder()
                .dni(rs.getString("dni"))
                .fecha(rs.getString("fecha"))
                .mi(rs.getString("mi") != null ? rs.getString("mi") : "")
                .ms(rs.getString("ms") != null ? rs.getString("ms") : "")
                .ti(rs.getString("ti") != null ? rs.getString("ti") : "")
                .ts(rs.getString("ts") != null ? rs.getString("ts") : "")
                .tarde(rs.getString("tarde") != null ? rs.getString("tarde") : "")
                .build());
    }

    public List<AsistenciaDTO> getPersonalActivo(List<Integer> statuses, List<Integer> types) {
        StringBuilder queryPersonal = new StringBuilder(
                """
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
            queryPersonal.append(" AND p.idEstado IN (")
                    .append(statuses.stream().map(String::valueOf).collect(Collectors.joining(","))).append(")");
        } else {
            queryPersonal.append(" AND p.idEstado IN (1, 2)"); // Por defecto activos/vigentes
        }

        if (types != null && !types.isEmpty()) {
            queryPersonal.append(" AND p.idTipo IN (")
                    .append(types.stream().map(String::valueOf).collect(Collectors.joining(","))).append(")");
        }

        return jdbcTemplate.query(queryPersonal.toString(), new MapSqlParameterSource(),
                (rs, rowNum) -> AsistenciaDTO.builder()
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
    }

    public List<AsistenciaDTO> getMonthlyAsistenciaBase(String dni, String fechaI, String fechaF) {
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
                    WHERE time >= :fecha AND time < DATEADD(day, 1, :fecha) AND pin = ?
                ) AS SourceTable PIVOT(min(tiempo) FOR [turno] IN([mi],[ms],[ti],[ts],[tarde])) AS PivotTable
                ORDER BY fecha ASC;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("fechaI", fechaI)
        .addValue("fechaF", fechaF)
        .addValue("dni", dni);
        return jdbcTemplate.query(query, params, (rs, rowNum) -> AsistenciaDTO.builder()
                .dni(rs.getString("dni"))
                .fecha(rs.getString("fecha"))
                .mi(rs.getString("mi") != null ? rs.getString("mi") : "")
                .ms(rs.getString("ms") != null ? rs.getString("ms") : "")
                .ti(rs.getString("ti") != null ? rs.getString("ti") : "")
                .ts(rs.getString("ts") != null ? rs.getString("ts") : "")
                .tarde(rs.getString("tarde") != null ? rs.getString("tarde") : "")
                .build());
    }

    public List<AsistenciaStatsDTO.PersonLatenessDTO> getTopTardones(String fechaI, String fechaF) {
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
                    WHERE time >= :fechaI AND time < :fechaF
                ) AS SourceTable PIVOT ( MIN(tiempo) FOR turno IN ([mi], [ti]) ) AS PivotTable
                LEFT JOIN bty.dbo.personal p ON PivotTable.pin = p.dni COLLATE DATABASE_DEFAULT
                LEFT JOIN bty.dbo.areas a ON p.idArea = a.id
                GROUP BY p.dni, p.nombres, p.apellidos, a.descripcion
                HAVING SUM(CASE WHEN PivotTable.mi > '09:00:59' THEN 1 ELSE 0 END) > 0 OR SUM(CASE WHEN PivotTable.ti > '14:00:59' THEN 1 ELSE 0 END) > 0
                ORDER BY minutosTotal DESC
                """;

        return jdbcTemplate.query(
                queryRankingPersonas, new MapSqlParameterSource().addValue("fechaI", fechaI).addValue("fechaF", fechaF), (rs, rowNum) -> AsistenciaStatsDTO.PersonLatenessDTO.builder()
                        .dni(rs.getString("dni"))
                        .nombreCompleto(rs.getString("nombreCompleto"))
                        .area(rs.getString("area"))
                        .minutosTardanza(rs.getInt("minutosTotal"))
                        .build());
    }


    public List<Map<String, Object>> getRawLogs(String dni, String fecha) {
        String query = """
                SELECT FORMAT(time, 'HH:mm:ss') as tiempo,
                       'Marcación' as evento
                FROM ZKAccess1.dbo.acc_monitor_log
                WHERE pin = :dni AND CONVERT(date, time) = :fecha
                ORDER BY time ASC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("dni", dni)
        .addValue("fecha", fecha);
        return jdbcTemplate.queryForList(query, params);
    }

}
