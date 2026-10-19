package com.joa.prexixionapi.modules.rus.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.joa.prexixionapi.common.utils.QueryUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.modules.rus.dto.SignerRusRequest;
import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.dto.SignerNivelDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.rus.dto.SignerRusListDTO;

@Repository
public class SignerRusRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<SignerRusListDTO> list(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder("""
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
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" ORDER BY cl.ruc ASC");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            SignerRusListDTO obj = new SignerRusListDTO();
            obj.setEstado(new GclassDTO(rs.getInt("idEstado"), rs.getString("estado")));
            obj.setRuc(rs.getString("ruc"));
            obj.setY(rs.getString("y"));
            obj.setRazonSocial(rs.getString("razonSocial"));
            obj.setServicio(
                    new GclassDTO(rs.getInt("idTipoServicio"), rs.getString("abrServicio"),
                            rs.getString("descServicio")));

            SignerNivelDTO sn = new SignerNivelDTO();
            sn.setNivelFijo(
                    new GclassDTO(rs.getInt("idNivelF"), rs.getString("abrNivelF"), rs.getString("descNivelF")));
            sn.setNivelX3(
                    new GclassDTO(rs.getInt("idNivelX3"), rs.getString("abrNivelX3"), rs.getString("descNivelX3")));
            obj.setSignerNivel(sn);
            obj.setGrupoEconomico(new GclassDTO(rs.getInt("idGrupoEconomico"), rs.getString("descGrupoEconomico")));
            obj.setRegimenTributario(buildRegimenTributario(rs));
            obj.setPeriodoIEnvoyRus(rs.getString("periodoIEnvoyRus"));
            obj.setPeriodoFEnvoyRus(rs.getString("periodoFEnvoyRus"));
            return obj;
        });
    }

    public List<IndicadorDTO> getSummaryEstados(SignerRusRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT ce.descripcion AS texto, COUNT(*) AS cantidad
                FROM cliente cl
                INNER JOIN clientsEstados ce ON cl.idEstado = ce.id
                LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                LEFT JOIN signerNivelesFijos sf ON s.idNivelF = sf.id
                LEFT JOIN signerNivelesTemperatura st ON s.idNivelX3 = st.id
                LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                WHERE cl.rtRus = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, req);
        sql.append(" GROUP BY ce.descripcion");

        return jdbcTemplate.query(sql.toString(), params,
                (rs, rowNum) -> new IndicadorDTO(rs.getString("texto"), rs.getInt("cantidad")));
    }

    private void appendFilters(StringBuilder sql, MapSqlParameterSource params, SignerRusRequest req) {
        QueryUtils.appendIntFilter(sql, params, "s.idNivelF", req.getNivelFijoFiltro(), "nivelesFijos");
        QueryUtils.appendIntFilter(sql, params, "s.idNivelX3", req.getNivelX3Filtro(), "nivelesX3");
        QueryUtils.appendIntFilter(sql, params, "cl.idGrupoEconomico", req.getGrupoEconomicoFiltro(),
                "gruposEconomicos");
        QueryUtils.appendIntFilter(sql, params, "cl.idEstado", req.getEstadoFiltro(), "estados");
        QueryUtils.appendStringFilter(sql, params, "cl.y", req.getGrupoFiltro(), "grupos");
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
