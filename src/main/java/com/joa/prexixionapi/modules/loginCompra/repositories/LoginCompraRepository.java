package com.joa.prexixionapi.modules.loginCompra.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import com.joa.prexixionapi.modules.loginCompra.repositories.LoginCompraRepository;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraDTO;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraRequest;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraListDTO;
import com.joa.prexixionapi.common.utils.QueryUtils;

@Repository
@RequiredArgsConstructor
public class LoginCompraRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public String[] getVencimientoLimits(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        int anioMesInt = Integer.parseInt(anio + mes);
        String queryComprasFilasJoin = (anioMesInt < 202310)
                ? " LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = :anioAux AND ple.mes = :mesAux "
                : " LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anioAux AND sd.mes = :mesAux ";

        String sql = "SELECT MIN(v.fVencimiento), MAX(v.fVencimiento) FROM ( SELECT "
                + getFechaVencimientoSql(anio, mes, lastAnio) + " AS fVencimiento "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN clientsTipoServicio cts ON c.idTipoServicio = cts.id "
                + " LEFT JOIN ( SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6 ) cso ON c.ruc = cso.idCliente "
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 "
                + " AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= :startDate AND "
                + " ( CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= :startDate OR si.stAnioHasta IS NULL ) "
                + " LEFT JOIN cronogramaPDT cr ON :anio = cr.anio AND :mes = cr.mes "
                + " LEFT JOIN cronogramaCovidPDT crC ON :anio = crC.anio AND :mes = crC.mes "
                + " LEFT JOIN cronogramaPricoPDT crP ON :anio = crP.anio AND :mes = crP.mes "
                + " LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = :anio AND crPle.mes = :mes "
                + " LEFT JOIN CRONOGRAMACOVIDPLE_A crPleC ON crPleC.anio = :anio AND crPleC.mes = :mes "
                + " LEFT JOIN cronogramaPricoPLE_A crPleP ON crPleP.anio = :anio AND crPleP.mes = :mes "
                + " LEFT JOIN cronogramaSire crS ON :anio = crS.anio AND :mes = crS.mes "
                + " LEFT JOIN uit u ON u.anio = :lastAnio "
                + " LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = :anio  AND lp.mes = :mes "
                + " LEFT JOIN loginCompras lc ON c.ruc = lc.idCliente AND lc.anio = :anio  AND lc.mes = :mes "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = :anio  AND lv.mes = :mes "
                + queryComprasFilasJoin
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND "
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL ) "
                + " ) v "
                + " WHERE v.fVencimiento IS NOT NULL AND LEN(v.fVencimiento) > 0";

        MapSqlParameterSource params = buildBaseParams(anio, mes);

        return jdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                String min = rs.getString(1);
                String max = rs.getString(2);
                return new String[] { min != null ? min : "", max != null ? max : "" };
            }
            return new String[] { "", "" };
        });
    }

    public List<LoginCompraListDTO> list(String anio, String mes, LoginCompraRequest req) {
        StringBuilder sql = new StringBuilder(buildSelectQuery(anio, mes));
        MapSqlParameterSource params = buildBaseParams(anio, mes);
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);

        if (req != null) {
            QueryUtils.appendIntFilter(sql, params, "c.idEstado", req.getEstadoFiltro(), "estados");
            QueryUtils.appendIntFilter(sql, params, "c.idGrupoEconomico", req.getGrupoEconomicoFiltro(), "gruposEcon");
            QueryUtils.appendStringFilter(sql, params, "c.y", req.getGrupoFiltro(), "grupos");

            QueryUtils.appendIntFilter(sql, params, "lv.confirmacion", req.getVentasConfirmacionFiltro(),
                    "ventasConfirmacion");
            QueryUtils.appendIntFilter(sql, params, "lc.sire", req.getSireFiltro(), "sire");
            QueryUtils.appendIntFilter(sql, params, "lc.validacionSunat", req.getValidacionSunatFiltro(),
                    "validacionSunat");
            QueryUtils.appendIntFilter(sql, params, "lc.validacion", req.getValidacionFiltro(), "validacion");
            QueryUtils.appendIntFilter(sql, params, "lc.confirmacion", req.getConfirmacionFiltro(), "confirmacion");

            if (req.getCategoriaStoreFiltro() != null && !req.getCategoriaStoreFiltro().isEmpty()) {
                sql.append(" AND (CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END) IN (:categoriaStore) ");
                params.addValue("categoriaStore", req.getCategoriaStoreFiltro());
            }

            if (req.getFechaVencimientoMin() != null && !req.getFechaVencimientoMin().isEmpty()) {
                String fVencSql = getFechaVencimientoSql(anio, mes, lastAnio);
                sql.append(" AND (").append(fVencSql).append(") >= :fVencMin ");
                params.addValue("fVencMin", req.getFechaVencimientoMin());
            }

            if (req.getFechaVencimientoMax() != null && !req.getFechaVencimientoMax().isEmpty()) {
                String fVencSql = getFechaVencimientoSql(anio, mes, lastAnio);
                sql.append(" AND (").append(fVencSql).append(") <= :fVencMax ");
                params.addValue("fVencMax", req.getFechaVencimientoMax());
            }
        }

        sql.append(" ORDER BY c.y ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapRowToListDTO(rs, anio, mes));
    }

    public LoginCompraDTO getOne(String ruc, String anio, String mes) {
        StringBuilder sql = new StringBuilder(buildSelectQuery(anio, mes));
        sql.append(" AND c.ruc = :ruc ");

        MapSqlParameterSource params = buildBaseParams(anio, mes);
        params.addValue("ruc", ruc);

        List<LoginCompraDTO> clientes = jdbcTemplate.query(sql.toString(), params,
                (rs, rowNum) -> mapRowToDetailDTO(rs, anio, mes));
        return clientes.isEmpty() ? null : clientes.get(0);
    }

    public int countTotal(String anio, String mes) {
        String query = """
                SELECT COUNT(*) AS total
                FROM cliente c
                INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4
                WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND
                ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL )
                """;

        String startDateStr = anio + "-" + mes + "-01";
        MapSqlParameterSource params = new MapSqlParameterSource("startDate", startDateStr);

        Integer count = jdbcTemplate.queryForObject(query, params, Integer.class);
        return count != null ? count : 0;
    }

    public int countAcumulado(String proceso, String fecha, String anio, String mes) {
        java.time.LocalDate date = java.time.LocalDate.parse(fecha);
        if (date.getDayOfMonth() == 1) {
            return 0;
        }
        String fechaI = date.withDayOfMonth(1).toString();
        String fechaF = date.minusDays(1).toString();

        String queryProcesoFecha = "";
        switch (proceso) {
            case "validacion":
                queryProcesoFecha = " :fechaI <= lc.validacionFecha AND lc.validacionFecha <= :fechaF AND lc.validacion IN (1,2) ";
                break;
            case "confirmacion":
                queryProcesoFecha = " :fechaI <= lc.confirmacionFecha AND lc.confirmacionFecha <= :fechaF AND lc.confirmacion = 1 ";
                break;
        }

        if (queryProcesoFecha.isEmpty()) {
            return 0;
        }

        String query = "SELECT COUNT(*) AS total \n"
                + " FROM cliente c \n"
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + " LEFT JOIN loginCompras lc ON lc.idCliente = c.ruc \n"
                + " WHERE " + queryProcesoFecha + " \n"
                + " AND CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND \n"
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL ) \n"
                + " AND (:anio IS NULL OR :anio = '' OR (lc.anio = :anio AND lc.mes = :mes)) ";

        String startDateStr = anio + "-" + mes + "-01";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fechaI", fechaI)
                .addValue("fechaF", fechaF)
                .addValue("startDate", startDateStr)
                .addValue("anio", anio)
                .addValue("mes", mes);

        Integer count = jdbcTemplate.queryForObject(query, params, Integer.class);
        return count != null ? count : 0;
    }

    public List<LoginCompraDTO> listExcelDiario(String proceso, String fecha, String anio, String mes) {
        return listExcelDiario(proceso, fecha, fecha, anio, mes);
    }

    public List<LoginCompraDTO> listExcelDiario(String proceso, String fechaI, String fechaF, String anio, String mes) {
        if (fechaI == null || fechaI.isEmpty()) {
            fechaI = fechaF;
        }
        if (fechaF == null || fechaF.isEmpty()) {
            fechaF = fechaI;
        }

        String queryProcesoFecha = "";

        switch (proceso) {
            case "validacion":
                queryProcesoFecha = " :fechaI <= lc.validacionFecha AND lc.validacionFecha <= :fechaF ";
                break;
            case "confirmacion":
                queryProcesoFecha = " :fechaI <= lc.confirmacionFecha AND lc.confirmacionFecha <= :fechaF ";
                break;
        }

        if (queryProcesoFecha.isEmpty()) {
            return new ArrayList<>();
        }

        String query = " SELECT c.ruc, c.y, c.razonSocial, c.nombreCorto, c.idEstado, cE.descripcion AS estado, \n"
                + " c.idTipoServicio, tS.abreviatura as tipoServicioAbr, tS.descripcion as tipoServicio, c.prioridad, \n"
                + " c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, \n"
                + " 0 as categoriaStore, \n"
                + " c.rTMypeTributario, c.rTRus, c.rTEspecial, c.rTGeneral, c.rTAmazonico, c.rTAgrario, c.rT1ra, c.rT2da, c.rT3ra, c.rT4ta, c.rT5ta, c.fPle, \n"
                + " lp.movimiento, \n"
                + " lc.validacion, lc.validacionUsuario, lc.validacionFecha, \n"
                + " lc.confirmacion, lc.confirmacionUsuario, lc.confirmacionFecha, \n"
                + " lc.version, lc.observacion, \n"
                + " c.sunatSire, c.externoSire, c.gerenciaSire, \n"
                + " 0 as sire, NULL as sireUsuario, NULL as sireFecha, 0 as validacionSunat, NULL as validacionSunatUsuario, NULL as validacionSunatFecha, NULL as validacionHora, NULL as confirmacionHora, NULL as fVencimiento, '' as modalidad, \n"
                + " 0 as ventasConfirmacion, NULL as ventasConfirmacionUsuario, NULL as ventasConfirmacionFecha, \n"
                + " 0 as comprasFilas \n"
                + " FROM cliente c \n"
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id \n"
                + " LEFT JOIN clientsTipoServicio tS ON c.idTipoServicio = tS.id \n"
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id \n"
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc AND lp.anio = :anio  AND lp.mes = :mes \n"
                + " LEFT JOIN loginCompras lc ON lc.idCliente = c.ruc AND lc.anio = :anio  AND lc.mes = :mes \n"
                + " WHERE " + queryProcesoFecha + " ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fechaI", fechaI)
                .addValue("fechaF", fechaF)
                .addValue("anio", anio)
                .addValue("mes", mes);

        return jdbcTemplate.query(query, params, (rs, rowNum) -> mapRowToDetailDTO(rs, anio, mes));
    }

    private String getFechaVencimientoSql(String anio, String mes, String lastAnio) {
        return "CASE WHEN (" + anio + mes + " >= 202310) "
                + "	 THEN CASE WHEN si.idCliente IS NOT NULL "
                + "			   THEN CASE WHEN c.y = '0' THEN crS.fecha0 WHEN c.y = '1' THEN crS.fecha1 WHEN c.y = '2' THEN crS.fecha2 "
                + "				     WHEN c.y = '3' THEN crS.fecha3 WHEN c.y = '4' THEN crS.fecha4 WHEN c.y = '5' THEN crS.fecha5 "
                + "				     WHEN c.y = '6' THEN crS.fecha6 WHEN c.y = '7' THEN crS.fecha7 WHEN c.y = '8' THEN crS.fecha8 "
                + "				     WHEN c.y = '9' THEN crS.fecha9 END "
                + "			   ELSE CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "                                  WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "                                  WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "                                  WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END END "
                + "	 ELSE CASE WHEN c.fPle IS NOT NULL THEN "
                + "     	   CASE WHEN c.fPle != '' THEN "
                + "     	            CASE WHEN (" + anio + mes + " >= 202002) AND (" + anio + mes
                + " <= 202008) THEN "
                + "     	                    CASE WHEN (" + anio + mes + " = 202002) THEN "
                + "     	                                CASE WHEN (c.y IN ('0','1')) OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 2300)) THEN "
                + "     	                                        CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 "
                + "     	                                             WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 "
                + "     	                                             WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 "
                + "     	                                             WHEN c.y = '9' THEN crPle.fecha9 END "
                + "     	                                     ELSE "
                + "     	                                        CASE WHEN c.fPrico = '' THEN "
                + "     	                                                CASE WHEN c.y = '0' THEN crPleC.fecha0 WHEN c.y = '1' THEN crPleC.fecha1 WHEN c.y = '2' THEN crPleC.fecha2 "
                + "     	                                                     WHEN c.y = '3' THEN crPleC.fecha3 WHEN c.y = '4' THEN crPleC.fecha4 WHEN c.y = '5' THEN crPleC.fecha5 "
                + "     	                                                     WHEN c.y = '6' THEN crPleC.fecha6 WHEN c.y = '7' THEN crPleC.fecha7 WHEN c.y = '8' THEN crPleC.fecha8 "
                + "     	                                                     WHEN c.y = '9' THEN crPleC.fecha9 END "
                + "     	                                             ELSE "
                + "     	                                                CASE WHEN c.y = '0' THEN crPleP.fecha0 WHEN c.y = '1' THEN crPleP.fecha1 WHEN c.y = '2' THEN crPleP.fecha2 "
                + "     	                                                     WHEN c.y = '3' THEN crPleP.fecha3 WHEN c.y = '4' THEN crPleP.fecha4 WHEN c.y = '5' THEN crPleP.fecha5 "
                + "     	                                                     WHEN c.y = '6' THEN crPleP.fecha6 WHEN c.y = '7' THEN crPleP.fecha7 WHEN c.y = '8' THEN crPleP.fecha8 "
                + "     	                                                     WHEN c.y = '9' THEN crPleP.fecha9 END "
                + "     	                                        END "
                + "     	                                END "
                + "     	                         WHEN (" + anio + mes + " > 202002) THEN "
                + "     	                             CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 5000)) THEN "
                + "     	                                     CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 "
                + "     	                                          WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 "
                + "     	                                          WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 "
                + "     	                                          WHEN c.y = '9' THEN crPle.fecha9 END "
                + "     	                                  ELSE "
                + "     	                                        CASE WHEN c.fPrico = '' THEN "
                + "     	                                                CASE WHEN c.y = '0' THEN crPleC.fecha0 WHEN c.y = '1' THEN crPleC.fecha1 WHEN c.y = '2' THEN crPleC.fecha2 "
                + "     	                                                     WHEN c.y = '3' THEN crPleC.fecha3 WHEN c.y = '4' THEN crPleC.fecha4 WHEN c.y = '5' THEN crPleC.fecha5 "
                + "     	                                                     WHEN c.y = '6' THEN crPleC.fecha6 WHEN c.y = '7' THEN crPleC.fecha7 WHEN c.y = '8' THEN crPleC.fecha8 "
                + "     	                                                     WHEN c.y = '9' THEN crPleC.fecha9 END "
                + "     	                                             ELSE "
                + "     	                                                CASE WHEN c.y = '0' THEN crPleP.fecha0 WHEN c.y = '1' THEN crPleP.fecha1 WHEN c.y = '2' THEN crPleP.fecha2 "
                + "     	                                                     WHEN c.y = '3' THEN crPleP.fecha3 WHEN c.y = '4' THEN crPleP.fecha4 WHEN c.y = '5' THEN crPleP.fecha5 "
                + "     	                                                     WHEN c.y = '6' THEN crPleP.fecha6 WHEN c.y = '7' THEN crPleP.fecha7 WHEN c.y = '8' THEN crPleP.fecha8 "
                + "     	                                                     WHEN c.y = '9' THEN crPleP.fecha9 END "
                + "     	                                        END "
                + "     	                             END "
                + "     	                    END "
                + "     	                 ELSE "
                + "     	                    CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 "
                + "     	                         WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 "
                + "     	                         WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 "
                + "     	                         WHEN c.y = '9' THEN crPle.fecha9 END "
                + "     	            END "
                + "     	        ELSE "
                + "     	            CASE WHEN (" + anio + mes + " >= 202002) AND (" + anio + mes
                + " <= 202008) THEN "
                + "     	                   CASE WHEN (" + anio + mes + " = 202002) THEN "
                + "     	                               CASE WHEN (c.y = '0') OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 2300)) THEN "
                + "     	                                       CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "     	                                            WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "     	                                            WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "     	                                            WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END "
                + "     	                                    ELSE "
                + "     	                                       CASE WHEN c.fPrico = '' THEN "
                + "     	                                               CASE WHEN c.y = '0' THEN crC.fecha0 WHEN c.y = '1' THEN crC.fecha1 WHEN c.y = '2' THEN crC.fecha2 "
                + "     	                                                    WHEN c.y = '3' THEN crC.fecha3 WHEN c.y = '4' THEN crC.fecha4 WHEN c.y = '5' THEN crC.fecha5 "
                + "     	                                                    WHEN c.y = '6' THEN crC.fecha6 WHEN c.y = '7' THEN crC.fecha7 WHEN c.y = '8' THEN crC.fecha8 "
                + "     	                                                    WHEN c.y = '9' THEN crC.fecha9 END "
                + "     	                                            ELSE "
                + "     	                                               CASE WHEN c.y = '0' THEN crP.fecha0 WHEN c.y = '1' THEN crP.fecha1 WHEN c.y = '2' THEN crP.fecha2 "
                + "     	                                                    WHEN c.y = '3' THEN crP.fecha3 WHEN c.y = '4' THEN crP.fecha4 WHEN c.y = '5' THEN crP.fecha5 "
                + "     	                                                    WHEN c.y = '6' THEN crP.fecha6 WHEN c.y = '7' THEN crP.fecha7 WHEN c.y = '8' THEN crP.fecha8 "
                + "     	                                                    WHEN c.y = '9' THEN crP.fecha9 END "
                + "     	                                       END "
                + "     	                               END "
                + "     	                        WHEN (" + anio + mes + " > 202002) THEN "
                + "     	                            CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 5000)) THEN "
                + "     	                                    CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "     	                                         WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "     	                                         WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "     	                                         WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END "
                + "     	                                 ELSE "
                + "     	                                    CASE WHEN c.fPrico = '' THEN "
                + "     	                                            CASE WHEN c.y = '0' THEN crC.fecha0 WHEN c.y = '1' THEN crC.fecha1 WHEN c.y = '2' THEN crC.fecha2 "
                + "     	                                                 WHEN c.y = '3' THEN crC.fecha3 WHEN c.y = '4' THEN crC.fecha4 WHEN c.y = '5' THEN crC.fecha5 "
                + "     	                                                 WHEN c.y = '6' THEN crC.fecha6 WHEN c.y = '7' THEN crC.fecha7 WHEN c.y = '8' THEN crC.fecha8 "
                + "     	                                                 WHEN c.y = '9' THEN crC.fecha9 END "
                + "     	                                         ELSE "
                + "     	                                            CASE WHEN c.y = '0' THEN crP.fecha0 WHEN c.y = '1' THEN crP.fecha1 WHEN c.y = '2' THEN crP.fecha2 "
                + "     	                                                 WHEN c.y = '3' THEN crP.fecha3 WHEN c.y = '4' THEN crP.fecha4 WHEN c.y = '5' THEN crP.fecha5 "
                + "     	                                                 WHEN c.y = '6' THEN crP.fecha6 WHEN c.y = '7' THEN crP.fecha7 WHEN c.y = '8' THEN crP.fecha8 "
                + "     	                                                 WHEN c.y = '9' THEN crP.fecha9 END "
                + "     	                                    END "
                + "     	                            END "
                + "     	                   END "
                + "     	                ELSE "
                + "     	                   CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "     	                        WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "     	                        WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "     	                        WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END "
                + "     	            END "
                + "     	   END "
                + "       END "
                + " END ";
    }

    private String buildSelectQuery(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        int anioMesInt = Integer.parseInt(anio + mes);

        String queryComprasFilasColumna = (anioMesInt < 202310) ? " ple.comprasFilas, " : " sd.comprasFilas, ";
        String queryComprasFilasJoin = (anioMesInt < 202310)
                ? " LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = :anioAux AND ple.mes = :mesAux "
                : " LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anioAux AND sd.mes = :mesAux ";

        return "SELECT c.idEstado, cE.descripcion AS estado, "
                + " c.idTipoServicio, cts.abreviatura AS tipoServicioAbr, cts.descripcion AS tipoServicio, "
                + " c.prioridad, "
                + " CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore, "
                + " c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, "
                + " c.ruc, c.y, c.razonSocial, c.nombreCorto, c.fPle, "
                + " c.rTMypeTributario, c.rTRus, c.rTEspecial, c.rTGeneral, c.rTAmazonico, c.rTAgrario, c.rT1ra, c.rT2da, c.rT3ra, c.rT4ta, c.rT5ta, "
                + getFechaVencimientoSql(anio, mes, lastAnio) + " AS fVencimiento, "
                + " lp.movimiento, "
                + " c.sunatSire, c.externoSire, c.gerenciaSire, "
                + " CASE WHEN EXISTS (SELECT idModalidad FROM clienteProcesos cp WHERE cp.idCliente = c.ruc  AND cp.idProceso = 1 ) "
                + "	  THEN  (SELECT TOP 1 idModalidad FROM clienteProcesos cp WHERE cp.idCliente = c.ruc  AND cp.idProceso = 1 AND CAST(cp.anioPeriodo + cp.mesPeriodo + '01' as date) <= :startDate ORDER BY cp.anioPeriodo DESC, cp.mesPeriodo DESC )  "
                + "	  ELSE '' "
                + " END AS modalidad, "
                + queryComprasFilasColumna
                + " lv.confirmacion as ventasConfirmacion, lv.confirmacionUsuario as ventasConfirmacionUsuario, lv.confirmacionFecha as ventasConfirmacionFecha, "
                + " lc.sire, lc.sireUsuario, lc.sireFecha, "
                + " lc.validacionSunat, lc.validacionSunatUsuario, lc.validacionSunatFecha, "
                + " lc.validacion, lc.validacionUsuario, lc.validacionFecha, lc.validacionHora, "
                + " lc.confirmacion, lc.confirmacionUsuario, lc.confirmacionFecha, lc.confirmacionHora, "
                + " lc.observacion, lc.version "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN clientsTipoServicio cts ON c.idTipoServicio = cts.id "
                + " LEFT JOIN ( SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6 ) cso ON c.ruc = cso.idCliente "
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 "
                + " AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= :startDate AND "
                + " ( CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= :startDate OR si.stAnioHasta IS NULL ) "
                + " LEFT JOIN cronogramaPDT cr ON :anio = cr.anio AND :mes = cr.mes "
                + " LEFT JOIN cronogramaCovidPDT crC ON :anio = crC.anio AND :mes = crC.mes "
                + " LEFT JOIN cronogramaPricoPDT crP ON :anio = crP.anio AND :mes = crP.mes "
                + " LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = :anio AND crPle.mes = :mes "
                + " LEFT JOIN CRONOGRAMACOVIDPLE_A crPleC ON crPleC.anio = :anio AND crPleC.mes = :mes "
                + " LEFT JOIN cronogramaPricoPLE_A crPleP ON crPleP.anio = :anio AND crPleP.mes = :mes "
                + " LEFT JOIN cronogramaSire crS ON :anio = crS.anio AND :mes = crS.mes "
                + " LEFT JOIN uit u ON u.anio = :lastAnio "
                + " LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = :anio  AND lp.mes = :mes "
                + " LEFT JOIN loginCompras lc ON c.ruc = lc.idCliente AND lc.anio = :anio  AND lc.mes = :mes "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = :anio  AND lv.mes = :mes "
                + queryComprasFilasJoin
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND "
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL ) ";
    }

    private MapSqlParameterSource buildBaseParams(String anio, String mes) {
        int m = Integer.parseInt(mes);
        int a = Integer.parseInt(anio);
        if (m == 1) {
            m = 12;
            a = a - 1;
        } else {
            m = m - 1;
        }
        String anioAux = String.valueOf(a);
        String mesAux = m < 10 ? "0" + m : String.valueOf(m);
        String startDateStr = anio + "-" + mes + "-01";
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);

        return new MapSqlParameterSource()
                .addValue("anioAux", anioAux)
                .addValue("mesAux", mesAux)
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("startDate", startDateStr)
                .addValue("lastAnio", lastAnio);
    }

    private LoginCompraDTO mapRowToDetailDTO(ResultSet rs, String anio, String mes) throws SQLException {
        LoginCompraDTO dto = new LoginCompraDTO();

        dto.setRuc(rs.getString("ruc"));
        dto.setY(rs.getString("y"));
        dto.setRazonSocial(rs.getString("razonSocial"));
        dto.setNombreCorto(rs.getString("nombreCorto"));

        dto.setEstadoId(rs.getInt("idEstado"));
        dto.setEstadoDescripcion(rs.getString("estado"));
        dto.setTipoServicioAbr(rs.getString("tipoServicioAbr"));
        dto.setTipoServicioDesc(rs.getString("tipoServicio"));
        dto.setGrupoEconomicoId(rs.getInt("idGrupoEconomico"));
        dto.setDescGrupoEconomico(rs.getString("descGrupoEconomico"));
        dto.setCategoriaStore(rs.getInt("categoriaStore"));

        // Regimen Tributario
        List<String> rTs = new ArrayList<>();
        if (rs.getInt("rT3ra") != 0) {
            if (rs.getInt("rTMypeTributario") != 0)
                rTs.add("RM");
            if (rs.getInt("rTRus") != 0)
                rTs.add("RUS");
            if (rs.getInt("rTEspecial") != 0)
                rTs.add("RE");
            if (rs.getInt("rTGeneral") != 0)
                rTs.add("RG");
            if (rs.getInt("rTAmazonico") != 0)
                rTs.add("RAM");
            if (rs.getInt("rTAgrario") != 0)
                rTs.add("RAG");
        } else if (rs.getInt("rT1ra") != 0) {
            rTs.add("1ra");
        } else if (rs.getInt("rT2da") != 0) {
            rTs.add("2da");
        } else if (rs.getInt("rT4ta") != 0) {
            rTs.add("4ta");
        } else if (rs.getInt("rT5ta") != 0) {
            rTs.add("5ta");
        }
        dto.setRegimenTributario(rTs.isEmpty() ? "" : rTs.get(rTs.size() - 1));

        dto.setSireCompras(rs.getInt("sire"));
        dto.setSireUsuario(rs.getString("sireUsuario"));
        dto.setSireFecha(rs.getString("sireFecha"));

        dto.setValidacionSunat(rs.getInt("validacionSunat"));
        dto.setValidacionSunatUsuario(rs.getString("validacionSunatUsuario"));
        dto.setValidacionSunatFecha(rs.getString("validacionSunatFecha"));

        dto.setValidacion(rs.getInt("validacion"));
        dto.setValidacionUsuario(rs.getString("validacionUsuario"));

        String valUser = dto.getValidacionUsuario();
        if (valUser != null && !valUser.trim().isEmpty()) {
            try {
                String x = valUser.trim().replaceAll("\\s+", " ");
                String[] partsX = x.split(" ");
                if (partsX.length >= 3) {
                    String apellido = partsX[0];
                    String nombre = partsX[2];
                    dto.setValidacionUsuarioNombreCorto(nombre.substring(0, 1) + apellido);
                } else {
                    dto.setValidacionUsuarioNombreCorto(partsX[0]);
                }
            } catch (Exception ex) {
                dto.setValidacionUsuarioNombreCorto(valUser);
            }
        } else {
            dto.setValidacionUsuarioNombreCorto("");
        }

        dto.setValidacionFecha(rs.getString("validacionFecha"));
        dto.setValidacionHora(rs.getString("validacionHora"));

        dto.setConfirmacion(rs.getInt("confirmacion"));
        dto.setConfirmacionUsuario(rs.getString("confirmacionUsuario"));
        dto.setConfirmacionFecha(rs.getString("confirmacionFecha"));
        dto.setConfirmacionHora(rs.getString("confirmacionHora"));

        dto.setObservacion(rs.getString("observacion"));
        dto.setVersion(rs.getInt("version"));

        String fVenc = rs.getString("fVencimiento");
        dto.setFechaVencimiento(fVenc);
        if (fVenc != null && !fVenc.isEmpty()) {
            String[] parts = fVenc.split("-");
            if (parts.length >= 3) {
                dto.setDiaVencimiento(parts[2]);
                dto.setMesVencimiento(parts[1]);
            }
            try {
                java.time.LocalDate fVencDate = java.time.LocalDate.parse(fVenc);
                java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("America/Lima"));
                dto.setDifference((int) java.time.temporal.ChronoUnit.DAYS.between(fVencDate, today));
            } catch (Exception e) {
                dto.setDifference(-999);
            }
        } else {
            dto.setDifference(-999);
        }

        dto.setAnio(anio);
        dto.setMes(mes);
        dto.setMovimiento(rs.getString("movimiento"));
        dto.setComprasFilas(rs.getInt("comprasFilas"));
        String mod = rs.getString("modalidad");
        dto.setModalidad(mod != null && !mod.isEmpty() ? Integer.parseInt(mod) : 0);
        dto.setPrioridad(rs.getString("prioridad"));

        String fPle = rs.getString("fPle");
        dto.setPle((fPle == null || fPle.isEmpty()) ? "N/A" : "PLE");

        dto.setSunatSire(rs.getInt("sunatSire"));
        dto.setExternoSire(rs.getInt("externoSire"));
        dto.setGerenciaSire(rs.getInt("gerenciaSire"));

        List<String> sireList = new ArrayList<>();
        if (dto.getSunatSire() != 0)
            sireList.add("SEE");
        if (dto.getExternoSire() != 0)
            sireList.add("EXT");
        if (dto.getGerenciaSire() != 0)
            sireList.add("GCOM");
        dto.setSireGlobal(String.join(", ", sireList));

        dto.setVentasConfirmacion(rs.getInt("ventasConfirmacion"));
        dto.setVentasConfirmacionUsuario(rs.getString("ventasConfirmacionUsuario"));
        dto.setVentasConfirmacionFecha(rs.getString("ventasConfirmacionFecha"));

        return dto;
    }

    public int insertUpdate(LoginCompraDTO dto) {
        String checkSql = "SELECT version FROM loginCompras WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes";
        MapSqlParameterSource checkParams = new MapSqlParameterSource()
                .addValue("idCliente", dto.getRuc())
                .addValue("anio", dto.getAnio())
                .addValue("mes", dto.getMes());

        List<Integer> versions = jdbcTemplate.query(checkSql, checkParams, (rs, rowNum) -> rs.getInt("version"));

        if (versions.isEmpty()) {
            String insertSql = "INSERT INTO loginCompras (idCliente, anio, mes, sire, sireUsuario, sireFecha, "
                    + "validacionSunat, validacionSunatUsuario, validacionSunatFecha, "
                    + "validacion, validacionUsuario, validacionFecha, validacionHora, "
                    + "confirmacion, confirmacionUsuario, confirmacionFecha, confirmacionHora, "
                    + "observacion, version) "
                    + "VALUES (:idCliente, :anio, :mes, :sire, :sireUsuario, :sireFecha, "
                    + ":validacionSunat, :validacionSunatUsuario, :validacionSunatFecha, "
                    + ":validacion, :validacionUsuario, :validacionFecha, :validacionHora, "
                    + ":confirmacion, :confirmacionUsuario, :confirmacionFecha, :confirmacionHora, "
                    + ":observacion, 1)";
            MapSqlParameterSource insertParams = new MapSqlParameterSource()
                    .addValue("idCliente", dto.getRuc())
                    .addValue("anio", dto.getAnio())
                    .addValue("mes", dto.getMes())
                    .addValue("sire", dto.getSireCompras())
                    .addValue("sireUsuario", dto.getSireUsuario())
                    .addValue("sireFecha", dto.getSireFecha())
                    .addValue("validacionSunat", dto.getValidacionSunat())
                    .addValue("validacionSunatUsuario", dto.getValidacionSunatUsuario())
                    .addValue("validacionSunatFecha", dto.getValidacionSunatFecha())
                    .addValue("validacion", dto.getValidacion())
                    .addValue("validacionUsuario", dto.getValidacionUsuario())
                    .addValue("validacionFecha", dto.getValidacionFecha())
                    .addValue("validacionHora", dto.getValidacionHora())
                    .addValue("confirmacion", dto.getConfirmacion())
                    .addValue("confirmacionUsuario", dto.getConfirmacionUsuario())
                    .addValue("confirmacionFecha", dto.getConfirmacionFecha())
                    .addValue("confirmacionHora", dto.getConfirmacionHora())
                    .addValue("observacion", dto.getObservacion());

            jdbcTemplate.update(insertSql, insertParams);
            return 1;
        } else {
            Integer currentVersion = versions.get(0);
            if (dto.getVersion() != null && !dto.getVersion().equals(currentVersion)) {
                throw new org.springframework.dao.OptimisticLockingFailureException("Version mismatch");
            }

            String updateSql = "UPDATE loginCompras SET "
                    + "sire = :sire, sireUsuario = :sireUsuario, sireFecha = :sireFecha, "
                    + "validacionSunat = :validacionSunat, validacionSunatUsuario = :validacionSunatUsuario, validacionSunatFecha = :validacionSunatFecha, "
                    + "validacion = :validacion, validacionUsuario = :validacionUsuario, validacionFecha = :validacionFecha, validacionHora = :validacionHora, "
                    + "confirmacion = :confirmacion, confirmacionUsuario = :confirmacionUsuario, confirmacionFecha = :confirmacionFecha, confirmacionHora = :confirmacionHora, "
                    + "observacion = :observacion, version = version + 1 "
                    + "WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes AND version = :version";

            MapSqlParameterSource updateParams = new MapSqlParameterSource()
                    .addValue("idCliente", dto.getRuc())
                    .addValue("anio", dto.getAnio())
                    .addValue("mes", dto.getMes())
                    .addValue("sire", dto.getSireCompras())
                    .addValue("sireUsuario", dto.getSireUsuario())
                    .addValue("sireFecha", dto.getSireFecha())
                    .addValue("validacionSunat", dto.getValidacionSunat())
                    .addValue("validacionSunatUsuario", dto.getValidacionSunatUsuario())
                    .addValue("validacionSunatFecha", dto.getValidacionSunatFecha())
                    .addValue("validacion", dto.getValidacion())
                    .addValue("validacionUsuario", dto.getValidacionUsuario())
                    .addValue("validacionFecha", dto.getValidacionFecha())
                    .addValue("validacionHora", dto.getValidacionHora())
                    .addValue("confirmacion", dto.getConfirmacion())
                    .addValue("confirmacionUsuario", dto.getConfirmacionUsuario())
                    .addValue("confirmacionFecha", dto.getConfirmacionFecha())
                    .addValue("confirmacionHora", dto.getConfirmacionHora())
                    .addValue("observacion", dto.getObservacion())
                    .addValue("version", currentVersion != null ? currentVersion : 0);

            int updated = jdbcTemplate.update(updateSql, updateParams);
            if (updated == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Version mismatch or record deleted");
            }
            return 2;
        }
    }

    private LoginCompraListDTO mapRowToListDTO(ResultSet rs, String anio, String mes) throws SQLException {
        LoginCompraListDTO dto = new LoginCompraListDTO();

        dto.setRuc(rs.getString("ruc"));
        dto.setY(rs.getString("y"));
        dto.setRazonSocial(rs.getString("razonSocial"));
        dto.setNombreCorto(rs.getString("nombreCorto"));

        int estadoId = rs.getInt("idEstado");
        dto.setEstadoId(estadoId);
        dto.setEstadoDescripcion(rs.getString("estado"));

        dto.setCategoriaStore(rs.getObject("categoriaStore", Integer.class));

        int grupoEconomicoId = rs.getInt("idGrupoEconomico");
        dto.setGrupoEconomicoId(grupoEconomicoId);
        dto.setDescGrupoEconomico(rs.getString("descGrupoEconomico"));

        // Regimen Tributario
        List<String> rTs = new ArrayList<>();
        if (rs.getInt("rT3ra") != 0) {
            if (rs.getInt("rTMypeTributario") != 0)
                rTs.add("RM");
            if (rs.getInt("rTRus") != 0)
                rTs.add("RUS");
            if (rs.getInt("rTEspecial") != 0)
                rTs.add("RE");
            if (rs.getInt("rTGeneral") != 0)
                rTs.add("RG");
            if (rs.getInt("rTAmazonico") != 0)
                rTs.add("RAM");
            if (rs.getInt("rTAgrario") != 0)
                rTs.add("RAG");
        } else if (rs.getInt("rT1ra") != 0) {
            rTs.add("1ra");
        } else if (rs.getInt("rT2da") != 0) {
            rTs.add("2da");
        } else if (rs.getInt("rT4ta") != 0) {
            rTs.add("4ta");
        } else if (rs.getInt("rT5ta") != 0) {
            rTs.add("5ta");
        }
        dto.setRegimenTributario(rTs.isEmpty() ? "" : rTs.get(rTs.size() - 1));

        dto.setAnio(anio);
        dto.setMes(mes);

        dto.setFechaVencimiento(rs.getString("fVencimiento"));

        dto.setMovimiento(rs.getString("movimiento"));

        int cFilas = rs.getObject("comprasFilas", Integer.class) != null ? rs.getObject("comprasFilas", Integer.class)
                : 0;
        dto.setComprasFilas(cFilas);

        dto.setVentasConfirmacion(rs.getObject("ventasConfirmacion", Integer.class));

        dto.setSireCompras(rs.getObject("sire", Integer.class));
        dto.setSireUsuario(rs.getString("sireUsuario"));
        dto.setSireFecha(rs.getString("sireFecha"));

        dto.setValidacionSunat(rs.getObject("validacionSunat", Integer.class));
        dto.setValidacionSunatUsuario(rs.getString("validacionSunatUsuario"));
        dto.setValidacionSunatFecha(rs.getString("validacionSunatFecha"));

        if (hasColumn(rs, "validacion")) {
            dto.setValidacion(rs.getObject("validacion", Integer.class));
            dto.setValidacionUsuario(rs.getString("validacionUsuario"));
            dto.setValidacionFecha(rs.getString("validacionFecha"));
        }

        String valUser = dto.getValidacionUsuario();
        if (valUser != null && !valUser.trim().isEmpty()) {
            try {
                String x = valUser.trim().replaceAll("\\s+", " ");
                String[] partsX = x.split(" ");
                if (partsX.length >= 3) {
                    String apellido = partsX[0];
                    String nombre = partsX[2];
                    dto.setValidacionUsuarioNombreCorto(nombre.substring(0, 1) + apellido);
                } else {
                    dto.setValidacionUsuarioNombreCorto(partsX[0]);
                }
            } catch (Exception ex) {
                dto.setValidacionUsuarioNombreCorto(valUser);
            }
        } else {
            dto.setValidacionUsuarioNombreCorto("");
        }

        if (hasColumn(rs, "confirmacion")) {
            dto.setConfirmacion(rs.getObject("confirmacion", Integer.class));
            dto.setConfirmacionUsuario(rs.getString("confirmacionUsuario"));
            dto.setConfirmacionFecha(rs.getString("confirmacionFecha"));
        }

        if (hasColumn(rs, "observacion")) {
            dto.setObservacion(rs.getString("observacion"));
        }

        return dto;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
