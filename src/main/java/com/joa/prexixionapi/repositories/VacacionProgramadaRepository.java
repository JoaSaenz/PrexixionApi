package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.PersonDTO;
import com.joa.prexixionapi.dto.VacacionProgramadaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VacacionProgramadaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<VacacionProgramadaDTO> list(Integer idArea, String dni, String fechaI, String fechaF) {
        StringBuilder sql = new StringBuilder("""
                SELECT v.id,
                       v.dni,
                       CONCAT(p.nombres, ' ', p.apellidos) AS colaborador,
                       p.idArea,
                       COALESCE(a.descripcion, '') AS area,
                       p.idSubArea,
                       COALESCE(sa.descripcion, '') AS subArea,
                       v.fechaInicio,
                       v.fechaFin,
                       CONVERT(VARCHAR(10), DATEADD(day, 1, CAST(v.fechaFin AS DATE)), 23) AS fechaFinCalendar,
                       v.dias,
                       v.idEstado,
                       COALESCE(e.descripcion, 'PROGRAMADA') AS estado,
                       COALESCE(e.color, '#041562') AS color,
                       COALESCE(v.observacion, '') AS observacion
                FROM vacacionesProgramadas v
                INNER JOIN personal p ON v.dni = p.dni
                LEFT JOIN areas a ON p.idArea = a.id
                LEFT JOIN personalSubAreas sa ON p.idSubArea = sa.id
                LEFT JOIN vacacionesProgramadasEstados e ON v.idEstado = e.id
                WHERE 1 = 1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (idArea != null && idArea > 0) {
            sql.append(" AND p.idArea = :idArea ");
            params.addValue("idArea", idArea);
        }

        if (dni != null && !dni.trim().isEmpty()) {
            sql.append(" AND v.dni = :dni ");
            params.addValue("dni", dni.trim());
        }

        if (fechaI != null && !fechaI.trim().isEmpty() && fechaF != null && !fechaF.trim().isEmpty()) {
            sql.append(" AND v.fechaFin >= :fechaI AND v.fechaInicio <= :fechaF ");
            params.addValue("fechaI", fechaI.trim());
            params.addValue("fechaF", fechaF.trim());
        }

        sql.append(" ORDER BY v.fechaInicio ASC ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            String color = rs.getString("color");
            String estado = rs.getString("estado");
            String area = rs.getString("area");
            String subArea = rs.getString("subArea");

            String areaSubArea = "";
            if (area != null && !area.trim().isEmpty()) {
                if (subArea != null && !subArea.trim().isEmpty()) {
                    areaSubArea = " - " + area.trim() + " | " + subArea.trim();
                } else {
                    areaSubArea = " - " + area.trim();
                }
            }

            String title = rs.getString("colaborador") + " (" + rs.getInt("dias") + "d)" + areaSubArea;

            return VacacionProgramadaDTO.builder()
                    .id(rs.getInt("id"))
                    .dni(rs.getString("dni"))
                    .colaborador(rs.getString("colaborador"))
                    .idArea(rs.getInt("idArea"))
                    .area(area)
                    .idSubArea(rs.getInt("idSubArea"))
                    .subArea(subArea)
                    .fechaInicio(rs.getString("fechaInicio"))
                    .fechaFin(rs.getString("fechaFin"))
                    .dias(rs.getInt("dias"))
                    .idEstado(rs.getInt("idEstado"))
                    .estado(estado)
                    .observacion(rs.getString("observacion"))
                    .title(title)
                    .start(rs.getString("fechaInicio"))
                    .end(rs.getString("fechaFinCalendar"))
                    .color(color)
                    .textColor("#ffffff")
                    .allDay(true)
                    .build();
        });
    }

    public VacacionProgramadaDTO getOne(Integer id) {
        String sql = """
                SELECT v.id,
                       v.dni,
                       CONCAT(p.nombres, ' ', p.apellidos) AS colaborador,
                       p.idArea,
                       COALESCE(a.descripcion, '') AS area,
                       p.idSubArea,
                       COALESCE(sa.descripcion, '') AS subArea,
                       v.fechaInicio,
                       v.fechaFin,
                       CONVERT(VARCHAR(10), DATEADD(day, 1, CAST(v.fechaFin AS DATE)), 23) AS fechaFinCalendar,
                       v.dias,
                       v.idEstado,
                       COALESCE(e.descripcion, 'PROGRAMADA') AS estado,
                       COALESCE(e.color, '#041562') AS color,
                       COALESCE(v.observacion, '') AS observacion
                FROM vacacionesProgramadas v
                INNER JOIN personal p ON v.dni = p.dni
                LEFT JOIN areas a ON p.idArea = a.id
                LEFT JOIN personalSubAreas sa ON p.idSubArea = sa.id
                LEFT JOIN vacacionesProgramadasEstados e ON v.idEstado = e.id
                WHERE v.id = :id
                """;

        try {
            return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), (rs, rowNum) -> {
                String color = rs.getString("color");
                String estado = rs.getString("estado");
                String area = rs.getString("area");
                String subArea = rs.getString("subArea");

                String areaSubArea = "";
                if (area != null && !area.trim().isEmpty()) {
                    if (subArea != null && !subArea.trim().isEmpty()) {
                        areaSubArea = "[" + area.trim() + " - " + subArea.trim() + "] ";
                    } else {
                        areaSubArea = "[" + area.trim() + "] ";
                    }
                }

                String title = areaSubArea + rs.getString("colaborador") + " (" + rs.getInt("dias") + "d)";

                return VacacionProgramadaDTO.builder()
                        .id(rs.getInt("id"))
                        .dni(rs.getString("dni"))
                        .colaborador(rs.getString("colaborador"))
                        .idArea(rs.getInt("idArea"))
                        .area(area)
                        .idSubArea(rs.getInt("idSubArea"))
                        .subArea(subArea)
                        .fechaInicio(rs.getString("fechaInicio"))
                        .fechaFin(rs.getString("fechaFin"))
                        .dias(rs.getInt("dias"))
                        .idEstado(rs.getInt("idEstado"))
                        .estado(estado)
                        .observacion(rs.getString("observacion"))
                        .title(title)
                        .start(rs.getString("fechaInicio"))
                        .end(rs.getString("fechaFinCalendar"))
                        .color(color)
                        .textColor("#ffffff")
                        .allDay(true)
                        .build();
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean exist(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM vacacionesProgramadas WHERE id = :id";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), Integer.class);
        return count != null && count > 0;
    }

    public int insert(VacacionProgramadaDTO dto) {
        String sql = """
                INSERT INTO vacacionesProgramadas (dni, fechaInicio, fechaFin, dias, idEstado, observacion)
                VALUES (:dni, :fechaInicio, :fechaFin, :dias, :idEstado, :observacion)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("dni", dto.getDni())
                .addValue("fechaInicio", dto.getFechaInicio())
                .addValue("fechaFin", dto.getFechaFin())
                .addValue("dias", dto.getDias() != null ? dto.getDias() : 0)
                .addValue("idEstado", dto.getIdEstado() != null ? dto.getIdEstado() : 1)
                .addValue("observacion", dto.getObservacion());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public int update(VacacionProgramadaDTO dto) {
        String sql = """
                UPDATE vacacionesProgramadas
                SET fechaInicio = :fechaInicio,
                    fechaFin = :fechaFin,
                    dias = :dias,
                    idEstado = :idEstado,
                    observacion = :observacion
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", dto.getId())
                .addValue("fechaInicio", dto.getFechaInicio())
                .addValue("fechaFin", dto.getFechaFin())
                .addValue("dias", dto.getDias() != null ? dto.getDias() : 0)
                .addValue("idEstado", dto.getIdEstado() != null ? dto.getIdEstado() : 2)
                .addValue("observacion", dto.getObservacion());

        return jdbcTemplate.update(sql, params);
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM vacacionesProgramadas WHERE id = :id";
        return jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    public List<Map<String, Object>> getEstados() {
        String sql = "SELECT id, descripcion, color FROM vacacionesProgramadasEstados ORDER BY id ASC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getAreas() {
        String sql = "SELECT id, descripcion FROM areas ORDER BY descripcion ASC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }

    public List<PersonDTO> getPersonal(Integer idArea) {
        StringBuilder sql = new StringBuilder(
                """
                        SELECT p.dni,
                               CONCAT(p.nombres, ' ', p.apellidos,
                                      CASE WHEN p.idEstado != 2 THEN CONCAT(' [', COALESCE(e.descripcion, 'INACTIVO'), ']') ELSE '' END) AS nombreCompleto,
                               COALESCE(a.descripcion, '') AS area,
                               COALESCE(pu.descripcion, '') AS puesto
                        FROM personal p
                        LEFT JOIN areas a ON p.idArea = a.id
                        LEFT JOIN personalPuestos pu ON p.idPuesto = pu.id
                        LEFT JOIN personalEstados e ON p.idEstado = e.id
                        WHERE 1 = 1
                        """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (idArea != null && idArea > 0) {
            sql.append(" AND p.idArea = :idArea ");
            params.addValue("idArea", idArea);
        }
        sql.append(" ORDER BY CASE WHEN p.idEstado = 2 THEN 0 ELSE 1 END ASC, p.apellidos ASC, p.nombres ASC ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> PersonDTO.builder()
                .dni(rs.getString("dni"))
                .nombreCompleto(rs.getString("nombreCompleto"))
                .area(rs.getString("area"))
                .puesto(rs.getString("puesto"))
                .build());
    }
}
