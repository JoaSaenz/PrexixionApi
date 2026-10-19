package com.joa.prexixionapi.modules.trade.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.modules.trade.dto.TradeClienteDTO;
import com.joa.prexixionapi.modules.trade.dto.TradeClienteBudgetDTO;
import com.joa.prexixionapi.modules.trade.dto.TradeClienteDataTablesRequest;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TradeClienteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

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

    public List<TradeClienteDTO> list(TradeClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT tc.codigoCliente, tc.ruc, tc.razonSocial, tc.idContribuyente, ctc.name AS descContribuyente, 
                tc.idEstado, test.descripcion AS descEstado, tc.fRegistro, tc.fTermino, 
                tcBU.buFechaInicio, tcBU.buFechaFin, tcBU.buIdRespuesta, tcr.descripcion AS descRespuesta, tcBU.buNumero, 
                tcBU.buIdTipoServicio, tcts.descripcion AS descTipoServicio, 
                tcBU.buIdTipoCosto, tctc.descripcion AS descTipoCosto, 
                tcBU.buImporte, 
                tcBU.buIdSideProduccion, tcsp.descripcion AS descSideProduccion,
                tcBU.buObservacion, 
                (SELECT TOP 1 ctNombre FROM tradeClientesContactos x where x.idTradeCliente = tc.codigoCliente) as nombresContacto, 
                (SELECT TOP 1 ctTelefono FROM tradeClientesContactos x where x.idTradeCliente = tc.codigoCliente) as telefonosContacto, 
                (SELECT TOP 1 ctCorreo FROM tradeClientesContactos x where x.idTradeCliente = tc.codigoCliente) as correosContacto 
                FROM tradeClientesBudgets tcBU 
                LEFT JOIN tradeClientes tc ON tcBU.idTradeCliente = tc.codigoCliente 
                LEFT JOIN clienteTipoContribuyente ctc ON tc.idContribuyente = ctc.id 
                LEFT JOIN tradeClientesEstados test ON tc.idEstado = test.id 
                LEFT JOIN tradeClientesRespuestas tcr ON tcBU.buIdRespuesta = tcr.id 
                LEFT JOIN tradeClientesTiposServicios tcts ON tcBU.buIdTipoServicio = tcts.id 
                LEFT JOIN tradeClientesTiposCostos tctc ON tcBU.buIdTipoCosto = tctc.id
                LEFT JOIN tradeClientesSidesProduccion tcsp ON tcBu.buIdSideProduccion = tcsp.id
                WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(buildOrderClause(req.getSortKey(), req.getSortDir()));

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            TradeClienteDTO obj = new TradeClienteDTO();
            obj.setCodigoCliente(rs.getInt("codigoCliente"));
            obj.setRuc(rs.getString("ruc"));
            obj.setRazonSocial(rs.getString("razonSocial"));
            obj.setIdContribuyente(rs.getInt("idContribuyente"));
            obj.setContribuyenteDescripcion(rs.getString("descContribuyente"));
            obj.setIdEstado(rs.getInt("idEstado"));
            obj.setEstadoDescripcion(rs.getString("descEstado"));
            obj.setfRegistro(rs.getString("fRegistro"));
            obj.setfTermino(rs.getString("fTermino"));

            TradeClienteBudgetDTO budget = new TradeClienteBudgetDTO();
            budget.setBuFechaInicio(rs.getString("buFechaInicio"));
            budget.setBuFechaFin(rs.getString("buFechaFin"));
            budget.setBuIdRespuesta(rs.getInt("buIdRespuesta"));
            budget.setBuRespuestaDescripcion(rs.getString("descRespuesta"));
            budget.setBuNumero(rs.getString("buNumero"));
            budget.setBuIdTipoServicio(rs.getInt("buIdTipoServicio"));
            budget.setBuTpServicioDescripcion(rs.getString("descTipoServicio"));
            budget.setBuIdTipoCosto(rs.getInt("buIdTipoCosto"));
            budget.setBuTipoCostoDescripcion(rs.getString("descTipoCosto"));
            budget.setBuImporte(rs.getDouble("buImporte"));
            budget.setBuIdSideProduccion(rs.getInt("buIdSideProduccion"));
            budget.setBuSideProduccionDescripcion(rs.getString("descSideProduccion"));
            budget.setBuObservacion(rs.getString("buObservacion"));
            obj.setBudget(budget);

            obj.setTcContactoNombre(rs.getString("nombresContacto"));
            obj.setTcContactoTelefono(rs.getString("telefonosContacto"));
            obj.setTcContactoCorreo(rs.getString("correosContacto"));
            return obj;
        });
    }

    public Map<Integer, Integer> getSummaryEstados(TradeClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT tc.idEstado, COUNT(tcBU.buId) as cantidad 
                FROM tradeClientesBudgets tcBU 
                LEFT JOIN tradeClientes tc ON tcBU.idTradeCliente = tc.codigoCliente 
                LEFT JOIN clienteTipoContribuyente ctc ON tc.idContribuyente = ctc.id 
                LEFT JOIN tradeClientesEstados test ON tc.idEstado = test.id 
                LEFT JOIN tradeClientesRespuestas tcr ON tcBU.buIdRespuesta = tcr.id 
                LEFT JOIN tradeClientesTiposServicios tcts ON tcBU.buIdTipoServicio = tcts.id 
                LEFT JOIN tradeClientesTiposCostos tctc ON tcBU.buIdTipoCosto = tctc.id
                LEFT JOIN tradeClientesSidesProduccion tcsp ON tcBu.buIdSideProduccion = tcsp.id
                WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" GROUP BY tc.idEstado ");

        Map<Integer, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql.toString(), params, rs -> {
            result.put(rs.getInt("idEstado"), rs.getInt("cantidad"));
        });
        return result;
    }

    private void appendFilters(StringBuilder query, MapSqlParameterSource params, TradeClienteDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.append(" AND (tc.ruc LIKE :search OR tc.razonSocial LIKE :search OR tcBU.buNumero LIKE :search OR CAST(tc.codigoCliente AS VARCHAR) LIKE :search) ");
            params.addValue("search", "%" + req.getSearch().trim() + "%");
        }

        if (req.getPeriodoI() != null && !req.getPeriodoI().trim().isEmpty() &&
            req.getPeriodoF() != null && !req.getPeriodoF().trim().isEmpty()) {
            query.append(" AND :fechaInicial <= tcBU.buFechaInicio AND tcBU.buFechaInicio <= :fechaFinal ");
            params.addValue("fechaInicial", req.getPeriodoI().trim() + "-01");
            params.addValue("fechaFinal", req.getPeriodoF().trim() + "-31");
        }

        appendParsedIntCsvFilter(query, params, "tc.idEstado", req.getEstadosString(), "estados");
        appendParsedIntCsvFilter(query, params, "tcBU.buIdRespuesta", req.getRespuestasString(), "respuestas");
        appendParsedIntCsvFilter(query, params, "tcBU.buIdTipoServicio", req.getServiciosString(), "servicios");
    }

    private void appendParsedIntCsvFilter(StringBuilder query, MapSqlParameterSource params, String columnExpr, String csv, String paramName) {
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
            params.addValue(paramName, nonZero);
            if (includeZero) {
                query.append(" OR ");
            }
        }
        if (includeZero) {
            query.append("(").append(columnExpr).append(" IS NULL OR ").append(columnExpr).append(" = 0)");
        }
        query.append(") ");
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
}
