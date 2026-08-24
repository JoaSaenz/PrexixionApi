package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.BiBecomActualizacionItemDTO;
import com.joa.prexixionapi.dto.BiBecomDevolucionItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BiBecomRepository {

    private final JdbcTemplate jdbcTemplate;

    public String findResponsableNombre() {
        String sql = "SELECT TOP 1 CONCAT(p.nombres, ' ', p.apellidos) AS responsable " +
                "FROM bty.dbo.personal p " +
                "WHERE p.idArea = 8 AND p.idPuesto = 3 AND p.idEstado = 2";
        try {
            String responsable = jdbcTemplate.queryForObject(sql, String.class);
            if (responsable != null && !responsable.trim().isEmpty()) {
                return responsable.trim();
            }
        } catch (Exception e) {
            log.warn(
                    "No se pudo obtener el responsable de BECOM vía SQL (idArea=8, idPuesto=3, idEstado=2). Usando valor por defecto.",
                    e);
        }
        return "Karen Arroyo Lescano";
    }

    public List<BiBecomDevolucionItemDTO> findAllDevolucionesByAnio(String anio) {
        String yearStr = (anio != null && !anio.trim().isEmpty() ? anio.trim() : "2026");
        String fechaInicio = yearStr + "-01-01";
        String fechaFin = yearStr + "-12-31";

        String sql = "SELECT " +
                "ISNULL(dc.descripcion, 'OTRO') AS procedencia, " +
                "CASE WHEN d.idClase = 2 THEN " +
                "  CASE WHEN ISNULL(d.aprobado, 0) > ISNULL(d.saldoActual, 0) THEN ISNULL(d.saldoActual, 0) ELSE ISNULL(d.aprobado, 0) END "
                +
                "ELSE ISNULL(d.aprobado, 0) END AS importe, " +
                "d.ruc, " +
                "CASE WHEN ISNULL(c.nombreCorto, '') <> '' THEN c.nombreCorto ELSE c.razonSocial END AS signer, " +
                "d.idEstadoCheque, dech.descripcion AS recoge, " +
                "d.idMedioPago, dmp.descripcion AS tipo, " +
                "d.idEstado, de.descripcion AS estado, " +
                "d.periodo, CONVERT(VARCHAR(10), d.fechaPres, 120) AS fechaSolicitud, " +
                "ISNULL(d.saldoMesAnterior, 0) AS montoSolicitado, " +
                "ISNULL(d.aprobado, 0) AS montoAprobado, " +
                "ISNULL(d.saldoActual, 0) AS saldoBn, " +
                "(ISNULL(d.saldoMesAnterior, 0) - ISNULL(d.aprobado, 0)) AS diferencia, " +
                "d.idResultado, dr.descripcion AS resultado " +
                "FROM Devoluciones d " +
                "LEFT JOIN Cliente c ON d.ruc = c.ruc " +
                "LEFT JOIN DevolucionesClase dc ON d.idClase = dc.id " +
                "LEFT JOIN DevolucionesEstadoCheque dech ON d.idEstadoCheque = dech.id " +
                "LEFT JOIN DevolucionesMedioPago dmp ON d.idMedioPago = dmp.id " +
                "LEFT JOIN DevolucionesEstados de ON d.idEstado = de.id " +
                "LEFT JOIN DevolucionesResultados dr ON dr.id = d.idResultado " +
                "WHERE d.fechaPres >= ? AND d.fechaPres <= ? " +
                "UNION ALL " +
                "SELECT " +
                "'ISC' AS procedencia, " +
                "ISNULL(i.montoAprobado, 0) AS importe, " +
                "i.ruc, " +
                "CASE WHEN ISNULL(c.nombreCorto, '') <> '' THEN c.nombreCorto ELSE c.razonSocial END AS signer, " +
                "i.idEstadoCheque, dech.descripcion AS recoge, " +
                "i.idMedioDePago AS idMedioPago, dmp.descripcion AS tipo, " +
                "i.idEstadoSolicitud AS idEstado, de.descripcion AS estado, " +
                "i.periodo, CONVERT(VARCHAR(10), i.fechaSolicitud, 120) AS fechaSolicitud, " +
                "ISNULL(i.monto, 0) AS montoSolicitado, " +
                "ISNULL(i.montoAprobado, 0) AS montoAprobado, " +
                "0.00 AS saldoBn, " +
                "(ISNULL(i.monto, 0) - ISNULL(i.montoAprobado, 0)) AS diferencia, " +
                "i.idResultado, dr.descripcion AS resultado " +
                "FROM iscTransportistas i " +
                "LEFT JOIN Cliente c ON i.ruc = c.ruc " +
                "LEFT JOIN DevolucionesEstadoCheque dech ON i.idEstadoCheque = dech.id " +
                "LEFT JOIN DevolucionesMedioPago dmp ON i.idMedioDePago = dmp.id " +
                "LEFT JOIN DevolucionesEstados de ON i.idEstadoSolicitud = de.id " +
                "LEFT JOIN DevolucionesResultados dr ON dr.id = i.idResultado " +
                "WHERE i.fechaSolicitud >= ? AND i.fechaSolicitud <= ? " +
                "ORDER BY fechaSolicitud DESC";

        try {
            return jdbcTemplate.query(sql, new Object[] { fechaInicio, fechaFin, fechaInicio, fechaFin },
                    (rs, rowNum) -> BiBecomDevolucionItemDTO.builder()
                            .procedimiento(rs.getString("procedencia"))
                            .importe(rs.getBigDecimal("importe"))
                            .ruc(rs.getString("ruc"))
                            .signer(rs.getString("signer"))
                            .idEstadoCheque((Integer) rs.getObject("idEstadoCheque"))
                            .recoge(rs.getString("recoge"))
                            .tipo(rs.getString("tipo"))
                            .idEstado((Integer) rs.getObject("idEstado"))
                            .estado(rs.getString("estado"))
                            .periodo(rs.getString("periodo"))
                            .fechaSolicitud(rs.getString("fechaSolicitud"))
                            .montoSolicitado(rs.getBigDecimal("montoSolicitado"))
                            .montoAprobado(rs.getBigDecimal("montoAprobado"))
                            .saldoBn(rs.getBigDecimal("saldoBn"))
                            .diferencia(rs.getBigDecimal("diferencia"))
                            .idResultado((Integer) rs.getObject("idResultado"))
                            .resultado(rs.getString("resultado"))
                            .build());
        } catch (Exception e) {
            log.error("Error al consultar la unión de devoluciones para el año: " + anio, e);
            return new ArrayList<>();
        }
    }

    public List<BiBecomActualizacionItemDTO> findAllActualizaciones() {
        String sql = "SELECT " +
                "cl.idGrupoEconomico, ISNULL(ge.descripcion, 'NINGUNO') AS descGrupoEconomico, " +
                "act.ruc, cl.razonSocial, " +
                "ce.id AS idEstadoCliente, ce.descripcion AS estadoCliente, " +
                "act.idEstado, ae.descripcion AS descEstado, " +
                "act.pInicio, act.pFinal, " +
                "CONVERT(VARCHAR(10), act.fInicio, 120) AS fechaInicio, " +
                "ISNULL(CONVERT(VARCHAR(10), act.fTermino, 120), '—') AS fechaTermino, " +
                "ISNULL(act.observacion, '') AS observacion " +
                "FROM Actualizaciones act " +
                "LEFT JOIN cliente cl ON act.ruc = cl.ruc " +
                "LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id " +
                "LEFT JOIN ClientsEstados ce ON cl.idEstado = ce.id " +
                "LEFT JOIN ActualizacionesEstados ae ON act.idEstado = ae.id " +
                "ORDER BY act.idEstado DESC, act.fInicio ASC";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> BiBecomActualizacionItemDTO.builder()
                    .idGrupoEconomico((Integer) rs.getObject("idGrupoEconomico"))
                    .grupoEmpresarial(rs.getString("descGrupoEconomico"))
                    .ruc(rs.getString("ruc"))
                    .razonSocial(rs.getString("razonSocial"))
                    .idEstadoCliente((Integer) rs.getObject("idEstadoCliente"))
                    .estadoCliente(rs.getString("estadoCliente"))
                    .idEstado((Integer) rs.getObject("idEstado"))
                    .estado(rs.getString("descEstado"))
                    .periodoInicio(rs.getString("pInicio"))
                    .periodoFinal(rs.getString("pFinal"))
                    .fechaInicio(rs.getString("fechaInicio"))
                    .fechaTermino(rs.getString("fechaTermino"))
                    .observacion(rs.getString("observacion"))
                    .build());
        } catch (Exception e) {
            log.error("Error al consultar la lista completa de Actualizaciones", e);
            return new ArrayList<>();
        }
    }
}
