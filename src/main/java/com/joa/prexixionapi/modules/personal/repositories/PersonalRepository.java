package com.joa.prexixionapi.modules.personal.repositories;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.modules.personal.dto.PersonalDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PersonalRepository {

        private final NamedParameterJdbcTemplate jdbcTemplate;

        public List<GclassDTO> getPersonalStatuses() {
                return jdbcTemplate.query("SELECT id, descripcion FROM bty.dbo.personalEstados ORDER BY id",
                                new MapSqlParameterSource(),
                                (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
        }

        public List<GclassDTO> getPersonalTypes() {
                return jdbcTemplate.query("SELECT id, descripcion FROM bty.dbo.personalTipos ORDER BY id",
                                new MapSqlParameterSource(),
                                (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
        }

        public List<PersonalDTO> searchPersonal(String query) {
                String sql = """
                                SELECT TOP 10
                                       p.dni,
                                       p.nombres + ' ' + p.apellidos as nombreCompleto,
                                       a.descripcion as area,
                                       pu.descripcion as puesto
                                FROM bty.dbo.personal p
                                LEFT JOIN bty.dbo.areas a ON p.idArea = a.id
                                LEFT JOIN bty.dbo.personalPuestos pu ON p.idPuesto = pu.id
                                WHERE p.dni LIKE :dni
                                   OR p.nombres LIKE :nombres
                                   OR p.apellidos LIKE :apellidos
                                   OR (p.nombres + ' ' + p.apellidos) LIKE :nombreCompleto
                                ORDER BY p.idEstado ASC, p.apellidos ASC
                                """;

                String searchTerm = "%" + query.replace(" ", "%") + "%";

                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("dni", "%" + query + "%")
                                .addValue("nombres", searchTerm)
                                .addValue("apellidos", searchTerm)
                                .addValue("nombreCompleto", searchTerm);
                return jdbcTemplate.query(sql, params, (rs, rowNum) -> PersonalDTO.builder()
                                .dni(rs.getString("dni"))
                                .nombreCompleto(rs.getString("nombreCompleto"))
                                .area(rs.getString("area"))
                                .puesto(rs.getString("puesto"))
                                .build());
        }

        public List<PersonalDTO> getResponsablesPeriodo(String tabla, String anio, String mes) {
                // Validar nombre de tabla para evitar SQL injection
                if (!tabla.matches("^[a-zA-Z0-9_]+$")) {
                        throw new IllegalArgumentException("Nombre de tabla inválido");
                }

                String sql = "SELECT r.dni, p.apellidos, p.nombres " +
                                "FROM bty.dbo." + tabla + " r " +
                                "LEFT JOIN bty.dbo.personal p ON r.dni = p.dni " +
                                "WHERE CAST(r.anioDesde + r.mesDesde + '01' AS DATE) <= :fecha " +
                                "AND (CAST(r.anioHasta + r.mesHasta + '01' AS DATE) >= :fecha OR r.anioHasta IS NULL) "
                                +
                                "ORDER BY p.apellidos";

                String fecha = anio + "-" + mes + "-01";
                MapSqlParameterSource params = new MapSqlParameterSource().addValue("fecha", fecha);

                List<PersonalDTO> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> PersonalDTO.builder()
                                .dni(rs.getString("dni"))
                                .nombreCompleto(rs.getString("apellidos") + " " + rs.getString("nombres"))
                                .build());

                // Add VACÍO at index 0
                list.add(0, PersonalDTO.builder().dni("0").nombreCompleto("VACÍO ").build());

                return list;
        }
}
