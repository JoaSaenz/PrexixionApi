package com.joa.prexixionapi.modules.jobSunat.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusDTO;

@Repository
@RequiredArgsConstructor
public class JobStatusRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<JobStatusDTO> rowMapper = (rs, rowNum) -> {
        JobStatusDTO dto = new JobStatusDTO();
        dto.setId(rs.getLong("id"));
        dto.setNombreJob(rs.getString("nombreJob"));
        dto.setEstado(rs.getString("estado"));
        dto.setProgreso(rs.getDouble("progreso"));
        dto.setMensaje(rs.getString("mensaje"));
        dto.setUltimaActualizacion(rs.getString("ultimaActualizacion"));
        
        java.sql.Date fechaSql = rs.getDate("fechaEjecucion");
        if (fechaSql != null) dto.setFechaEjecucion(fechaSql.toLocalDate());
        
        java.sql.Timestamp horaInicioSql = rs.getTimestamp("horaInicio");
        if (horaInicioSql != null) dto.setHoraInicio(horaInicioSql.toLocalDateTime());
        
        java.sql.Timestamp horaFinSql = rs.getTimestamp("horaFin");
        if (horaFinSql != null) dto.setHoraFin(horaFinSql.toLocalDateTime());
        
        dto.setRucsOk(rs.getInt("rucsOk"));
        dto.setRucsNoOk(rs.getInt("rucsNoOk"));
        return dto;
    };

    public Optional<JobStatusDTO> findById(Long id) {
        String sql = "SELECT * FROM jobStatus WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id);
        List<JobStatusDTO> list = jdbcTemplate.query(sql, params, rowMapper);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<JobStatusDTO> findTopByNombreJobOrderByHoraInicioDesc(String nombreJob) {
        String sql = "SELECT TOP 1 * FROM jobStatus WHERE nombreJob = :nombreJob ORDER BY horaInicio DESC";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("nombreJob", nombreJob);
        List<JobStatusDTO> list = jdbcTemplate.query(sql, params, rowMapper);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<JobStatusDTO> findTop7ByNombreJobOrderByHoraInicioDesc(String nombreJob) {
        String sql = "SELECT TOP 7 * FROM jobStatus WHERE nombreJob = :nombreJob ORDER BY horaInicio DESC";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("nombreJob", nombreJob);
        return jdbcTemplate.query(sql, params, rowMapper);
    }
}
