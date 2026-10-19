package com.joa.prexixionapi.modules.cliente.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.joa.prexixionapi.modules.cliente.repositories.ClienteCuentaBancariaRepository;
import com.joa.prexixionapi.modules.cliente.dto.ClienteCuentaBancariaDTO;

@Repository
@RequiredArgsConstructor
public class ClienteCuentaBancariaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ClienteCuentaBancariaDTO> getCuentaBancariaData(List<Integer> estados, List<Integer> grupos) {
        String sql = """
            SELECT
                ce.descripcion AS descEstado,
                (SELECT SUBSTRING(
                    (SELECT ',' + acInicioCom AS 'data()'
                     FROM clienteAltaCom ac
                     WHERE ac.idCliente = ccb.idCliente
                     FOR XML PATH('')), 2, 9999)
                ) AS acInicioCom,
                ccb.idCliente,
                c.y,
                ctc.abreviatura AS abrContribuyente,
                c.razonSocial,
                ccb.ccbCCI
            FROM clienteCuentasBancarias ccb
            LEFT JOIN cliente c ON ccb.idCliente = c.ruc
            LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
            LEFT JOIN clientesTiposContribuyente ctc ON c.idContribuyente = ctc.id
            WHERE ce.id IN (:estados) AND c.y IN (:grupos)
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("estados", estados)
                .addValue("grupos", grupos);

        return jdbcTemplate.query(sql, params, this::mapRow);
    }

    private ClienteCuentaBancariaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ClienteCuentaBancariaDTO.builder()
                .descEstado(rs.getString("descEstado"))
                .acInicioCom(rs.getString("acInicioCom"))
                .idCliente(rs.getString("idCliente"))
                .y(rs.getString("y"))
                .abrContribuyente(rs.getString("abrContribuyente"))
                .razonSocial(rs.getString("razonSocial"))
                .ccbCCI(rs.getString("ccbCCI"))
                .build();
    }
}
