package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.dto.SignerRusRequest;
import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.entities.SignerNivel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class SignerRusRepository {

    @PersistenceContext
    private EntityManager em;

    // ─── Base SELECT ─────────────────────────────────────────────────────────────

    private String getBaseSelect() {
        return """
                    SELECT
                        cl.idEstado, ce.descripcion AS estado,
                        cl.y,
                        cl.ruc,
                        cl.razonSocial,
                        s.idNivelF, sf.abreviatura AS abrNivelF, sf.descripcion AS descNivelF,
                        s.idNivelX3, st.abreviatura AS abrNivelX3, st.descripcion AS descNivelX3,
                        cl.idGrupoEconomico, ge.descripcion AS descGrupoEconomico,
                        cl.idTipoServicio, cts.abreviatura AS abrServicio, cts.descripcion AS descServicio,
                        cl.rTMypeTributario, cl.rTRus, cl.rTEspecial, cl.rTGeneral, cl.rTAmazonico, cl.rTAgrario,
                        cl.rT1ra, cl.rT2da, cl.rT3ra, cl.rT4ta, cl.rT5ta,
                        (SELECT TOP 1 CONCAT(stAnioDesde, '-', stMesDesde) FROM clienteServiciosTributarios y
                            WHERE y.stIdServicioTributario = 17 AND y.idCliente = cl.ruc
                            ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoIEnvoyRus,
                        (SELECT TOP 1 CONCAT(stAnioHasta, '-', stMesHasta) FROM clienteServiciosTributarios y
                            WHERE y.stIdServicioTributario = 17 AND y.idCliente = cl.ruc
                            ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoFEnvoyRus
                    FROM cliente cl
                        INNER JOIN clientsEstados ce ON cl.idEstado = ce.id
                        LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                        LEFT JOIN signerNivelesFijos sf ON s.idNivelF = sf.id
                        LEFT JOIN signerNivelesTemperatura st ON s.idNivelX3 = st.id
                        LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                        LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                    WHERE cl.rtRus = 1
                """;
    }

    // ─── Listado sin paginación (uso original) ────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Cliente> list() {
        Query query = em.createNativeQuery(getBaseSelect(), Tuple.class);
        return mapTuples((List<Tuple>) query.getResultList());
    }

    // ─── Server-side paginado ─────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Cliente> listServerSide(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder(getBaseSelect());
        appendFilters(sql, req);
        sql.append(buildOrderClause(req.getSortKey(), req.getSortDir()));
        sql.append(" OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        setFilterParams(query, req);
        query.setParameter("offset", req.getStart());
        query.setParameter("limit", req.getLength());

        return mapTuples((List<Tuple>) query.getResultList());
    }

    public int countServerSide(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder("""
                    SELECT COUNT(*) FROM cliente cl
                        INNER JOIN clientsEstados ce ON cl.idEstado = ce.id
                        LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                        LEFT JOIN signerNivelesFijos sf ON s.idNivelF = sf.id
                        LEFT JOIN signerNivelesTemperatura st ON s.idNivelX3 = st.id
                        LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                        LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                    WHERE cl.rtRus = 1
                """);
        appendFilters(sql, req);
        Query query = em.createNativeQuery(sql.toString());
        setFilterParams(query, req);
        return ((Number) query.getSingleResult()).intValue();
    }

    public Map<Integer, Integer> getSummaryEstados(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder("""
                    SELECT cl.idEstado, COUNT(*) AS cantidad FROM cliente cl
                        INNER JOIN clientsEstados ce ON cl.idEstado = ce.id
                        LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                        LEFT JOIN signerNivelesFijos sf ON s.idNivelF = sf.id
                        LEFT JOIN signerNivelesTemperatura st ON s.idNivelX3 = st.id
                        LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                        LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                    WHERE cl.rtRus = 1
                """);
        appendFilters(sql, req);
        sql.append(" GROUP BY cl.idEstado");

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

    // ─── Filtros dinámicos ────────────────────────────────────────────────────────

    private void appendFilters(StringBuilder sql, SignerRusRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            sql.append(" AND (cl.ruc LIKE :search OR cl.razonSocial LIKE :search OR ge.descripcion LIKE :search)");
        }
        appendIntCsvFilter(sql, "s.idNivelF", req.getNivelesFijosString(), "nivelesFijos");
        appendIntCsvFilter(sql, "s.idNivelX3", req.getNivelesX3String(), "nivelesX3");
        appendIntCsvFilter(sql, "cl.idGrupoEconomico", req.getGruposEconomicosString(), "gruposEconomicos");
        appendIntCsvFilter(sql, "cl.idEstado", req.getEstadosString(), "estados");

        String grupos = req.getGruposString();
        if (grupos != null && !grupos.trim().isEmpty()) {
            sql.append(" AND cl.y IN (:grupos)");
        }
    }

    private void setFilterParams(Query query, SignerRusRequest req) {
        if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
            query.setParameter("search", "%" + req.getSearch().trim() + "%");
        }
        bindIntCsv(query, req.getNivelesFijosString(), "nivelesFijos");
        bindIntCsv(query, req.getNivelesX3String(), "nivelesX3");
        bindIntCsv(query, req.getGruposEconomicosString(), "gruposEconomicos");
        bindIntCsv(query, req.getEstadosString(), "estados");

        String grupos = req.getGruposString();
        if (grupos != null && !grupos.trim().isEmpty()) {
            query.setParameter("grupos", parseCsvToStringList(grupos));
        }
    }

    /**
     * Agrega un filtro IN para una columna a partir de un CSV de enteros.
     * Si el CSV incluye 0 también filtra NULL o 0 (para IDs opcionales).
     */
    private void appendIntCsvFilter(StringBuilder sql, String col, String csv, String param) {
        if (csv == null || csv.trim().isEmpty()) return;
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) continue;
            int v = Integer.parseInt(t);
            if (v == 0) includeZero = true;
            else nonZero.add(v);
        }
        if (!includeZero && nonZero.isEmpty()) return;

        sql.append(" AND (");
        if (!nonZero.isEmpty()) {
            sql.append(col).append(" IN (:").append(param).append(")");
            if (includeZero) sql.append(" OR ");
        }
        if (includeZero) {
            sql.append("(").append(col).append(" IS NULL OR ").append(col).append(" = 0)");
        }
        sql.append(")");
    }

    private void bindIntCsv(Query query, String csv, String param) {
        if (csv == null || csv.trim().isEmpty()) return;
        List<Integer> nonZero = new ArrayList<>();
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) continue;
            int v = Integer.parseInt(t);
            if (v != 0) nonZero.add(v);
        }
        if (!nonZero.isEmpty()) {
            query.setParameter(param, nonZero);
        }
    }

    // ─── Ordenamiento ─────────────────────────────────────────────────────────────

    private static final Map<String, String> ORDER_BY_MAP = new HashMap<>();
    static {
        ORDER_BY_MAP.put("abrNivelFijo",     "sf.abreviatura");
        ORDER_BY_MAP.put("abrNivelX3",       "st.abreviatura");
        ORDER_BY_MAP.put("descGrupoEconomico","ge.descripcion");
        ORDER_BY_MAP.put("estado",           "ce.descripcion");
        ORDER_BY_MAP.put("servicio",         "cts.abreviatura");
        ORDER_BY_MAP.put("ruc",              "cl.ruc");
        ORDER_BY_MAP.put("y",                "cl.y");
        ORDER_BY_MAP.put("razonSocial",      "cl.razonSocial");
        ORDER_BY_MAP.put("periodoIEnvoyRus", "periodoIEnvoyRus");
        ORDER_BY_MAP.put("periodoFEnvoyRus", "periodoFEnvoyRus");
    }

    private String buildOrderClause(String sortKey, String sortDir) {
        String dir = "desc".equalsIgnoreCase(sortDir != null ? sortDir.trim() : "") ? "DESC" : "ASC";
        if (sortKey == null || sortKey.trim().isEmpty()) {
            return " ORDER BY cl.ruc " + dir;
        }
        String expr = ORDER_BY_MAP.get(sortKey.trim());
        if (expr == null) return " ORDER BY cl.ruc ASC";
        if ("cl.ruc".equals(expr)) return " ORDER BY cl.ruc " + dir;
        return " ORDER BY " + expr + " " + dir + ", cl.ruc ASC";
    }

    // ─── Mapeo Tuple → Cliente ────────────────────────────────────────────────────

    private List<Cliente> mapTuples(List<Tuple> tuples) {
        List<Cliente> list = new ArrayList<>();
        for (Tuple t : tuples) {
            Cliente obj = new Cliente();

            obj.setEstado(new Gclass(
                    t.get("idEstado", Integer.class) == null ? 0 : t.get("idEstado", Integer.class),
                    t.get("estado", String.class)));

            obj.setRuc(t.get("ruc", String.class));

            Character yChar = t.get("y", Character.class);
            obj.setY(yChar != null ? yChar.toString() : null);

            obj.setRazonSocial(t.get("razonSocial", String.class));

            obj.setServicio(new Gclass(
                    t.get("idTipoServicio", Integer.class) == null ? 0 : t.get("idTipoServicio", Integer.class),
                    t.get("abrServicio", String.class),
                    t.get("descServicio", String.class)));

            SignerNivel sn = new SignerNivel();
            sn.setNivelFijo(new Gclass(
                    t.get("idNivelF", Integer.class) == null ? 0 : t.get("idNivelF", Integer.class),
                    t.get("abrNivelF", String.class),
                    t.get("descNivelF", String.class)));
            sn.setNivelX3(new Gclass(
                    t.get("idNivelX3", Integer.class) == null ? 0 : t.get("idNivelX3", Integer.class),
                    t.get("abrNivelX3", String.class),
                    t.get("descNivelX3", String.class)));
            obj.setSignerNivel(sn);

            obj.setGrupoEconomico(new Gclass(
                    t.get("idGrupoEconomico", Integer.class) == null ? 0 : t.get("idGrupoEconomico", Integer.class),
                    t.get("descGrupoEconomico", String.class)));

            obj.setRegimenTributario(buildRegimenTributario(t));
            obj.setPeriodoIEnvoyRus(t.get("periodoIEnvoyRus", String.class));
            obj.setPeriodoFEnvoyRus(t.get("periodoFEnvoyRus", String.class));

            list.add(obj);
        }
        return list;
    }

    private String buildRegimenTributario(Tuple t) {
        List<String> rTs = new ArrayList<>();
        int rT3ra      = nvl(t, "rT3ra");
        int rT1ra      = nvl(t, "rT1ra");
        int rT2da      = nvl(t, "rT2da");
        int rT4ta      = nvl(t, "rT4ta");
        int rT5ta      = nvl(t, "rT5ta");

        if (rT3ra != 0) {
            if (nvl(t, "rTMypeTributario") != 0) rTs.add("MYPE Tributario");
            if (nvl(t, "rTRus")            != 0) rTs.add("RUS");
            if (nvl(t, "rTEspecial")       != 0) rTs.add("Especial");
            if (nvl(t, "rTGeneral")        != 0) rTs.add("General");
            if (nvl(t, "rTAmazonico")      != 0) rTs.add("Amazónico");
            if (nvl(t, "rTAgrario")        != 0) rTs.add("Agrario");
        } else if (rT1ra != 0) { rTs.add("1ra");
        } else if (rT2da != 0) { rTs.add("2da");
        } else if (rT4ta != 0) { rTs.add("4ta");
        } else if (rT5ta != 0) { rTs.add("5ta"); }

        return String.join(", ", rTs);
    }

    private int nvl(Tuple t, String col) {
        Integer v = t.get(col, Integer.class);
        return v == null ? 0 : v;
    }

    private List<String> parseCsvToStringList(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
