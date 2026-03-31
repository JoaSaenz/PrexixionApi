package com.joa.prexixion.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.Cliente;
import com.joa.prexixion.entities.Gclass;
import com.joa.prexixion.entities.SignerNivel;
import com.joa.prexixion.dto.ClienteDataTablesRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class ClienteRepository {

    @PersistenceContext
    private EntityManager em;

    private static final Map<String, String> CLIE_SERVER_SIDE_ORDER_BY = new HashMap<>();

    static {
        CLIE_SERVER_SIDE_ORDER_BY.put("abrCategoria", "nc.abreviatura");
        CLIE_SERVER_SIDE_ORDER_BY.put("descGrupoEconomico", "ge.descripcion");
        CLIE_SERVER_SIDE_ORDER_BY.put("descEstado", "ce.descripcion");
        CLIE_SERVER_SIDE_ORDER_BY.put("altaCom", "altaCom");
        CLIE_SERVER_SIDE_ORDER_BY.put("periodoInicioCom", "periodoInicioCom");
        CLIE_SERVER_SIDE_ORDER_BY.put("abrServicio", "cts.abreviatura");
        CLIE_SERVER_SIDE_ORDER_BY.put("codigoCliente", "cl.codigoCliente");
        CLIE_SERVER_SIDE_ORDER_BY.put("ruc", "cl.ruc");
        CLIE_SERVER_SIDE_ORDER_BY.put("y", "cl.y");
        CLIE_SERVER_SIDE_ORDER_BY.put("abrContribuyente", "ctc.abreviatura");
        CLIE_SERVER_SIDE_ORDER_BY.put("razonSocial", "cl.razonSocial");
        CLIE_SERVER_SIDE_ORDER_BY.put("nombreCorto", "cl.nombreCorto");
        CLIE_SERVER_SIDE_ORDER_BY.put("periodoI", "periodoI");
        CLIE_SERVER_SIDE_ORDER_BY.put("periodoF", "periodoF");
        CLIE_SERVER_SIDE_ORDER_BY.put("taxReview", "cl.taxReview");
        CLIE_SERVER_SIDE_ORDER_BY.put("fEntregaTaxReview", "cl.fEntregaTaxReview");
        CLIE_SERVER_SIDE_ORDER_BY.put("ccbCuenta", "ccbCuenta");
        CLIE_SERVER_SIDE_ORDER_BY.put("ccbUsuario", "ccbUsuario");
        CLIE_SERVER_SIDE_ORDER_BY.put("ccbClave", "ccbClave");
        CLIE_SERVER_SIDE_ORDER_BY.put("periodoIActualizacion", "periodoIActualizacion");
        CLIE_SERVER_SIDE_ORDER_BY.put("periodoFActualizacion", "periodoFActualizacion");
    }

    private String getBaseSelect() {
        return """
            SELECT cl.idEstado, ce.descripcion AS descEstado, cl.idTipoServicio, cts.abreviatura AS abrServicio, cts.descripcion AS descServicio,
            cl.codigoCliente, cl.ruc, cl.y,
            cl.idContribuyente, ctc.descripcion AS descContribuyente, ctc.abreviatura AS abrContribuyente, cl.razonSocial, cl.nombreCorto,
            (SELECT TOP 1 CONCAT(stAnioDesde, '-', stMesDesde)periodoI FROM clienteServiciosTributarios y WHERE y.stIdServicioTributario = 4 AND y.idCliente = cl.ruc ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoI,
            (SELECT TOP 1 CONCAT(stAnioHasta, '-', stMesHasta)periodoF FROM clienteServiciosTributarios y WHERE y.stIdServicioTributario = 4 AND y.idCliente = cl.ruc ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoF,
            cl.taxReview, cl.fEntregaTaxReview,
            cl.solU, cl.solC, cl.upsU, cl.upsC, cl.soldierU, cl.soldierC,  cl.signerU, cl.signerC,
            (SELECT TOP 1 acInicioCom FROM clienteAltaCom x WHERE x.idCliente = cl.ruc ORDER BY acInicioCom DESC) as altaCom,
            (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio)periodoI FROM clienteAltaCom y WHERE y.idCliente = cl.ruc ORDER BY acAnioPeriodoInicio DESC, acMesPeriodoInicio DESC) AS periodoInicioCom,
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
        """;
    }

    @SuppressWarnings("unchecked")
    public List<Cliente> list() {
        Query query = em.createNativeQuery(getBaseSelect(), Tuple.class);
        return mapTuples((List<Tuple>) query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public List<Cliente> listServerSide(ClienteDataTablesRequest req) {
        StringBuilder sql = new StringBuilder(getBaseSelect());
        appendFilters(sql, req);
        sql.append(buildClienteServerSideOrderClause(req.getSortKey(), req.getSortDir()));
        sql.append(" OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);
        query.setParameter("offset", req.getStart());
        query.setParameter("limit", req.getLength());

        return mapTuples((List<Tuple>) query.getResultList());
    }

    public int countServerSide(ClienteDataTablesRequest req) {
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
        appendFilters(sql, req);
        Query query = em.createNativeQuery(sql.toString());
        setFilterParams(query, req);
        return ((Number) query.getSingleResult()).intValue();
    }

    public Map<Integer, Integer> getSummaryEstadosServerSide(ClienteDataTablesRequest req) {
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
        appendFilters(sql, req);
        sql.append(" GROUP BY cl.idEstado ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);
        
        Map<Integer, Integer> result = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Tuple> tuples = (List<Tuple>) query.getResultList();
        for (Tuple t : tuples) {
            Number idEstado = t.get("idEstado", Number.class);
            Number cantidad = t.get("cantidad", Number.class);
            if(idEstado != null) {
                result.put(idEstado.intValue(), cantidad.intValue());
            }
        }
        return result;
    }

    public Map<Integer, Integer> getSummaryCategoriasServerSide(ClienteDataTablesRequest req) {
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
        appendFilters(sql, req);
        sql.append(" GROUP BY s.idCategoria ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);
        
        Map<Integer, Integer> result = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Tuple> tuples = (List<Tuple>) query.getResultList();
        for (Tuple t : tuples) {
            Number idCategoria = t.get("idCategoria", Number.class);
            Number cantidad = t.get("cantidad", Number.class);
            if(idCategoria != null) {
                result.put(idCategoria.intValue(), cantidad.intValue());
            }
        }
        return result;
    }

    private void appendFilters(StringBuilder query, ClienteDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.append(" AND (cl.ruc LIKE :search OR cl.razonSocial LIKE :search OR cl.nombreCorto LIKE :search OR cl.codigoCliente LIKE :search) ");
        }
        appendParsedIntCsvFilter(query, "s.idCategoria", req.getCategoriasString(), "categorias");
        
        String cge = req.getCategoriaGrupoEString();
        if (cge != null && !cge.trim().isEmpty()) {
            query.append(" AND (CASE WHEN cl.idGrupoEconomico IS NOT NULL AND cl.idGrupoEconomico NOT IN (0, 1) THEN 1 ELSE 0 END) IN (:categoriaGrupoE) ");
        }
        
        String cs = req.getCategoriaStoreString();
        if (cs != null && !cs.trim().isEmpty()) {
            query.append(" AND (CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END) IN (:categoriaStore) ");
        }

        appendParsedIntCsvFilter(query, "cl.idGrupoEconomico", req.getGruposEconomicosString(), "gruposEconomicos");
        appendParsedIntCsvFilter(query, "cl.idEstado", req.getEstadosString(), "estados");
        
        String grupos = req.getGruposString();
        if (grupos != null && !grupos.trim().isEmpty()) {
            query.append(" AND cl.y IN (:grupos) ");
        }
        
        appendParsedIntCsvFilter(query, "cl.taxReview", req.getTaxReviewString(), "taxReview");
    }

    private void setFilterParams(Query query, ClienteDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.setParameter("search", "%" + req.getSearch().trim() + "%");
        }
        bindParsedIntCsv(query, req.getCategoriasString(), "categorias");
        
        String cge = req.getCategoriaGrupoEString();
        if (cge != null && !cge.trim().isEmpty()) {
            query.setParameter("categoriaGrupoE", parseCsvToIntList(cge));
        }
        String cs = req.getCategoriaStoreString();
        if (cs != null && !cs.trim().isEmpty()) {
            query.setParameter("categoriaStore", parseCsvToIntList(cs));
        }
        bindParsedIntCsv(query, req.getGruposEconomicosString(), "gruposEconomicos");
        bindParsedIntCsv(query, req.getEstadosString(), "estados");
        
        String grupos = req.getGruposString();
        if (grupos != null && !grupos.trim().isEmpty()) {
            query.setParameter("grupos", parseCsvToStringList(grupos));
        }
        bindParsedIntCsv(query, req.getTaxReviewString(), "taxReview");
    }

    private void appendParsedIntCsvFilter(StringBuilder query, String columnExpr, String csv, String paramName) {
        if (csv == null || csv.trim().isEmpty()) return;
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) continue;
            int v = Integer.parseInt(t);
            if (v == 0) includeZero = true; else nonZero.add(v);
        }
        if (!includeZero && nonZero.isEmpty()) return;

        query.append(" AND (");
        if (!nonZero.isEmpty()) {
            query.append(columnExpr).append(" IN (:").append(paramName).append(")");
            if (includeZero) query.append(" OR ");
        }
        if (includeZero) {
            query.append("(").append(columnExpr).append(" IS NULL OR ").append(columnExpr).append(" = 0)");
        }
        query.append(") ");
    }

    private void bindParsedIntCsv(Query query, String csv, String paramName) {
        if (csv == null || csv.trim().isEmpty()) return;
        List<Integer> nonZero = new ArrayList<>();
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) continue;
            int v = Integer.parseInt(t);
            if (v != 0) nonZero.add(v);
        }
        if (!nonZero.isEmpty()) {
            query.setParameter(paramName, nonZero);
        }
    }

    private List<Integer> parseCsvToIntList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toList());
    }

    private List<String> parseCsvToStringList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private String buildClienteServerSideOrderClause(String sortKey, String sortDir) {
        String direction = "desc".equalsIgnoreCase(sortDir != null ? sortDir.trim() : "") ? "DESC" : "ASC";
        if (sortKey == null || sortKey.trim().isEmpty()) {
            return " ORDER BY cl.ruc " + direction + " ";
        }
        String expr = CLIE_SERVER_SIDE_ORDER_BY.get(sortKey.trim());
        if (expr == null) {
            return " ORDER BY cl.ruc ASC ";
        }
        if ("cl.ruc".equalsIgnoreCase(expr.trim())) {
            return " ORDER BY cl.ruc " + direction + " ";
        }
        return " ORDER BY " + expr + " " + direction + ", cl.ruc ASC ";
    }

    private List<Cliente> mapTuples(List<Tuple> tuples) {
        List<Cliente> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Cliente obj = new Cliente();
            obj.setEstado(new Gclass(
                tuple.get("idEstado", Integer.class) == null ? 0 : tuple.get("idEstado", Integer.class),
                tuple.get("descEstado", String.class))
            );
            obj.setServicio(new Gclass(
                tuple.get("idTipoServicio", Integer.class) == null ? 0 : tuple.get("idTipoServicio", Integer.class),
                tuple.get("abrServicio", String.class),
                tuple.get("descServicio", String.class))
            );
            obj.setCodigoCliente(tuple.get("codigoCliente", String.class));
            obj.setRuc(tuple.get("ruc", String.class));
            
            Character yChar = tuple.get("y", Character.class);
            obj.setY(yChar != null ? yChar.toString() : null);
            
            obj.setContribuyente(new Gclass(
                tuple.get("idContribuyente", Integer.class) == null ? 0 : tuple.get("idContribuyente", Integer.class),
                tuple.get("abrContribuyente", String.class),
                tuple.get("descContribuyente", String.class))
            );
            obj.setRazonSocial(tuple.get("razonSocial", String.class));
            obj.setNombreCorto(tuple.get("nombreCorto", String.class));
            obj.setPeriodoInicioCom(tuple.get("periodoInicioCom", String.class));
            obj.setPeriodoI621(tuple.get("periodoI", String.class));
            obj.setPeriodoF621(tuple.get("periodoF", String.class));
            obj.setTaxReview(tuple.get("taxReview", Integer.class) == null ? 0 : tuple.get("taxReview", Integer.class));
            obj.setfEntregaTaxReview(tuple.get("fEntregaTaxReview", String.class));
            obj.setSolU(tuple.get("solU", String.class));
            obj.setSolC(tuple.get("solC", String.class));
            obj.setUpsU(tuple.get("upsU", String.class));
            obj.setUpsC(tuple.get("upsC", String.class));
            obj.setSoldierU(tuple.get("soldierU", String.class));
            obj.setSoldierC(tuple.get("soldierC", String.class));
            obj.setSignerU(tuple.get("signerU", String.class));
            obj.setSignerC(tuple.get("signerC", String.class));
            obj.setfInscripcion(tuple.get("fInscripcion", String.class));
            obj.setAltaCom(tuple.get("altaCom", String.class));
            obj.setfRetiro(tuple.get("fRetiro", String.class));
            obj.setCcbCuenta(tuple.get("ccbCuenta", String.class));
            obj.setCcbUsuario(tuple.get("ccbUsuario", String.class));
            obj.setCcbClave(tuple.get("ccbClave", String.class));
            obj.setPeriodoIActualizacion(tuple.get("periodoIActualizacion", String.class));
            obj.setPeriodoFActualizacion(tuple.get("periodoFActualizacion", String.class));

            SignerNivel sn = new SignerNivel();
            sn.setCategoria(new Gclass(
                tuple.get("idCategoria", Integer.class) == null ? 0 : tuple.get("idCategoria", Integer.class),
                tuple.get("abrCategoria", String.class),
                tuple.get("descCategoria", String.class))
            );
            obj.setSignerNivel(sn);

            obj.setGrupoEconomico(new Gclass(
                tuple.get("idGrupoEconomico", Integer.class) == null ? 0 : tuple.get("idGrupoEconomico", Integer.class),
                tuple.get("descGrupoEconomico", String.class))
            );
            list.add(obj);
        }
        return list;
    }
}
