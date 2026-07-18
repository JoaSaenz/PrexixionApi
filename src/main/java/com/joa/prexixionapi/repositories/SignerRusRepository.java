package com.joa.prexixionapi.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.dto.SignerRusRequest;
import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.entities.SignerNivel;

@Repository
public class SignerRusRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

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
                        (SELECT TOP 1 CONCAT(anioDesde, '-', mesDesde) FROM fitRus y
                            WHERE y.idServicio = 1 AND y.idCliente = cl.ruc
                            ORDER BY anioDesde DESC, mesDesde DESC) AS periodoIEnvoyRus,
                        (SELECT TOP 1 CONCAT(anioHasta, '-', mesHasta) FROM fitRus y
                            WHERE y.idServicio = 1 AND y.idCliente = cl.ruc
                            ORDER BY anioDesde DESC, mesDesde DESC) AS periodoFEnvoyRus
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

    public List<Cliente> list(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder(getBaseSelect());
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" ORDER BY cl.ruc ASC");

        return jdbcTemplate.query(sql.toString(), params, new ClienteRowMapper());
    }

    public Map<Integer, Integer> getSummaryEstados(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder(
                "SELECT cl.idEstado, COUNT(*) AS cantidad FROM cliente cl " +
                        "INNER JOIN clientsEstados ce ON cl.idEstado = ce.id " +
                        "LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente " +
                        "LEFT JOIN signerNivelesFijos sf ON s.idNivelF = sf.id " +
                        "LEFT JOIN signerNivelesTemperatura st ON s.idNivelX3 = st.id " +
                        "LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id " +
                        "LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id " +
                        "WHERE cl.rtRus = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" GROUP BY cl.idEstado");

        Map<Integer, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql.toString(), params, rs -> {
            result.put(rs.getInt("idEstado"), rs.getInt("cantidad"));
        });
        return result;
    }

    private void appendFilters(StringBuilder sql, MapSqlParameterSource params, SignerRusRequest req) {
        appendIntCsvFilter(sql, params, "s.idNivelF", req.getNivelesFijosString(), "nivelesFijos");
        appendIntCsvFilter(sql, params, "s.idNivelX3", req.getNivelesX3String(), "nivelesX3");
        appendIntCsvFilter(sql, params, "cl.idGrupoEconomico", req.getGruposEconomicosString(), "gruposEconomicos");
        appendIntCsvFilter(sql, params, "cl.idEstado", req.getEstadosString(), "estados");

        String grupos = req.getGruposString();
        if (grupos != null && !grupos.trim().isEmpty()) {
            List<String> list = Arrays.stream(grupos.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                sql.append(" AND cl.y IN (:grupos)");
                params.addValue("grupos", list);
            }
        }
    }

    private void appendIntCsvFilter(StringBuilder sql, MapSqlParameterSource params, String col, String csv,
            String param) {
        if (csv == null || csv.trim().isEmpty())
            return;
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty())
                continue;
            int v = Integer.parseInt(t);
            if (v == 0)
                includeZero = true;
            else
                nonZero.add(v);
        }
        if (!includeZero && nonZero.isEmpty())
            return;

        sql.append(" AND (");
        if (!nonZero.isEmpty()) {
            sql.append(col).append(" IN (:").append(param).append(")");
            params.addValue(param, nonZero);
            if (includeZero)
                sql.append(" OR ");
        }
        if (includeZero) {
            sql.append("(").append(col).append(" IS NULL OR ").append(col).append(" = 0)");
        }
        sql.append(")");
    }

    private class ClienteRowMapper implements RowMapper<Cliente> {
        @Override
        public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
            Cliente obj = new Cliente();
            obj.setEstado(new Gclass(rs.getInt("idEstado"), rs.getString("estado")));
            obj.setRuc(rs.getString("ruc"));
            String y = rs.getString("y");
            obj.setY(y);
            obj.setRazonSocial(rs.getString("razonSocial"));
            obj.setServicio(
                    new Gclass(rs.getInt("idTipoServicio"), rs.getString("abrServicio"), rs.getString("descServicio")));

            SignerNivel sn = new SignerNivel();
            sn.setNivelFijo(new Gclass(rs.getInt("idNivelF"), rs.getString("abrNivelF"), rs.getString("descNivelF")));
            sn.setNivelX3(new Gclass(rs.getInt("idNivelX3"), rs.getString("abrNivelX3"), rs.getString("descNivelX3")));
            obj.setSignerNivel(sn);

            obj.setGrupoEconomico(new Gclass(rs.getInt("idGrupoEconomico"), rs.getString("descGrupoEconomico")));

            obj.setRegimenTributario(buildRegimenTributario(rs));
            obj.setPeriodoIEnvoyRus(rs.getString("periodoIEnvoyRus"));
            obj.setPeriodoFEnvoyRus(rs.getString("periodoFEnvoyRus"));
            return obj;
        }

        private String buildRegimenTributario(ResultSet rs) throws SQLException {
            List<String> rTs = new ArrayList<>();
            int rT3ra = rs.getInt("rT3ra");
            int rT1ra = rs.getInt("rT1ra");
            int rT2da = rs.getInt("rT2da");
            int rT4ta = rs.getInt("rT4ta");
            int rT5ta = rs.getInt("rT5ta");

            if (rT3ra != 0) {
                if (rs.getInt("rTMypeTributario") != 0)
                    rTs.add("MYPE Tributario");
                if (rs.getInt("rTRus") != 0)
                    rTs.add("RUS");
                if (rs.getInt("rTEspecial") != 0)
                    rTs.add("Especial");
                if (rs.getInt("rTGeneral") != 0)
                    rTs.add("General");
                if (rs.getInt("rTAmazonico") != 0)
                    rTs.add("Amazónico");
                if (rs.getInt("rTAgrario") != 0)
                    rTs.add("Agrario");
            } else if (rT1ra != 0) {
                rTs.add("1ra");
            } else if (rT2da != 0) {
                rTs.add("2da");
            } else if (rT4ta != 0) {
                rTs.add("4ta");
            } else if (rT5ta != 0) {
                rTs.add("5ta");
            }
            return String.join(", ", rTs);
        }
    }
}
