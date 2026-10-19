package com.joa.prexixionapi.modules.xentra.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.joa.prexixionapi.modules.xentra.dto.XentraFechaDTO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class XentraFechaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void saveAll(List<XentraFechaDTO> fechas) {
        if (fechas == null || fechas.isEmpty()) return;

        String sql = "INSERT INTO xentraFechas (idXentra, fecha, idEstado, estadoLogico) " +
                     "VALUES (:idXentra, :fecha, :idEstado, :estadoLogico)";
        
        Map<String, Object>[] batchValues = fechas.stream().map(f -> new MapSqlParameterSource()
            .addValue("idXentra", f.getIdXentra() != null ? f.getIdXentra() : 0)
            .addValue("fecha", f.getFecha())
            .addValue("idEstado", f.getIdEstado())
            .addValue("estadoLogico", f.getEstadoLogico() != null ? f.getEstadoLogico() : 1)
            .getValues()
        ).toArray(Map[]::new);

        jdbcTemplate.batchUpdate(sql, batchValues);
    }
}
