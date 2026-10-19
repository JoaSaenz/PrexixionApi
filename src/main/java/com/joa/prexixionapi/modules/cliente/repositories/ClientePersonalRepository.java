package com.joa.prexixionapi.modules.cliente.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.joa.prexixionapi.modules.cliente.dto.ClientePersonalDTO;
import com.joa.prexixionapi.modules.cliente.repositories.ClientePersonalRepository;

@Repository
@RequiredArgsConstructor
public class ClientePersonalRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ClientePersonalDTO> getPersonalData(List<Integer> estados, List<Integer> grupos) {
        String sql = """
            SELECT
                c.idGrupoEconomico,
                ge.descripcion AS descGrupoEconomico,
                c.idEstado,
                ce.descripcion AS descEstado,
                c.ruc, c.y, c.razonSocial,
                cp.plIdTipoCoordinacionContable,
                t.descripcion AS descTipoCC,
                cp.plDni, cp.plApellido, cp.plNombre,
                cp.plIdPuesto,
                p.descripcion AS descPuesto,
                cp.plTelefono, cp.plCorreo, cp.plFNacimiento,
                cp.plTieneClaveSol, cp.plAdministracion
            FROM cliente c
            LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id
            LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
            LEFT JOIN clientePersonal cp ON c.ruc = cp.idCliente
            LEFT JOIN tipoCoordinacionContable t ON cp.plIdTipoCoordinacionContable = t.id
            LEFT JOIN puestos p ON cp.plIdPuesto = p.id
            WHERE ce.id IN (:estados) AND c.y IN (:grupos)
            ORDER BY c.y
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("estados", estados)
                .addValue("grupos", grupos);

        return jdbcTemplate.query(sql, params, this::mapRow);
    }

    private ClientePersonalDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ClientePersonalDTO.builder()
                .idGrupoEconomico(rs.getInt("idGrupoEconomico"))
                .descGrupoEconomico(rs.getString("descGrupoEconomico"))
                .idEstado(rs.getInt("idEstado"))
                .descEstado(rs.getString("descEstado"))
                .ruc(rs.getString("ruc"))
                .y(rs.getString("y"))
                .razonSocial(rs.getString("razonSocial"))
                .descTipoCC(rs.getString("descTipoCC"))
                .plDni(rs.getString("plDni"))
                .plApellido(rs.getString("plApellido"))
                .plNombre(rs.getString("plNombre"))
                .descPuesto(rs.getString("descPuesto"))
                .plTelefono(rs.getString("plTelefono"))
                .plCorreo(rs.getString("plCorreo"))
                .plFNacimiento(rs.getString("plFNacimiento"))
                .plTieneClaveSol(rs.getString("plTieneClaveSol"))
                .plAdministracion(rs.getInt("plAdministracion"))
                .build();
    }
}
