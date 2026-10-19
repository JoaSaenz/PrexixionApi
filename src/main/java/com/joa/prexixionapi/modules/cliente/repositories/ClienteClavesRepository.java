package com.joa.prexixionapi.modules.cliente.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.joa.prexixionapi.modules.cliente.repositories.ClienteClavesRepository;
import com.joa.prexixionapi.modules.cliente.dto.ClienteClavesDTO;

@Repository
@RequiredArgsConstructor
public class ClienteClavesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ClienteClavesDTO> getClavesData(List<Integer> estados, List<Integer> grupos) {
        String sql = """
            SELECT
                ce.descripcion AS descEstado,
                cl.ruc, cl.y,
                ctc.abreviatura AS abrContribuyente,
                cl.razonSocial,
                cl.solU, cl.solC,
                cl.upsU, cl.upsC,
                cl.soldierU, cl.soldierC,
                cl.signerU, cl.signerC,
                cl.afpU, cl.afpC,
                cl.sisU, cl.sisC,
                nc.abreviatura AS abrCategoria,
                CASE
                    WHEN cl.idGrupoEconomico IS NOT NULL AND cl.idGrupoEconomico NOT IN (0, 1) THEN 1
                    ELSE 0
                END AS categoriaGrupoE,
                CASE
                    WHEN cso.idCliente IS NOT NULL THEN 1
                    ELSE 0
                END AS categoriaStore
            FROM cliente cl
            LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id
            LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id
            LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
            LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id
            LEFT JOIN (
                SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6
            ) cso ON cl.ruc = cso.idCliente
            WHERE ce.id IN (:estados) AND cl.y IN (:grupos)
            ORDER BY cl.y, ce.descripcion, cl.razonSocial
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("estados", estados)
                .addValue("grupos", grupos);

        return jdbcTemplate.query(sql, params, this::mapRow);
    }

    private ClienteClavesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ClienteClavesDTO.builder()
                .descEstado(rs.getString("descEstado"))
                .ruc(rs.getString("ruc"))
                .y(rs.getString("y"))
                .abrContribuyente(rs.getString("abrContribuyente"))
                .razonSocial(rs.getString("razonSocial"))
                .solU(rs.getString("solU"))
                .solC(rs.getString("solC"))
                .upsU(rs.getString("upsU"))
                .upsC(rs.getString("upsC"))
                .soldierU(rs.getString("soldierU"))
                .soldierC(rs.getString("soldierC"))
                .signerU(rs.getString("signerU"))
                .signerC(rs.getString("signerC"))
                .afpU(rs.getString("afpU"))
                .afpC(rs.getString("afpC"))
                .sisU(rs.getString("sisU"))
                .sisC(rs.getString("sisC"))
                .abrCategoria(rs.getString("abrCategoria"))
                .categoriaGrupoE(rs.getInt("categoriaGrupoE"))
                .categoriaStore(rs.getInt("categoriaStore"))
                .build();
    }
}
