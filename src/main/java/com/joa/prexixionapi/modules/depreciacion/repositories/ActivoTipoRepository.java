package com.joa.prexixionapi.modules.depreciacion.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoTipoDTO;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoTipoRepository;

@Repository
@RequiredArgsConstructor
public class ActivoTipoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ActivoTipoDTO> list() {
        String sql = "SELECT id, descripcion, depreciacionContable, depreciacionTributaria FROM bienesTipos ORDER BY descripcion";
        return jdbcTemplate.query(sql, (rs, rowNum) -> ActivoTipoDTO.builder()
                .id(rs.getString("id"))
                .descripcion(rs.getString("descripcion"))
                .depreciacionContable(rs.getObject("depreciacionContable") != null ? rs.getDouble("depreciacionContable") : null)
                .depreciacionTributaria(rs.getObject("depreciacionTributaria") != null ? rs.getDouble("depreciacionTributaria") : null)
                .build());
    }

    public ActivoTipoDTO getById(String id) {
        String sql = "SELECT id, descripcion, depreciacionContable, depreciacionTributaria FROM bienesTipos WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<ActivoTipoDTO> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> ActivoTipoDTO.builder()
                .id(rs.getString("id"))
                .descripcion(rs.getString("descripcion"))
                .depreciacionContable(rs.getObject("depreciacionContable") != null ? rs.getDouble("depreciacionContable") : null)
                .depreciacionTributaria(rs.getObject("depreciacionTributaria") != null ? rs.getDouble("depreciacionTributaria") : null)
                .build());
        return list.isEmpty() ? null : list.get(0);
    }

    public void saveActivoTipo(String id, String descripcion, Double porcentajeContable, Double porcentajeTributario) {
        String sql = """
                IF EXISTS (SELECT 1 FROM bienesTipos WHERE id = :id)
                    UPDATE bienesTipos 
                    SET descripcion = :descripcion, 
                        depreciacionContable = :depreciacionContable, 
                        depreciacionTributaria = :depreciacionTributaria 
                    WHERE id = :id
                ELSE
                    INSERT INTO bienesTipos (id, descripcion, depreciacionContable, depreciacionTributaria) 
                    VALUES (:id, :descripcion, :depreciacionContable, :depreciacionTributaria)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("descripcion", descripcion)
                .addValue("depreciacionContable", porcentajeContable)
                .addValue("depreciacionTributaria", porcentajeTributario);
        jdbcTemplate.update(sql, params);
    }
}
