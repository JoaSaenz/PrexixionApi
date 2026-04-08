package com.joa.prexixionapi.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ActivoTipoRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Object[]> findAllRaw() {
        String sql = "SELECT id, descripcion, depreciacionContable, depreciacionTributaria FROM bienesTipos ORDER BY descripcion";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        });
    }

    public Object[] findByIdRaw(String id) {
        String sql = "SELECT id, descripcion, depreciacionContable, depreciacionTributaria FROM bienesTipos WHERE id = ?";
        List<Object[]> rows = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void saveActivoTipo(String id, String descripcion, Double porcentajeContable, Double porcentajeTributario) {
        String sql = """
                IF EXISTS (SELECT 1 FROM bienesTipos WHERE id = ?)
                    UPDATE bienesTipos SET descripcion = ?, depreciacionContable = ?, depreciacionTributaria = ? WHERE id = ?
                ELSE
                    INSERT INTO bienesTipos (id, descripcion, depreciacionContable, depreciacionTributaria) VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, id, descripcion, porcentajeContable, porcentajeTributario, id, id, descripcion, porcentajeContable, porcentajeTributario);
    }
}
