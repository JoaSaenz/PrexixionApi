package com.joa.prexixionapi.modules.cronograma.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.joa.prexixionapi.modules.cronograma.dto.CronogramaPDTDTO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CronogramaPDTRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<CronogramaPDTDTO> rowMapper = (rs, rowNum) -> {
        CronogramaPDTDTO dto = new CronogramaPDTDTO();
        dto.setAnio(rs.getString("anio"));
        dto.setMes(rs.getString("mes"));
        dto.setFecha0(rs.getString("fecha0"));
        dto.setFecha1(rs.getString("fecha1"));
        dto.setFecha2(rs.getString("fecha2"));
        dto.setFecha3(rs.getString("fecha3"));
        dto.setFecha4(rs.getString("fecha4"));
        dto.setFecha5(rs.getString("fecha5"));
        dto.setFecha6(rs.getString("fecha6"));
        dto.setFecha7(rs.getString("fecha7"));
        dto.setFecha8(rs.getString("fecha8"));
        dto.setFecha9(rs.getString("fecha9"));
        dto.setFechab(rs.getString("fechab"));
        return dto;
    };

    public Optional<CronogramaPDTDTO> findByAnioAndMes(String anio, String mes) {
        String sql = "SELECT * FROM CRONOGRAMAPDT WHERE anio = :anio AND mes = :mes";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("anio", anio)
        .addValue("mes", mes);
        List<CronogramaPDTDTO> list = jdbcTemplate.query(sql, params, rowMapper);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
