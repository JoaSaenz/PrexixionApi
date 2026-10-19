package com.joa.prexixionapi.modules.jobSunat.repositories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.modules.jobSunat.dto.SunatBuzonDTO;
import com.joa.prexixionapi.modules.jobSunat.dto.SunatBuzonDataTablesRequest;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SunatBuzonRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final Map<String, String> IBOX_ALL_ORDER_BY = new HashMap<>();

    static {
        IBOX_ALL_ORDER_BY.put("1", "cE.descripcion"); // Estado
        IBOX_ALL_ORDER_BY.put("2", "ge.descripcion"); // Grupo Económico
        IBOX_ALL_ORDER_BY.put("3", "c.y");             // Y
        IBOX_ALL_ORDER_BY.put("4", "c.nombreCorto");   // Signer/Nombre Corto
        IBOX_ALL_ORDER_BY.put("5", "c.ruc");           // RUC
    }

    public List<SunatBuzonDTO> list(SunatBuzonDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, c.idEstado, cE.descripcion AS estado, 
                c.ruc, c.y, c.razonSocial, c.nombreCorto, sc.notificaciones, 
                sc.tipos, sc.nombresCortos,
                ISNULL(sc.adjuntosDiaCount, 0) as adjuntosDiaCount, 
                ISNULL(sc.notifDiaCount, 0) as notifDiaCount, 
                ISNULL(sc.notifPendientesCount, 0) as notifPendientesCount 
                FROM cliente c 
                LEFT JOIN ( 
                  SELECT n.ruc, 
                         STRING_AGG(n.titulo , CHAR(13) + CHAR(10)) AS notificaciones, 
                         STRING_AGG(n.tipo , CHAR(13) + CHAR(10)) AS tipos, 
                         STRING_AGG(n.nombreCorto , CHAR(13) + CHAR(10)) AS nombresCortos, 
                         SUM(t.adjCount) as adjuntosDiaCount, 
                         COUNT(CASE WHEN t.adjCount > 0 THEN 1 END) as notifDiaCount, 
                         COUNT(n.id) as notifTotalDiaCount,
                         COUNT(CASE WHEN t.adjCount > 0 AND (n.revisado = 0 OR n.revisado IS NULL) THEN 1 END) as notifPendientesCount 
                  FROM sunatBuzonNotificacion n 
                  OUTER APPLY ( 
                      SELECT COUNT(*) as adjCount FROM sunatBuzonAdjunto sba WHERE sba.notificacion_id = n.id 
                  ) t 
                  WHERE CONVERT(char(10), n.fecha, 120) = :fecha 
                  GROUP BY n.ruc 
                ) sc ON c.ruc = sc.ruc 
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id 
                INNER JOIN clientsEstados cE ON c.idEstado = cE.id 
                WHERE 1=1 
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fecha", req.getFecha());
        appendFilters(sql, params, req);
        sql.append(buildOrderClause(req.getSortKey(), req.getSortDir()));

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            SunatBuzonDTO dto = new SunatBuzonDTO();
            dto.setRuc(rs.getString("ruc"));
            dto.setY(rs.getString("y"));
            dto.setRazonSocial(rs.getString("razonSocial"));
            dto.setNombreCorto(rs.getString("nombreCorto"));
            dto.setIdGrupoEconomico(rs.getInt("idGrupoEconomico"));
            dto.setGrupoEconomico(rs.getString("descGrupoEconomico"));
            dto.setIdEstado(rs.getInt("idEstado"));
            dto.setEstado(rs.getString("estado"));
            dto.setNotificaciones(rs.getString("notificaciones"));
            dto.setTipos(rs.getString("tipos"));
            dto.setNombresCortos(rs.getString("nombresCortos"));
            
            int totalDia = rs.getInt("notifDiaCount");
            int pendientesDia = rs.getInt("notifPendientesCount");
            dto.setNotificacionesDiaCount(totalDia);
            dto.setTieneAdjuntosDia(rs.getInt("adjuntosDiaCount") > 0);
            dto.setRevisadoDia(totalDia > 0 && pendientesDia == 0);
            return dto;
        });
    }

    public int countTotal(SunatBuzonDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM cliente c 
                LEFT JOIN ( 
                  SELECT n.ruc, COUNT(CASE WHEN t.adjCount > 0 THEN 1 END) as notifDiaCount, COUNT(n.id) as notifTotalDiaCount 
                  FROM sunatBuzonNotificacion n 
                  OUTER APPLY ( SELECT COUNT(*) as adjCount FROM sunatBuzonAdjunto sba WHERE sba.notificacion_id = n.id ) t 
                  WHERE CONVERT(char(10), n.fecha, 120) = :fecha 
                  GROUP BY n.ruc 
                ) sc ON c.ruc = sc.ruc 
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id 
                INNER JOIN clientsEstados cE ON c.idEstado = cE.id 
                WHERE 1=1 
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fecha", req.getFecha());
        appendFilters(sql, params, req);

        Integer count = jdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        return count != null ? count : 0;
    }

    private void appendFilters(StringBuilder query, MapSqlParameterSource params, SunatBuzonDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.append(" AND (c.ruc LIKE :search OR c.razonSocial LIKE :search OR c.nombreCorto LIKE :search) ");
            params.addValue("search", "%" + req.getSearch().trim() + "%");
        }

        if (req.getGrupoEconomicoString() != null && !req.getGrupoEconomicoString().isEmpty()) {
            query.append(" AND c.idGrupoEconomico IN (:gruposE) ");
            params.addValue("gruposE", parseCsvToIntList(req.getGrupoEconomicoString()));
        }

        if (req.getEstadosString() != null && !req.getEstadosString().isEmpty()) {
            query.append(" AND c.idEstado IN (:estados) ");
            params.addValue("estados", parseCsvToIntList(req.getEstadosString()));
        }

        if (req.getGruposString() != null && !req.getGruposString().isEmpty()) {
            query.append(" AND c.y IN (:grupos) ");
            params.addValue("grupos", Arrays.asList(req.getGruposString().split(",")));
        }

        String tieneNotif = req.getTieneNotificacionString();
        if (tieneNotif != null && !tieneNotif.isEmpty()) {
            List<Integer> filters = Arrays.stream(tieneNotif.split(",")).map(Integer::parseInt).toList();
            if (filters.size() == 1) {
                if (filters.contains(1)) {
                    query.append(" AND ISNULL(sc.notifTotalDiaCount, 0) > 0 ");
                } else if (filters.contains(0)) {
                    query.append(" AND ISNULL(sc.notifTotalDiaCount, 0) = 0 ");
                }
            }
        }

        String tieneAdjunto = req.getTieneAdjuntoString();
        if (tieneAdjunto != null && !tieneAdjunto.isEmpty()) {
            List<Integer> filters = Arrays.stream(tieneAdjunto.split(",")).map(Integer::parseInt).toList();
            if (filters.size() == 1) {
                if (filters.contains(1)) {
                    query.append(" AND ISNULL(sc.notifDiaCount, 0) > 0 ");
                } else if (filters.contains(0)) {
                    query.append(" AND ISNULL(sc.notifDiaCount, 0) = 0 ");
                }
            }
        }
    }

    private List<Integer> parseCsvToIntList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
    }

    private String buildOrderClause(String sortKey, String sortDir) {
        String direction = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        if (sortKey == null || sortKey.isEmpty()) {
            return " ORDER BY c.ruc ASC ";
        }
        String expr = IBOX_ALL_ORDER_BY.get(sortKey);
        return " ORDER BY " + (expr != null ? expr : "c.ruc") + " " + direction;
    }
}
