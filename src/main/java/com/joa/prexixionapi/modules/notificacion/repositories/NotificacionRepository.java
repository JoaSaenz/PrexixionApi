package com.joa.prexixionapi.modules.notificacion.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.modules.notificacion.dto.NotificacionDTO;
import com.joa.prexixionapi.modules.notificacion.dto.NotificacionProjection;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import com.joa.prexixionapi.modules.notificacion.dto.NotificacionDTO;
import com.joa.prexixionapi.modules.notificacion.dto.NotificacionAdjuntoDTO;

@Repository
@RequiredArgsConstructor
public class NotificacionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<NotificacionDTO> notificacionRowMapper = (rs, rowNum) -> {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(rs.getLong("id"));
        dto.setRuc(rs.getString("ruc"));
        dto.setIdSunat(rs.getString("idSunat"));
        dto.setTitulo(rs.getString("titulo"));
        
        java.sql.Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) dto.setFecha(ts.toLocalDateTime());
        
        dto.setTipo(rs.getString("tipo"));
        dto.setNombreCorto(rs.getString("nombreCorto"));
        dto.setJobStatusId(rs.getLong("jobStatusId"));
        dto.setRevisado(rs.getBoolean("revisado"));
        return dto;
    };

    private final RowMapper<NotificacionAdjuntoDTO> adjuntoRowMapper = (rs, rowNum) -> {
        NotificacionAdjuntoDTO adj = new NotificacionAdjuntoDTO();
        adj.setId(rs.getLong("id"));
        adj.setNombre(rs.getString("nombre"));
        adj.setContenido(rs.getBytes("contenido"));
        return adj;
    };

    private void fetchAdjuntosForNotificacion(NotificacionDTO notif) {
        String sql = "SELECT * FROM sunatBuzonAdjunto WHERE notificacion_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", notif.getId());
        List<NotificacionAdjuntoDTO> adjuntos = jdbcTemplate.query(sql, params, adjuntoRowMapper);
        // Link parent reference if needed
        for (NotificacionAdjuntoDTO adj : adjuntos) {
            adj.setNotificacion(notif);
        }
        notif.setAdjuntos(adjuntos);
    }

    public Optional<NotificacionDTO> findById(Long id) {
        String sql = "SELECT * FROM sunatBuzonNotificacion WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id);
        List<NotificacionDTO> list = jdbcTemplate.query(sql, params, notificacionRowMapper);
        if (list.isEmpty()) return Optional.empty();
        NotificacionDTO notif = list.get(0);
        fetchAdjuntosForNotificacion(notif);
        return Optional.of(notif);
    }

    public List<NotificacionDTO> findByRucAndFechaBetween(String ruc, java.time.LocalDateTime inicio, java.time.LocalDateTime fin) {
        String sql = "SELECT * FROM sunatBuzonNotificacion WHERE ruc = :ruc AND fecha BETWEEN :inicio AND :fin";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ruc", ruc)
        .addValue("inicio", inicio)
        .addValue("fin", fin);
        List<NotificacionDTO> list = jdbcTemplate.query(sql, params, notificacionRowMapper);
        for (NotificacionDTO notif : list) {
            fetchAdjuntosForNotificacion(notif);
        }
        return list;
    }

    public void updateRevisado(Long id, boolean revisado) {
        String sql = "UPDATE sunatBuzonNotificacion SET revisado = :revisado WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id)
        .addValue("revisado", revisado ? 1 : 0);
        jdbcTemplate.update(sql, params);
    }

    public List<NotificacionDTO> findByJobStatusIdOrderByFechaDesc(Long jobStatusId) {
        String sql = "SELECT * FROM sunatBuzonNotificacion WHERE jobStatusId = :jobStatusId ORDER BY fecha DESC";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("jobStatusId", jobStatusId);
        return jdbcTemplate.query(sql, params, notificacionRowMapper);
    }

    private final RowMapper<NotificacionProjection> projectionRowMapper = (rs, rowNum) -> {
        final Long idVal = rs.getLong("id");
        final String rucVal = rs.getString("ruc");
        final String idSunatVal = rs.getString("idSunat");
        final String tituloVal = rs.getString("titulo");
        
        java.sql.Timestamp ts = rs.getTimestamp("fecha");
        final java.time.LocalDateTime fechaVal = ts != null ? ts.toLocalDateTime() : null;
        
        final String tipoVal = rs.getString("tipo");
        final String nombreCortoVal = rs.getString("nombreCorto");
        final Long jobStatusIdVal = rs.getLong("jobStatusId");
        final Boolean revisadoVal = rs.getBoolean("revisado");
        final String estadoClienteVal = rs.getString("estadoCliente");
        final String razonSocialVal = rs.getString("razonSocial");
        final String yVal = rs.getString("y");
        
        int idERaw = rs.getInt("idEstado");
        final Integer idEstadoVal = rs.wasNull() ? null : idERaw;

        return new NotificacionProjection() {
            @Override public Long getId() { return idVal; }
            @Override public String getRuc() { return rucVal; }
            @Override public String getIdSunat() { return idSunatVal; }
            @Override public String getTitulo() { return tituloVal; }
            @Override public java.time.LocalDateTime getFecha() { return fechaVal; }
            @Override public String getTipo() { return tipoVal; }
            @Override public String getNombreCorto() { return nombreCortoVal; }
            @Override public Long getJobStatusId() { return jobStatusIdVal; }
            @Override public Boolean getRevisado() { return revisadoVal; }
            @Override public String getEstadoCliente() { return estadoClienteVal; }
            @Override public String getRazonSocial() { return razonSocialVal; }
            @Override public String getY() { return yVal; }
            @Override public Integer getIdEstado() { return idEstadoVal; }
            @Override public Integer getTieneAdjuntos() { return null; }
        };
    };

    private final RowMapper<NotificacionProjection> resumenProjectionRowMapper = (rs, rowNum) -> {
        final Long idVal = rs.getLong("id");
        final String rucVal = rs.getString("ruc");
        final String idSunatVal = rs.getString("idSunat");
        final String tituloVal = rs.getString("titulo");
        
        java.sql.Timestamp ts = rs.getTimestamp("fecha");
        final java.time.LocalDateTime fechaVal = ts != null ? ts.toLocalDateTime() : null;
        
        final String tipoVal = rs.getString("tipo");
        final String nombreCortoVal = rs.getString("nombreCorto");
        final Boolean revisadoVal = rs.getBoolean("revisado");
        
        int tieneAdjRaw = rs.getInt("tieneAdjuntos");
        final Integer tieneAdjuntosVal = rs.wasNull() ? null : tieneAdjRaw;

        return new NotificacionProjection() {
            @Override public Long getId() { return idVal; }
            @Override public String getRuc() { return rucVal; }
            @Override public String getIdSunat() { return idSunatVal; }
            @Override public String getTitulo() { return tituloVal; }
            @Override public java.time.LocalDateTime getFecha() { return fechaVal; }
            @Override public String getTipo() { return tipoVal; }
            @Override public String getNombreCorto() { return nombreCortoVal; }
            @Override public Long getJobStatusId() { return null; }
            @Override public Boolean getRevisado() { return revisadoVal; }
            @Override public String getEstadoCliente() { return null; }
            @Override public String getRazonSocial() { return null; }
            @Override public String getY() { return null; }
            @Override public Integer getIdEstado() { return null; }
            @Override public Integer getTieneAdjuntos() { return tieneAdjuntosVal; }
        };
    };

    public List<NotificacionProjection> findNotificacionesWithEstadoByJobStatus(Long jobStatusId) {
        String sql = """
            SELECT n.*, ce.descripcion as estadoCliente, c.razonSocial as razonSocial, c.y as y, c.idEstado as idEstado
            FROM sunatBuzonNotificacion n
            LEFT JOIN cliente c ON n.ruc = c.ruc
            LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
            WHERE n.jobStatusId = :jobStatusId
            ORDER BY n.fecha DESC
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("jobStatusId", jobStatusId);
        return jdbcTemplate.query(sql, params, projectionRowMapper);
    }

    public List<NotificacionProjection> findResumenByRuc(String ruc) {
        String sql = """
            SELECT n.id, n.ruc, n.idSunat, n.titulo, n.fecha, n.revisado, n.tipo, n.nombreCorto,
                   (CASE WHEN EXISTS (SELECT 1 FROM sunatBuzonAdjunto a WHERE a.notificacion_id = n.id) THEN 1 ELSE 0 END) as tieneAdjuntos
            FROM sunatBuzonNotificacion n
            WHERE n.ruc = :ruc
            ORDER BY n.fecha DESC
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ruc", ruc);
        return jdbcTemplate.query(sql, params, resumenProjectionRowMapper);
    }
}
