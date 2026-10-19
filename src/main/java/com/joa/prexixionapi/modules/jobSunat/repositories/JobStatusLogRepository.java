package com.joa.prexixionapi.modules.jobSunat.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.joa.prexixionapi.modules.jobSunat.dto.JobStatusLogProjection;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JobStatusLogRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<JobStatusLogProjection> projectionRowMapper = (rs, rowNum) -> {
        final Long idVal = rs.getLong("id");
        final String rucVal = rs.getString("ruc");
        final String yVal = rs.getString("y");
        final String resultadoVal = rs.getString("resultado");
        final String mensajeVal = rs.getString("mensaje");

        long durRaw = rs.getLong("duracionMs");
        final Long duracionMsVal = rs.wasNull() ? null : durRaw;

        java.sql.Timestamp ts = rs.getTimestamp("fechaRegistro");
        final java.time.LocalDateTime fechaRegistroVal = ts != null ? ts.toLocalDateTime() : null;

        int nuevasRaw = rs.getInt("nuevasNotificaciones");
        final Integer nuevasNotificacionesVal = rs.wasNull() ? null : nuevasRaw;

        final String estadoClienteVal = rs.getString("estadoCliente");
        final String razonSocialVal = rs.getString("razonSocial");

        int idERaw = rs.getInt("idEstado");
        final Integer idEstadoVal = rs.wasNull() ? null : idERaw;

        return new JobStatusLogProjection() {
            @Override
            public Long getId() {
                return idVal;
            }

            @Override
            public String getRuc() {
                return rucVal;
            }

            @Override
            public String getY() {
                return yVal;
            }

            @Override
            public String getResultado() {
                return resultadoVal;
            }

            @Override
            public String getMensaje() {
                return mensajeVal;
            }

            @Override
            public Long getDuracionMs() {
                return duracionMsVal;
            }

            @Override
            public java.time.LocalDateTime getFechaRegistro() {
                return fechaRegistroVal;
            }

            @Override
            public Integer getNuevasNotificaciones() {
                return nuevasNotificacionesVal;
            }

            @Override
            public String getEstadoCliente() {
                return estadoClienteVal;
            }

            @Override
            public String getRazonSocial() {
                return razonSocialVal;
            }

            @Override
            public Integer getIdEstado() {
                return idEstadoVal;
            }
        };
    };

    public List<JobStatusLogProjection> findLogsWithEstadoByJobStatus(Long jobStatusId) {
        String sql = """
                    SELECT jsl.*, ce.descripcion as estadoCliente, c.razonSocial as razonSocial, c.idEstado as idEstado
                    FROM jobStatusLog jsl
                    LEFT JOIN cliente c ON jsl.ruc = c.ruc
                    LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
                    WHERE jsl.jobStatusId = :jobStatusId
                    ORDER BY jsl.fechaRegistro ASC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("jobStatusId", jobStatusId);
        return jdbcTemplate.query(sql, params, projectionRowMapper);
    }
}
