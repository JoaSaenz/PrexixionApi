package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.dto.SunatBuzonDTO;
import com.joa.prexixionapi.dto.SunatBuzonDataTablesRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class SunatBuzonRepository {

    @PersistenceContext
    private EntityManager em;

    private static final Map<String, String> IBOX_SERVER_SIDE_ORDER_BY = new HashMap<>();

    static {
        IBOX_SERVER_SIDE_ORDER_BY.put("1", "ge.descripcion"); // Grupo Económico
        IBOX_SERVER_SIDE_ORDER_BY.put("2", "cE.descripcion"); // Estado
        IBOX_SERVER_SIDE_ORDER_BY.put("3", "c.ruc");           // RUC
        IBOX_SERVER_SIDE_ORDER_BY.put("4", "c.y");             // Y
        IBOX_SERVER_SIDE_ORDER_BY.put("5", "c.nombreCorto");   // Signer/Nombre Corto
    }

    private String getBaseSelect(String fecha) {
        return """
                SELECT c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, c.idEstado, cE.descripcion AS estado, 
                c.ruc, c.y, c.razonSocial, c.nombreCorto, STRING_AGG(sbn.titulo , CHAR(13) + CHAR(10)) AS notificaciones, 
                (SELECT COUNT(*) FROM sunatBuzonAdjunto sba JOIN sunatBuzonNotificacion n ON sba.notificacion_id = n.id 
                 WHERE n.ruc = c.ruc AND CONVERT(char(10), n.fecha, 120) = '""" + fecha + """
                ') as adjuntosDiaCount, 
                (SELECT COUNT(*) FROM sunatBuzonNotificacion n 
                 WHERE n.ruc = c.ruc AND CONVERT(char(10), n.fecha, 120) = '""" + fecha + """
                ' AND EXISTS (SELECT 1 FROM sunatBuzonAdjunto sba WHERE sba.notificacion_id = n.id)) as notifDiaCount, 
                (SELECT COUNT(*) FROM sunatBuzonNotificacion n 
                 WHERE n.ruc = c.ruc AND CONVERT(char(10), n.fecha, 120) = '""" + fecha + """
                ' AND (n.revisado = 0 OR n.revisado IS NULL) AND EXISTS (SELECT 1 FROM sunatBuzonAdjunto sba WHERE sba.notificacion_id = n.id)) as notifPendientesCount 
                FROM cliente c 
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id 
                INNER JOIN clientsEstados cE ON c.idEstado = cE.id 
                LEFT JOIN sunatBuzonNotificacion sbn ON c.ruc = sbn.ruc 
                AND CONVERT(char(10), sbn.fecha, 120) = '""" + fecha + """
                ' 
                """;
    }

    @SuppressWarnings("unchecked")
    public List<SunatBuzonDTO> listServerSide(SunatBuzonDataTablesRequest req) {
        StringBuilder sql = new StringBuilder(getBaseSelect(req.getFecha()));
        sql.append(" WHERE 1=1 ");
        appendFilters(sql, req);
        sql.append(" GROUP BY c.idGrupoEconomico, ge.descripcion, c.idEstado, cE.descripcion, c.ruc, c.y, c.razonSocial, c.nombreCorto ");
        
        // El HAVING se usa para el filtro de "Tiene Notificación" porque notifDiaCount es un subquery/agregado
        appendHavingFilters(sql, req);

        sql.append(buildOrderClause(req.getSortKey(), req.getSortDir()));
        sql.append(" OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);
        query.setParameter("offset", req.getStart());
        query.setParameter("limit", req.getLength());

        return mapTuples((List<Tuple>) query.getResultList());
    }

    public int countServerSide(SunatBuzonDataTablesRequest req) {
        // Para el conteo con filtros complejos y HAVING, necesitamos envolverlo en una subconsulta
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sql.append("SELECT c.ruc ");
        sql.append("FROM cliente c ");
        sql.append("LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id ");
        sql.append("INNER JOIN clientsEstados cE ON c.idEstado = cE.id ");
        sql.append("LEFT JOIN sunatBuzonNotificacion sbn ON c.ruc = sbn.ruc AND CONVERT(char(10), sbn.fecha, 120) = :fecha ");
        sql.append(" WHERE 1=1 ");
        appendFilters(sql, req);
        sql.append(" GROUP BY c.idGrupoEconomico, ge.descripcion, c.idEstado, cE.descripcion, c.ruc, c.y, c.razonSocial, c.nombreCorto ");
        appendHavingFilters(sql, req);
        sql.append(" ) as sub ");

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("fecha", req.getFecha());
        setFilterParams(query, req);
        return ((Number) query.getSingleResult()).intValue();
    }

    private void appendFilters(StringBuilder query, SunatBuzonDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.append(" AND (c.ruc LIKE :search OR c.razonSocial LIKE :search OR c.nombreCorto LIKE :search) ");
        }

        if (req.getGrupoEconomicoString() != null && !req.getGrupoEconomicoString().isEmpty()) {
            query.append(" AND c.idGrupoEconomico IN (:gruposE) ");
        }

        if (req.getEstadosString() != null && !req.getEstadosString().isEmpty()) {
            query.append(" AND c.idEstado IN (:estados) ");
        }

        if (req.getGruposString() != null && !req.getGruposString().isEmpty()) {
            query.append(" AND c.y IN (:grupos) ");
        }
    }

    private void appendHavingFilters(StringBuilder query, SunatBuzonDataTablesRequest req) {
        String tieneNotif = req.getTieneNotificacionString();
        if (tieneNotif != null && !tieneNotif.isEmpty()) {
            // "1" = SI (notifDiaCount > 0), "0" = NO (notifDiaCount = 0)
            List<Integer> filters = Arrays.stream(tieneNotif.split(",")).map(Integer::parseInt).toList();
            if (filters.size() == 1) {
                if (filters.contains(1)) {
                    query.append(" HAVING (SELECT COUNT(*) FROM sunatBuzonNotificacion n WHERE n.ruc = c.ruc AND CONVERT(char(10), n.fecha, 120) = :fecha AND EXISTS (SELECT 1 FROM sunatBuzonAdjunto sba WHERE sba.notificacion_id = n.id)) > 0 ");
                } else if (filters.contains(0)) {
                    query.append(" HAVING (SELECT COUNT(*) FROM sunatBuzonNotificacion n WHERE n.ruc = c.ruc AND CONVERT(char(10), n.fecha, 120) = :fecha AND EXISTS (SELECT 1 FROM sunatBuzonAdjunto sba WHERE sba.notificacion_id = n.id)) = 0 ");
                }
            }
        }
    }

    private void setFilterParams(Query query, SunatBuzonDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.setParameter("search", "%" + req.getSearch().trim() + "%");
        }
        if (req.getGrupoEconomicoString() != null && !req.getGrupoEconomicoString().isEmpty()) {
            query.setParameter("gruposE", parseCsvToIntList(req.getGrupoEconomicoString()));
        }
        if (req.getEstadosString() != null && !req.getEstadosString().isEmpty()) {
            query.setParameter("estados", parseCsvToIntList(req.getEstadosString()));
        }
        if (req.getGruposString() != null && !req.getGruposString().isEmpty()) {
            query.setParameter("grupos", Arrays.asList(req.getGruposString().split(",")));
        }
        // El parámetro fecha ya se usó en el constructor de SQL pero si se usa en appendHavingFilters como :fecha:
        try {
            query.setParameter("fecha", req.getFecha());
        } catch (IllegalArgumentException e) {
            // Ignorar si no está el parámetro en la query
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
        String expr = IBOX_SERVER_SIDE_ORDER_BY.get(sortKey);
        return " ORDER BY " + (expr != null ? expr : "c.ruc") + " " + direction;
    }

    private List<SunatBuzonDTO> mapTuples(List<Tuple> tuples) {
        List<SunatBuzonDTO> list = new ArrayList<>();
        for (Tuple t : tuples) {
            SunatBuzonDTO dto = new SunatBuzonDTO();
            dto.setRuc(t.get("ruc", String.class));
            dto.setY(t.get("y", Character.class) != null ? t.get("y", Character.class).toString() : null);
            dto.setRazonSocial(t.get("razonSocial", String.class));
            dto.setNombreCorto(t.get("nombreCorto", String.class));
            dto.setIdGrupoEconomico(t.get("idGrupoEconomico", Integer.class) != null ? t.get("idGrupoEconomico", Integer.class) : 0);
            dto.setGrupoEconomico(t.get("descGrupoEconomico", String.class));
            dto.setIdEstado(t.get("idEstado", Integer.class) != null ? t.get("idEstado", Integer.class) : 0);
            dto.setEstado(t.get("estado", String.class));
            dto.setNotificaciones(t.get("notificaciones", String.class));
            
            int totalDia = t.get("notifDiaCount", Number.class).intValue();
            int pendientesDia = t.get("notifPendientesCount", Number.class).intValue();
            dto.setNotificacionesDiaCount(totalDia);
            dto.setTieneAdjuntosDia(t.get("adjuntosDiaCount", Number.class).intValue() > 0);
            dto.setRevisadoDia(totalDia > 0 && pendientesDia == 0);
            
            list.add(dto);
        }
        return list;
    }
}
