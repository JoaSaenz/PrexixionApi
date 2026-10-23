package com.joa.prexixionapi.modules.gestionSide.repositories;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import com.joa.prexixionapi.common.utils.QueryUtils;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideListDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideRequest;

@Repository
@RequiredArgsConstructor
public class GestionSideRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<GestionSideListDTO> list(GestionSideRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT 
                    s.id,
                    c.idGrupoEconomico, ge.descripcion AS descGrupoEconomico,
                    c.idEstado AS idEstadoCliente, ce.descripcion AS descEstadoCliente,
                    s.idCliente, c.razonSocial, 
                    s.idEstado AS idEstado, se.descripcion AS descEstado,
                    s.idArea, a.descripcion AS descArea,
                    s.idServicio, ts.descripcion AS descServicio,
                    s.detalle, s.fechaRecibido, s.fechaEntrega, s.importe
                FROM gestionSide s
                LEFT JOIN cliente c ON s.idCliente = c.ruc
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id
                LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN gestionSideEstados se ON s.idEstado = se.id
                LEFT JOIN areas a ON s.idArea = a.id
                LEFT JOIN tradeClientesTiposServicios ts ON s.idServicio = ts.id
                WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (req != null) {
            QueryUtils.appendIntFilter(sql, params, "c.idGrupoEconomico", req.getGrupoEconomicoFiltro(), "gruposEconomicos");
            QueryUtils.appendIntFilter(sql, params, "c.idEstado", req.getEstadoClienteFiltro(), "estadosCliente");
            QueryUtils.appendIntFilter(sql, params, "s.idEstado", req.getEstadoModuloFiltro(), "estadosModulo");
            QueryUtils.appendIntFilter(sql, params, "s.idArea", req.getAreaFiltro(), "areas");
            QueryUtils.appendIntFilter(sql, params, "s.idServicio", req.getServicioFiltro(), "servicios");
        }

        sql.append(" ORDER BY s.id DESC ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> GestionSideListDTO.builder()
                .id(rs.getObject("id", Integer.class))
                .idGrupoEconomico(rs.getObject("idGrupoEconomico", Integer.class))
                .descGrupoEconomico(rs.getString("descGrupoEconomico"))
                .idEstadoCliente(rs.getObject("idEstadoCliente", Integer.class))
                .descEstadoCliente(rs.getString("descEstadoCliente"))
                .idCliente(rs.getString("idCliente"))
                .razonSocial(rs.getString("razonSocial"))
                .idEstado(rs.getObject("idEstado", Integer.class))
                .descEstado(rs.getString("descEstado"))
                .idArea(rs.getObject("idArea", Integer.class))
                .descArea(rs.getString("descArea"))
                .idServicio(rs.getObject("idServicio", Integer.class))
                .descServicio(rs.getString("descServicio"))
                .detalle(rs.getString("detalle"))
                .fechaRecibido(rs.getString("fechaRecibido"))
                .fechaEntrega(rs.getString("fechaEntrega"))
                .importe(rs.getBigDecimal("importe"))
                .build());
    }

    public GestionSideDTO getOne(int id) {
        String sql = """
                SELECT 
                    s.id,
                    c.idGrupoEconomico, ge.descripcion AS descGrupoEconomico,
                    c.idEstado AS idEstadoCliente, ce.descripcion AS descEstadoCliente,
                    s.idCliente, c.razonSocial, 
                    s.idEstado AS idEstado, se.descripcion AS descEstado,
                    s.idArea, a.descripcion AS descArea,
                    s.idServicio, ts.descripcion AS descServicio,
                    s.detalle, s.fechaRecibido, s.fechaEntrega, s.importe
                FROM gestionSide s
                LEFT JOIN cliente c ON s.idCliente = c.ruc
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id
                LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN gestionSideEstados se ON s.idEstado = se.id
                LEFT JOIN areas a ON s.idArea = a.id
                LEFT JOIN tradeClientesTiposServicios ts ON s.idServicio = ts.id
                WHERE s.id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<GestionSideDTO> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> GestionSideDTO.builder()
                .id(rs.getObject("id", Integer.class))
                .idCliente(rs.getString("idCliente"))
                .razonSocial(rs.getString("razonSocial"))
                .idEstado(rs.getObject("idEstado", Integer.class))
                .descEstado(rs.getString("descEstado"))
                .idArea(rs.getObject("idArea", Integer.class))
                .descArea(rs.getString("descArea"))
                .idServicio(rs.getObject("idServicio", Integer.class))
                .descServicio(rs.getString("descServicio"))
                .detalle(rs.getString("detalle"))
                .fechaRecibido(rs.getString("fechaRecibido"))
                .fechaEntrega(rs.getString("fechaEntrega"))
                .importe(rs.getBigDecimal("importe"))
                .build());

        return list.isEmpty() ? null : list.get(0);
    }

    public int insertUpdate(GestionSideDTO dto) {
        if (dto.getId() == null || dto.getId() == 0) {
            String insertSql = """
                    INSERT INTO gestionSide (idCliente, idEstado, idArea, idServicio, detalle, fechaRecibido, fechaEntrega, importe)
                    OUTPUT INSERTED.id
                    VALUES (:idCliente, :idEstado, :idArea, :idServicio, :detalle, :fechaRecibido, :fechaEntrega, :importe)
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("idCliente", dto.getIdCliente())
                    .addValue("idEstado", dto.getIdEstado())
                    .addValue("idArea", dto.getIdArea())
                    .addValue("idServicio", dto.getIdServicio())
                    .addValue("detalle", dto.getDetalle())
                    .addValue("fechaRecibido", dto.getFechaRecibido())
                    .addValue("fechaEntrega", dto.getFechaEntrega())
                    .addValue("importe", dto.getImporte());

            Integer idGenerado = jdbcTemplate.queryForObject(insertSql, params, Integer.class);
            dto.setId(idGenerado);

            // INSERTAR EN gestionNegocios
            String queryGestionNegocios = """
                    INSERT INTO gestionNegocios (idCliente, idModuloPadre, modulo, idEstado)
                    VALUES (:idCliente, :idModuloPadre, :modulo, :idEstado)
                    """;

            MapSqlParameterSource paramsNegocios = new MapSqlParameterSource()
                    .addValue("idCliente", dto.getIdCliente())
                    .addValue("idModuloPadre", idGenerado)
                    .addValue("modulo", "GESTION SIDE")
                    .addValue("idEstado", 1);

            jdbcTemplate.update(queryGestionNegocios, paramsNegocios);

            return 1;
        } else {
            String updateSql = """
                    UPDATE gestionSide SET 
                        idCliente = :idCliente, 
                        idEstado = :idEstado, 
                        idArea = :idArea, 
                        idServicio = :idServicio, 
                        detalle = :detalle, 
                        fechaRecibido = :fechaRecibido, 
                        fechaEntrega = :fechaEntrega, 
                        importe = :importe 
                    WHERE id = :id
                    """;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", dto.getId())
                    .addValue("idCliente", dto.getIdCliente())
                    .addValue("idEstado", dto.getIdEstado())
                    .addValue("idArea", dto.getIdArea())
                    .addValue("idServicio", dto.getIdServicio())
                    .addValue("detalle", dto.getDetalle())
                    .addValue("fechaRecibido", dto.getFechaRecibido())
                    .addValue("fechaEntrega", dto.getFechaEntrega())
                    .addValue("importe", dto.getImporte());

            jdbcTemplate.update(updateSql, params);

            // Actualizar idCliente en gestionNegocios por si fue modificado
            String updateNegociosSql = """
                    UPDATE gestionNegocios SET idCliente = :idCliente 
                    WHERE idModuloPadre = :id AND modulo = 'GESTION SIDE'
                    """;
            jdbcTemplate.update(updateNegociosSql, new MapSqlParameterSource()
                    .addValue("id", dto.getId())
                    .addValue("idCliente", dto.getIdCliente()));

            return 2;
        }
    }

    public void delete(int id) {
        String deleteNegociosSql = "DELETE FROM gestionNegocios WHERE idModuloPadre = :id AND modulo = 'GESTION SIDE'";
        jdbcTemplate.update(deleteNegociosSql, new MapSqlParameterSource("id", id));

        String sql = "DELETE FROM gestionSide WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }
}
