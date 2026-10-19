package com.joa.prexixionapi.modules.cliente.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.dto.SignerNivelDTO;
import lombok.RequiredArgsConstructor;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDTO;
import com.joa.prexixionapi.modules.cliente.repositories.ClienteRepository;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesRequest;

@Repository
@RequiredArgsConstructor
public class ClienteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final Map<String, String> CLIE_ORDER_BY_MAPPING = new HashMap<>();

    static {
        CLIE_ORDER_BY_MAPPING.put("abrCategoria", "nc.abreviatura");
        CLIE_ORDER_BY_MAPPING.put("descGrupoEconomico", "ge.descripcion");
        CLIE_ORDER_BY_MAPPING.put("descEstado", "ce.descripcion");
        CLIE_ORDER_BY_MAPPING.put("altaCom", "altaCom");
        CLIE_ORDER_BY_MAPPING.put("periodoInicioCom", "periodoInicioCom");
        CLIE_ORDER_BY_MAPPING.put("abrServicio", "cts.abreviatura");
        CLIE_ORDER_BY_MAPPING.put("codigoCliente", "cl.codigoCliente");
        CLIE_ORDER_BY_MAPPING.put("ruc", "cl.ruc");
        CLIE_ORDER_BY_MAPPING.put("y", "cl.y");
        CLIE_ORDER_BY_MAPPING.put("abrContribuyente", "ctc.abreviatura");
        CLIE_ORDER_BY_MAPPING.put("razonSocial", "cl.razonSocial");
        CLIE_ORDER_BY_MAPPING.put("nombreCorto", "cl.nombreCorto");
        CLIE_ORDER_BY_MAPPING.put("periodoI", "periodoI");
        CLIE_ORDER_BY_MAPPING.put("periodoF", "periodoF");
        CLIE_ORDER_BY_MAPPING.put("taxReview", "cl.taxReview");
        CLIE_ORDER_BY_MAPPING.put("fEntregaTaxReview", "cl.fEntregaTaxReview");
        CLIE_ORDER_BY_MAPPING.put("ccbCuenta", "ccbCuenta");
        CLIE_ORDER_BY_MAPPING.put("ccbUsuario", "ccbUsuario");
        CLIE_ORDER_BY_MAPPING.put("ccbClave", "ccbClave");
        CLIE_ORDER_BY_MAPPING.put("periodoIActualizacion", "periodoIActualizacion");
        CLIE_ORDER_BY_MAPPING.put("periodoFActualizacion", "periodoFActualizacion");
    }

    public List<ClienteDTO> list(ClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT cl.idEstado, ce.descripcion AS descEstado, cl.idTipoServicio, cts.abreviatura AS abrServicio, cts.descripcion AS descServicio,
                cl.codigoCliente, cl.ruc, cl.y,
                cl.idContribuyente, ctc.descripcion AS descContribuyente, ctc.abreviatura AS abrContribuyente, cl.razonSocial, cl.nombreCorto,
                (SELECT TOP 1 CONCAT(stAnioDesde, '-', stMesDesde)periodoI FROM clienteServiciosTributarios y WHERE y.stIdServicioTributario = 4 AND y.idCliente = cl.ruc ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoI,
                (SELECT TOP 1 CONCAT(stAnioHasta, '-', stMesHasta)periodoF FROM clienteServiciosTributarios y WHERE y.stIdServicioTributario = 4 AND y.idCliente = cl.ruc ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoF,
                cl.taxReview, cl.fEntregaTaxReview,
                cl.solU, cl.solC, cl.upsU, cl.upsC, cl.soldierU, cl.soldierC,  cl.signerU, cl.signerC,
                (SELECT TOP 1 acInicioCom FROM clienteAltaCom x WHERE x.idCliente = cl.ruc ORDER BY acInicioCom DESC) as altaCom,
                (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio)periodoI FROM clienteAltaCom y WHERE y.idCliente = cl.ruc ORDER BY acAnioPeriodoInicio DESC, acMesPeriodoInicio DESC) AS periodoInicioCom,
                (SELECT TOP 1 CONCAT(acAnioPeriodoFin, '-', acMesPeriodoFin)periodoF FROM clienteAltaCom y WHERE y.idCliente = cl.ruc ORDER BY acAnioPeriodoInicio DESC, acMesPeriodoInicio DESC) AS periodoFinCom,
                cl.fInscripcion, cl.fRetiro,
                (SELECT SUBSTRING((SELECT ',' + ccbCuenta AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999) AS CbCuenta) AS ccbCuenta,
                (SELECT SUBSTRING((SELECT ',' + ccbUsuario AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999) AS CbUsuario) AS ccbUsuario,
                (SELECT SUBSTRING((SELECT ',' + ccbClave AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999) AS CbClave) AS ccbClave,
                (SELECT TOP 1 CONCAT(soAnioDesde, '-', soMesDesde)periodoIActualizacion FROM clienteServiciosOtros x WHERE x.soIdServicioOtro = 4 AND x.idCliente = cl.ruc ORDER BY soAnioDesde DESC, soMesDesde DESC) AS periodoIActualizacion,
                (SELECT TOP 1 CONCAT(soAnioHasta, '-', soMesHasta)periodoFActualizacion FROM clienteServiciosOtros x WHERE x.soIdServicioOtro = 4 AND x.idCliente = cl.ruc ORDER BY soAnioDesde DESC, soMesDesde DESC) AS periodoFActualizacion,
                s.idCategoria, nc.abreviatura as abrCategoria, nc.descripcion as descCategoria,
                CASE WHEN cl.idGrupoEconomico IS NOT NULL AND cl.idGrupoEconomico NOT IN (0, 1) THEN 1 ELSE 0 END AS categoriaGrupoE,
                CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore,
                cl.idGrupoEconomico, ge.descripcion as descGrupoEconomico
                FROM cliente cl
                LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id
                LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id
                LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id
                LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                LEFT JOIN (SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6) cso ON cl.ruc = cso.idCliente
                WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(buildClienteOrderClause(req.getSortKey(), req.getSortDir()));

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            ClienteDTO obj = new ClienteDTO();
            obj.setEstado(new GclassDTO(rs.getInt("idEstado"), rs.getString("descEstado")));
            obj.setServicio(new GclassDTO(rs.getInt("idTipoServicio"), rs.getString("abrServicio"), rs.getString("descServicio")));
            obj.setCodigoCliente(rs.getString("codigoCliente"));
            obj.setRuc(rs.getString("ruc"));
            obj.setY(rs.getString("y"));
            obj.setContribuyente(new GclassDTO(rs.getInt("idContribuyente"), rs.getString("abrContribuyente"), rs.getString("descContribuyente")));
            
            String razonSocial = rs.getString("razonSocial");
            if (razonSocial != null && razonSocial.length() > 40) {
                razonSocial = razonSocial.substring(0, 40) + "...";
            }
            obj.setRazonSocial(razonSocial);
            obj.setNombreCorto(rs.getString("nombreCorto"));
            obj.setPeriodoInicioCom(rs.getString("periodoInicioCom"));
            obj.setPeriodoFinCom(rs.getString("periodoFinCom"));
            obj.setPeriodoI621(rs.getString("periodoI"));
            obj.setPeriodoF621(rs.getString("periodoF"));
            obj.setTaxReview(rs.getInt("taxReview"));
            obj.setfEntregaTaxReview(rs.getString("fEntregaTaxReview"));
            obj.setSolU(rs.getString("solU"));
            obj.setSolC(rs.getString("solC"));
            obj.setUpsU(rs.getString("upsU"));
            obj.setUpsC(rs.getString("upsC"));
            obj.setSoldierU(rs.getString("soldierU"));
            obj.setSoldierC(rs.getString("soldierC"));
            obj.setSignerU(rs.getString("signerU"));
            obj.setSignerC(rs.getString("signerC"));
            obj.setfInscripcion(rs.getString("fInscripcion"));
            obj.setAltaCom(rs.getString("altaCom"));
            obj.setfRetiro(rs.getString("fRetiro"));
            obj.setCcbCuenta(rs.getString("ccbCuenta"));
            obj.setCcbUsuario(rs.getString("ccbUsuario"));
            obj.setCcbClave(rs.getString("ccbClave"));
            obj.setPeriodoIActualizacion(rs.getString("periodoIActualizacion"));
            obj.setPeriodoFActualizacion(rs.getString("periodoFActualizacion"));

            SignerNivelDTO sn = new SignerNivelDTO();
            sn.setCategoria(new GclassDTO(rs.getInt("idCategoria"), rs.getString("abrCategoria"), rs.getString("descCategoria")));
            sn.setCategoriaGrupoE(rs.getInt("categoriaGrupoE"));
            sn.setCategoriaStore(rs.getInt("categoriaStore"));
            obj.setSignerNivel(sn);

            obj.setGrupoEconomico(new GclassDTO(rs.getInt("idGrupoEconomico"), rs.getString("descGrupoEconomico")));
            return obj;
        });
    }

    public int countTotal(ClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM cliente cl 
                LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id 
                LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id 
                LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id 
                LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente 
                LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id 
                LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id 
                LEFT JOIN (SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6) cso ON cl.ruc = cso.idCliente 
                WHERE 1=1 
                """);
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);

        Integer count = jdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        return count != null ? count : 0;
    }
    
    public List<Map<String, Object>> getEmpresasSelector() {
        String sql = "SELECT ruc, razonSocial FROM cliente ORDER BY razonSocial";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ruc", rs.getString("ruc"));
            map.put("razonSocial", rs.getString("razonSocial"));
            return map;
        });
    }

    public Map<Integer, Integer> getSummaryEstados(ClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT cl.idEstado, COUNT(*) as cantidad FROM cliente cl
                LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id
                LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id
                LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id
                LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                LEFT JOIN (SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6) cso ON cl.ruc = cso.idCliente
                WHERE 1=1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" GROUP BY cl.idEstado ");

        Map<Integer, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql.toString(), params, rs -> {
            result.put(rs.getInt("idEstado"), rs.getInt("cantidad"));
        });
        return result;
    }

    public Map<Integer, Integer> getSummaryCategorias(ClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT s.idCategoria, COUNT(*) as cantidad FROM cliente cl
                LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id
                LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id
                LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id
                LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                LEFT JOIN (SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6) cso ON cl.ruc = cso.idCliente
                WHERE 1=1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" GROUP BY s.idCategoria ");

        Map<Integer, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql.toString(), params, rs -> {
            result.put(rs.getInt("idCategoria"), rs.getInt("cantidad"));
        });
        return result;
    }

    private void appendFilters(StringBuilder query, MapSqlParameterSource params, ClienteDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.append(" AND (cl.ruc LIKE :search OR cl.razonSocial LIKE :search OR cl.nombreCorto LIKE :search OR ge.descripcion LIKE :search OR cl.codigoCliente LIKE :search) ");
            params.addValue("search", "%" + req.getSearch().trim() + "%");
        }
        appendParsedIntCsvFilter(query, params, "s.idCategoria", req.getCategoriasString(), "categorias");

        String cge = req.getCategoriaGrupoEString();
        if (cge != null && !cge.trim().isEmpty()) {
            query.append(" AND (CASE WHEN cl.idGrupoEconomico IS NOT NULL AND cl.idGrupoEconomico NOT IN (0, 1) THEN 1 ELSE 0 END) IN (:categoriaGrupoE) ");
            params.addValue("categoriaGrupoE", parseCsvToIntList(cge));
        }

        String cs = req.getCategoriaStoreString();
        if (cs != null && !cs.trim().isEmpty()) {
            query.append(" AND (CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END) IN (:categoriaStore) ");
            params.addValue("categoriaStore", parseCsvToIntList(cs));
        }

        appendParsedIntCsvFilter(query, params, "cl.idGrupoEconomico", req.getGruposEconomicosString(), "gruposEconomicos");
        appendParsedIntCsvFilter(query, params, "cl.idEstado", req.getEstadosString(), "estados");

        String grupos = req.getGruposString();
        if (grupos != null && !grupos.trim().isEmpty()) {
            query.append(" AND cl.y IN (:grupos) ");
            params.addValue("grupos", parseCsvToStringList(grupos));
        }

        appendParsedIntCsvFilter(query, params, "cl.taxReview", req.getTaxReviewString(), "taxReview");
    }

    private void appendParsedIntCsvFilter(StringBuilder query, MapSqlParameterSource params, String columnExpr, String csv, String paramName) {
        if (csv == null || csv.trim().isEmpty())
            return;
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty())
                continue;
            try {
                int v = Integer.parseInt(t);
                if (v == 0)
                    includeZero = true;
                else
                    nonZero.add(v);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        if (!includeZero && nonZero.isEmpty())
            return;

        query.append(" AND (");
        if (!nonZero.isEmpty()) {
            query.append(columnExpr).append(" IN (:").append(paramName).append(")");
            params.addValue(paramName, nonZero);
            if (includeZero)
                query.append(" OR ");
        }
        if (includeZero) {
            query.append("(").append(columnExpr).append(" IS NULL OR ").append(columnExpr).append(" = 0)");
        }
        query.append(") ");
    }

    private List<Integer> parseCsvToIntList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private List<String> parseCsvToStringList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private String buildClienteOrderClause(String sortKey, String sortDir) {
        String direction = "desc".equalsIgnoreCase(sortDir != null ? sortDir.trim() : "") ? "DESC" : "ASC";
        if (sortKey == null || sortKey.trim().isEmpty()) {
            return " ORDER BY cl.ruc " + direction + " ";
        }
        String expr = CLIE_ORDER_BY_MAPPING.get(sortKey.trim());
        if (expr == null) {
            return " ORDER BY cl.ruc ASC ";
        }
        if ("cl.ruc".equalsIgnoreCase(expr.trim())) {
            return " ORDER BY cl.ruc " + direction + " ";
        }
        return " ORDER BY " + expr + " " + direction + ", cl.ruc ASC ";
    }
}
