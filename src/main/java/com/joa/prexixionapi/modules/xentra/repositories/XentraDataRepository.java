package com.joa.prexixionapi.modules.xentra.repositories;

import com.joa.prexixionapi.modules.xentra.dto.XentraDataDTO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class XentraDataRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public int create(XentraDataDTO dto) {
        String sql = "INSERT INTO xentraData (idArea, idSubArea, abreviatura, nombre, color, responsable, " +
                "fechaInicio, fechaFin, tipoRepeticion, diasSemana, intervaloSemanas, mesesPermitidos, " +
                "diaInicioMes, diaFinMes, estado) VALUES " +
                "(:idArea, :idSubArea, :abreviatura, :nombre, :color, :responsable, " +
                ":fechaInicio, :fechaFin, :tipoRepeticion, :diasSemana, :intervaloSemanas, :mesesPermitidos, " +
                ":diaInicioMes, :diaFinMes, :estado)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idArea", dto.getIdArea())
                .addValue("idSubArea", dto.getIdSubArea())
                .addValue("abreviatura", dto.getAbreviatura())
                .addValue("nombre", dto.getNombre())
                .addValue("color", dto.getColor())
                .addValue("responsable", dto.getResponsable())
                .addValue("fechaInicio", dto.getFechaInicio())
                .addValue("fechaFin", dto.getFechaFin())
                .addValue("tipoRepeticion", dto.getTipoRepeticion())
                .addValue("diasSemana", dto.getDiasSemanaString())
                .addValue("intervaloSemanas", dto.getIntervaloSemanas())
                .addValue("mesesPermitidos", dto.getMesesPermitidosString())
                .addValue("diaInicioMes", dto.getDiaInicioMes())
                .addValue("diaFinMes", dto.getDiaFinMes())
                .addValue("estado", dto.getEstado());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        return keyHolder.getKey().intValue();
    }
}
