package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.entities.TradeCliente;
import com.joa.prexixionapi.entities.TradeClienteBudget;
import com.joa.prexixionapi.dto.TradeClienteDataTablesRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class TradeClienteRepository {

    @PersistenceContext
    private EntityManager em;

    private static final Map<String, String> ORDER_BY_MAP = new HashMap<>();

    static {
        ORDER_BY_MAP.put("codigoCliente", "tc.codigoCliente");
        ORDER_BY_MAP.put("fRegistro", "tc.fRegistro");
        ORDER_BY_MAP.put("fTermino", "tc.fTermino");
        ORDER_BY_MAP.put("descEstado", "test.descripcion");
        ORDER_BY_MAP.put("ruc", "tc.ruc");
        ORDER_BY_MAP.put("razonSocial", "tc.razonSocial");
        ORDER_BY_MAP.put("buFechaInicio", "tcBU.buFechaInicio");
        ORDER_BY_MAP.put("buFechaFin", "tcBU.buFechaFin");
        ORDER_BY_MAP.put("descRespuesta", "tcr.descripcion");
        ORDER_BY_MAP.put("buNumero", "tcBU.buNumero");
        ORDER_BY_MAP.put("descTipoServicio", "tcts.descripcion");
        ORDER_BY_MAP.put("descTipoCosto", "tctc.descripcion");
        ORDER_BY_MAP.put("buImporte", "tcBU.buImporte");
        ORDER_BY_MAP.put("descSideProduccion", "tcsp.descripcion");
        ORDER_BY_MAP.put("buObservacion", "tcBU.buObservacion");
    }

    private String getBaseSelect() {
        return """
                    SELECT tc.codigoCliente, tc.ruc, tc.razonSocial, tc.idContribuyente, ctc.name AS descContribuyente,\s
                    tc.idEstado, test.descripcion AS descEstado, tc.fRegistro, tc.fTermino,\s
                    tcBU.buFechaInicio, tcBU.buFechaFin, tcBU.buIdRespuesta, tcr.descripcion AS descRespuesta, tcBU.buNumero,\s
                    tcBU.buIdTipoServicio, tcts.descripcion AS descTipoServicio,\s
                    tcBU.buIdTipoCosto, tctc.descripcion AS descTipoCosto,\s
                    tcBU.buImporte,\s
                    tcBU.buIdSideProduccion, tcsp.descripcion AS descSideProduccion,
                    tcBU.buObservacion,\s
                    (SELECT TOP 1 ctNombre FROM tradeClientesContactos x where x.idTradeCliente = tc.codigoCliente) as nombresContacto,\s
                    (SELECT TOP 1 ctTelefono FROM tradeClientesContactos x where x.idTradeCliente = tc.codigoCliente) as telefonosContacto,\s
                    (SELECT TOP 1 ctCorreo FROM tradeClientesContactos x where x.idTradeCliente = tc.codigoCliente) as correosContacto\s
                    FROM tradeClientesBudgets tcBU\s
                    LEFT JOIN tradeClientes tc ON tcBU.idTradeCliente = tc.codigoCliente\s
                    LEFT JOIN clienteTipoContribuyente ctc ON tc.idContribuyente = ctc.id\s
                    LEFT JOIN tradeClientesEstados test ON tc.idEstado = test.id\s
                    LEFT JOIN tradeClientesRespuestas tcr ON tcBU.buIdRespuesta = tcr.id\s
                    LEFT JOIN tradeClientesTiposServicios tcts ON tcBU.buIdTipoServicio = tcts.id\s
                    LEFT JOIN tradeClientesTiposCostos tctc ON tcBU.buIdTipoCosto = tctc.id
                    LEFT JOIN tradeClientesSidesProduccion tcsp ON tcBu.buIdSideProduccion = tcsp.id
                    WHERE 1=1
                """;
    }

    @SuppressWarnings("unchecked")
    public List<TradeCliente> listServerSide(TradeClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder(getBaseSelect());
        appendFilters(sql, req);
        sql.append(buildOrderClause(req.getSortKey(), req.getSortDir()));

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);

        return mapTuples((List<Tuple>) query.getResultList());
    }

    public Map<Integer, Integer> getSummaryEstadosServerSide(TradeClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                    SELECT tc.idEstado, COUNT(tcBU.buId) as cantidad\s
                    FROM tradeClientesBudgets tcBU\s
                    LEFT JOIN tradeClientes tc ON tcBU.idTradeCliente = tc.codigoCliente\s
                    LEFT JOIN clienteTipoContribuyente ctc ON tc.idContribuyente = ctc.id\s
                    LEFT JOIN tradeClientesEstados test ON tc.idEstado = test.id\s
                    LEFT JOIN tradeClientesRespuestas tcr ON tcBU.buIdRespuesta = tcr.id\s
                    LEFT JOIN tradeClientesTiposServicios tcts ON tcBU.buIdTipoServicio = tcts.id\s
                    LEFT JOIN tradeClientesTiposCostos tctc ON tcBU.buIdTipoCosto = tctc.id
                    LEFT JOIN tradeClientesSidesProduccion tcsp ON tcBu.buIdSideProduccion = tcsp.id
                    WHERE 1=1
                """);
        appendFilters(sql, req);
        sql.append(" GROUP BY tc.idEstado ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);

        Map<Integer, Integer> result = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Tuple> tuples = (List<Tuple>) query.getResultList();
        for (Tuple t : tuples) {
            Number idEstado = t.get("idEstado", Number.class);
            Number cantidad = t.get("cantidad", Number.class);
            if (idEstado != null) {
                result.put(idEstado.intValue(), cantidad.intValue());
            }
        }
        return result;
    }

    private void appendFilters(StringBuilder query, TradeClienteDataTablesRequest req) {
        // Filtro de búsqueda (Ruc, razonSocial, buNumero o codigoCliente)
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.append(" AND (tc.ruc LIKE :search OR tc.razonSocial LIKE :search OR tcBU.buNumero LIKE :search OR CAST(tc.codigoCliente AS VARCHAR) LIKE :search) ");
        }

        // Filtro de periodos
        if (req.getPeriodoI() != null && !req.getPeriodoI().trim().isEmpty() &&
            req.getPeriodoF() != null && !req.getPeriodoF().trim().isEmpty()) {
            query.append(" AND :fechaInicial <= tcBU.buFechaInicio AND tcBU.buFechaInicio <= :fechaFinal ");
        }

        // Filtro de Estados
        appendParsedIntCsvFilter(query, "tc.idEstado", req.getEstadosString(), "estados");
        // Filtro de Respuestas
        appendParsedIntCsvFilter(query, "tcBU.buIdRespuesta", req.getRespuestasString(), "respuestas");
        // Filtro de Servicios
        appendParsedIntCsvFilter(query, "tcBU.buIdTipoServicio", req.getServiciosString(), "servicios");
    }

    private void setFilterParams(Query query, TradeClienteDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.setParameter("search", "%" + req.getSearch().trim() + "%");
        }

        if (req.getPeriodoI() != null && !req.getPeriodoI().trim().isEmpty() &&
            req.getPeriodoF() != null && !req.getPeriodoF().trim().isEmpty()) {
            query.setParameter("fechaInicial", req.getPeriodoI().trim() + "-01");
            query.setParameter("fechaFinal", req.getPeriodoF().trim() + "-31");
        }

        bindParsedIntCsv(query, req.getEstadosString(), "estados");
        bindParsedIntCsv(query, req.getRespuestasString(), "respuestas");
        bindParsedIntCsv(query, req.getServiciosString(), "servicios");
    }

    private void appendParsedIntCsvFilter(StringBuilder query, String columnExpr, String csv, String paramName) {
        if (csv == null || csv.trim().isEmpty()) {
            return;
        }
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) continue;
            try {
                int v = Integer.parseInt(t);
                if (v == 0) {
                    includeZero = true;
                } else {
                    nonZero.add(v);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid values
            }
        }
        if (!includeZero && nonZero.isEmpty()) {
            return;
        }

        query.append(" AND (");
        if (!nonZero.isEmpty()) {
            query.append(columnExpr).append(" IN (:").append(paramName).append(")");
            if (includeZero) {
                query.append(" OR ");
            }
        }
        if (includeZero) {
            query.append("(").append(columnExpr).append(" IS NULL OR ").append(columnExpr).append(" = 0)");
        }
        query.append(") ");
    }

    private void bindParsedIntCsv(Query query, String csv, String paramName) {
        if (csv == null || csv.trim().isEmpty()) {
            return;
        }
        List<Integer> nonZero = new ArrayList<>();
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) continue;
            try {
                int v = Integer.parseInt(t);
                if (v != 0) {
                    nonZero.add(v);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid values
            }
        }
        if (!nonZero.isEmpty()) {
            query.setParameter(paramName, nonZero);
        }
    }

    private String buildOrderClause(String sortKey, String sortDir) {
        String direction = "desc".equalsIgnoreCase(sortDir != null ? sortDir.trim() : "") ? "DESC" : "ASC";
        if (sortKey == null || sortKey.trim().isEmpty()) {
            return " ORDER BY tcBU.buFechaInicio ASC, tc.codigoCliente ASC ";
        }
        String expr = ORDER_BY_MAP.get(sortKey.trim());
        if (expr == null) {
            return " ORDER BY tcBU.buFechaInicio ASC, tc.codigoCliente ASC ";
        }
        return " ORDER BY " + expr + " " + direction + ", tc.codigoCliente ASC ";
    }

    private List<TradeCliente> mapTuples(List<Tuple> tuples) {
        List<TradeCliente> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
            TradeCliente obj = new TradeCliente();
            obj.setCodigoCliente(tuple.get("codigoCliente", Integer.class) == null ? 0 : tuple.get("codigoCliente", Integer.class));
            obj.setRuc(tuple.get("ruc", String.class));
            obj.setRazonSocial(tuple.get("razonSocial", String.class));
            obj.setIdContribuyente(tuple.get("idContribuyente", Integer.class) == null ? 0 : tuple.get("idContribuyente", Integer.class));
            obj.setContribuyenteDescripcion(tuple.get("descContribuyente", String.class));
            obj.setIdEstado(tuple.get("idEstado", Integer.class) == null ? 0 : tuple.get("idEstado", Integer.class));
            obj.setEstadoDescripcion(tuple.get("descEstado", String.class));
            obj.setfRegistro(tuple.get("fRegistro", String.class));
            obj.setfTermino(tuple.get("fTermino", String.class));

            TradeClienteBudget budget = new TradeClienteBudget();
            budget.setBuFechaInicio(tuple.get("buFechaInicio", String.class));
            budget.setBuFechaFin(tuple.get("buFechaFin", String.class));
            budget.setBuIdRespuesta(tuple.get("buIdRespuesta", Integer.class) == null ? 0 : tuple.get("buIdRespuesta", Integer.class));
            budget.setBuRespuestaDescripcion(tuple.get("descRespuesta", String.class));
            budget.setBuNumero(tuple.get("buNumero", String.class));
            budget.setBuIdTipoServicio(tuple.get("buIdTipoServicio", Integer.class) == null ? 0 : tuple.get("buIdTipoServicio", Integer.class));
            budget.setBuTpServicioDescripcion(tuple.get("descTipoServicio", String.class));
            budget.setBuIdTipoCosto(tuple.get("buIdTipoCosto", Integer.class) == null ? 0 : tuple.get("buIdTipoCosto", Integer.class));
            budget.setBuTipoCostoDescripcion(tuple.get("descTipoCosto", String.class));
            Number buImporteNum = tuple.get("buImporte", Number.class);
            budget.setBuImporte(buImporteNum == null ? 0.0 : buImporteNum.doubleValue());
            budget.setBuIdSideProduccion(tuple.get("buIdSideProduccion", Integer.class) == null ? 0 : tuple.get("buIdSideProduccion", Integer.class));
            budget.setBuSideProduccionDescripcion(tuple.get("descSideProduccion", String.class));
            budget.setBuObservacion(tuple.get("buObservacion", String.class));
            obj.setBudget(budget);

            obj.setTcContactoNombre(tuple.get("nombresContacto", String.class));
            obj.setTcContactoTelefono(tuple.get("telefonosContacto", String.class));
            obj.setTcContactoCorreo(tuple.get("correosContacto", String.class));

            list.add(obj);
        }
        return list;
    }
}
