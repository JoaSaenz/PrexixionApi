package com.joa.prexixionapi.modules.fraccionamiento.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.common.utils.QueryUtils;
import com.joa.prexixionapi.modules.fraccionamiento.dto.FraccionamientoFilterRequestDTO;
import com.joa.prexixionapi.modules.fraccionamiento.dto.FraccionamientoListDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FraccionamientoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<FraccionamientoListDTO> list(FraccionamientoFilterRequestDTO req) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                f.idCliente, 
                c.y, 
                c.razonSocial, 
                c.idEstado AS idEstadoCliente, 
                ce.descripcion AS descEstadoCliente, 
                f.nroSolicitud, 
                f.fechaSolicitud,
                CASE WHEN f.mesesDeAplazamiento > 0 	 
                     THEN
                         CASE WHEN COALESCE(cu.pagadas, 0) = 0
                              THEN f.nroCuotas + 1
                              ELSE (f.nroCuotas + 1) - (COALESCE(cu.pagadas, 0) - 1)
                         END
                     ELSE 
                         CASE WHEN COALESCE(cu.fVencimientoIA, '') = '' 		   
                              THEN COALESCE(cu.pendientes, 0) - 1 		   
                              ELSE COALESCE(cu.pendientes, 0)            
                         END
                END AS pendientes,
                CASE WHEN f.mesesDeAplazamiento > 0 
                     THEN f.nroCuotas + 1 
                     ELSE 
                         CASE WHEN COALESCE(cu.fVencimientoIA, '') = '' 
                              THEN f.nroCuotas
                              ELSE f.nroCuotas + 1
                         END
                END AS nroCuotasTotal,
                f.mesesDeAplazamiento, 
                f.nroResolucion, 
                f.fechaNotificacion, 
                f.idEstadoSolicitud, 
                eS.descripcion AS estadoSolicitud, 
                f.idEstadoResolucion, 
                eR.descripcion AS estadoResolucion, 
                f.codigoFraccionamiento, 
                cu.fVencimientoPrimeraCuota
            FROM fraccionamientos f
            LEFT JOIN (
                SELECT 
                    nroSolicitud,
                    SUM(CASE WHEN idEstadoPago = 2 THEN 1 ELSE 0 END) AS pagadas,
                    SUM(CASE WHEN idEstadoPago = 1 THEN 1 ELSE 0 END) AS pendientes,
                    MAX(CASE WHEN nroCuota = 'IA' THEN fVencimiento ELSE '' END) AS fVencimientoIA,
                    MIN(CASE WHEN fVencimiento IS NOT NULL AND fVencimiento != '' THEN fVencimiento ELSE NULL END) AS fVencimientoPrimeraCuota
                FROM cuotasNew
                GROUP BY nroSolicitud
            ) cu ON f.nroSolicitud = cu.nroSolicitud
            LEFT JOIN fraccionamientosEstadosSolicitudes eS ON f.idEstadoSolicitud = eS.id
            LEFT JOIN fraccionamientosEstadosResoluciones eR ON f.idEstadoResolucion = eR.id
            LEFT JOIN cliente c ON f.idCliente = c.ruc
            LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
            WHERE 1 = 1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (req != null) {
            QueryUtils.appendIntFilter(sql, params, "c.idEstado", req.getEstadosClientes(), "estadosClientes");
            QueryUtils.appendStringFilter(sql, params, "c.y", req.getGrupos(), "grupos");
            QueryUtils.appendIntFilter(sql, params, "f.idEstadoSolicitud", req.getEstadosSolicitudes(), "estadosSolicitudes");
            QueryUtils.appendIntFilter(sql, params, "f.idEstadoResolucion", req.getEstadosResoluciones(), "estadosResoluciones");

            if (req.getClientes() != null && !req.getClientes().isEmpty()) {
                QueryUtils.appendStringFilter(sql, params, "f.idCliente", req.getClientes(), "clientes");
            }

            if (req.getSearch() != null && !req.getSearch().trim().isEmpty()) {
                String searchPattern = "%" + req.getSearch().trim() + "%";
                sql.append(" AND (f.idCliente LIKE :search OR c.razonSocial LIKE :search OR f.nroSolicitud LIKE :search OR f.nroResolucion LIKE :search) ");
                params.addValue("search", searchPattern);
            }
        }

        sql.append(" ORDER BY f.fechaSolicitud ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapRow(rs));
    }

    private FraccionamientoListDTO mapRow(ResultSet rs) throws SQLException {
        int pendientes = rs.getInt("pendientes");
        int nroCuotasTotal = rs.getInt("nroCuotasTotal");
        String cuotasProgreso = pendientes + "/" + nroCuotasTotal;

        return FraccionamientoListDTO.builder()
                .idCliente(rs.getString("idCliente"))
                .y(rs.getString("y"))
                .razonSocial(rs.getString("razonSocial"))
                .idEstadoCliente(rs.getObject("idEstadoCliente") != null ? rs.getInt("idEstadoCliente") : null)
                .descEstadoCliente(rs.getString("descEstadoCliente") != null ? rs.getString("descEstadoCliente") : "")
                .nroSolicitud(rs.getString("nroSolicitud"))
                .fechaSolicitud(rs.getString("fechaSolicitud"))
                .cuotasProgreso(cuotasProgreso)
                .nroCuotas(nroCuotasTotal)
                .mesesDeAplazamiento(rs.getInt("mesesDeAplazamiento"))
                .nroResolucion(rs.getString("nroResolucion") != null ? rs.getString("nroResolucion") : "")
                .fechaNotificacion(rs.getString("fechaNotificacion") != null ? rs.getString("fechaNotificacion") : "")
                .idEstadoSolicitud(rs.getObject("idEstadoSolicitud") != null ? rs.getInt("idEstadoSolicitud") : null)
                .estadoSolicitud(rs.getString("estadoSolicitud") != null ? rs.getString("estadoSolicitud") : "")
                .idEstadoResolucion(rs.getObject("idEstadoResolucion") != null ? rs.getInt("idEstadoResolucion") : null)
                .estadoResolucion(rs.getString("estadoResolucion") != null ? rs.getString("estadoResolucion") : "")
                .codigoFraccionamiento(rs.getString("codigoFraccionamiento") != null ? rs.getString("codigoFraccionamiento") : "")
                .fVencimientoPrimeraCuota(rs.getString("fVencimientoPrimeraCuota") != null ? rs.getString("fVencimientoPrimeraCuota") : "")
                .build();
    }
}
