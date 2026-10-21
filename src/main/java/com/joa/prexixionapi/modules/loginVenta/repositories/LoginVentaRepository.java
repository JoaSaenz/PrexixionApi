package com.joa.prexixionapi.modules.loginVenta.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaListDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaDTO;
import com.joa.prexixionapi.modules.loginVenta.dto.LoginVentaRequest;
import com.joa.prexixionapi.modules.loginVenta.repositories.LoginVentaRepository;
import com.joa.prexixionapi.common.utils.QueryUtils;

@Repository
@RequiredArgsConstructor
public class LoginVentaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

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

    public String[] getVencimientoLimits(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        int anioMesInt = Integer.parseInt(anio + mes);
        String queryComprasFilasJoin = (anioMesInt < 202310)
                ? " LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = :anioAux AND ple.mes = :mesAux "
                : " LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anioAux AND sd.mes = :mesAux ";

        String sql = "SELECT MIN(v.fechaVencimiento), MAX(v.fechaVencimiento) FROM ( SELECT "
                + getFechaVencimientoSql(anio, mes, lastAnio) + " AS fechaVencimiento "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN ( SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6 ) cso ON c.ruc = cso.idCliente "
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 "
                + " AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= '" + anio + "-" + mes + "-01' AND "
                + " ( CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= '" + anio + "-" + mes
                + "-01' OR si.stAnioHasta IS NULL ) "
                + " LEFT JOIN cronogramaPDT cr ON '" + anio + "' = cr.anio AND '" + mes + "' = cr.mes "
                + " LEFT JOIN cronogramaCovidPDT crC ON '" + anio + "' = crC.anio AND '" + mes + "' = crC.mes "
                + " LEFT JOIN cronogramaPricoPDT crP ON '" + anio + "' = crP.anio AND '" + mes + "' = crP.mes "
                + " LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = '" + anio + "' AND crPle.mes = '" + mes + "' "
                + " LEFT JOIN CRONOGRAMACOVIDPLE_A crPleC ON crPleC.anio = '" + anio + "' AND crPleC.mes = '" + mes
                + "' "
                + " LEFT JOIN cronogramaPricoPLE_A crPleP ON crPleP.anio = '" + anio + "' AND crPleP.mes = '" + mes
                + "'  "
                + " LEFT JOIN cronogramaSire crS ON '" + anio + "' = crS.anio AND '" + mes + "' = crS.mes "
                + " LEFT JOIN uit u ON u.anio = '" + lastAnio + "' "
                + " LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = '" + anio + "'  AND lp.mes = '" + mes
                + "' "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = '" + anio + "'  AND lv.mes = '" + mes
                + "' "
                + " LEFT JOIN personal per ON lv.responsable = per.dni "
                + queryComprasFilasJoin
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= '" + anio + "-" + mes + "-01' AND "
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= '" + anio + "-" + mes
                + "-01' OR p.stAnioHasta IS NULL ) "
                + " ) v "
                + " WHERE v.fechaVencimiento IS NOT NULL AND LEN(v.fechaVencimiento) > 0";

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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("anioAux", anioAux)
                .addValue("mesAux", mesAux);

        return jdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                String min = rs.getString(1);
                String max = rs.getString(2);
                return new String[] { min != null ? min : "", max != null ? max : "" };
            }
            return new String[] { "", "" };
        });
    }

    public List<LoginVentaListDTO> list(String anio, String mes, LoginVentaRequest req) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        int anioMesInt = Integer.parseInt(anio + mes);

        String queryComprasFilasColumna = (anioMesInt < 202310) ? " ple.comprasFilas, " : " sd.comprasFilas, ";
        String queryComprasFilasJoin = (anioMesInt < 202310)
                ? " LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = :anioAux AND ple.mes = :mesAux "
                : " LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anioAux AND sd.mes = :mesAux ";

        StringBuilder sql = new StringBuilder("SELECT c.idEstado, cE.descripcion AS estado, "
                + " c.idTipoServicio, cts.abreviatura as tipoServicioAbr, cts.descripcion as tipoServicio, "
                + " c.prioridad, "
                + " CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore, "
                + " c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, "
                + " c.ruc, c.y, c.razonSocial, c.nombreCorto, c.fPle, "
                + " c.rTMypeTributario, c.rTRus, c.rTEspecial, c.rTGeneral, c.rTAmazonico, c.rTAgrario, c.rT1ra, c.rT2da, c.rT3ra, c.rT4ta, c.rT5ta, "
                + getFechaVencimientoSql(anio, mes, lastAnio) + " AS fechaVencimiento, "
                + " lp.movimiento, "
                + " c.sunatSire, c.externoSire, c.gerenciaSire, "
                + " CASE WHEN EXISTS (SELECT idModalidad FROM clienteProcesos cp WHERE cp.idCliente = c.ruc  AND cp.idProceso = 2 ) "
                + "	  THEN  (SELECT TOP 1 idModalidad FROM clienteProcesos cp WHERE cp.idCliente = c.ruc  AND cp.idProceso = 2 AND CAST(cp.anioPeriodo + cp.mesPeriodo + '01' as date) <= '"
                + anio + "-" + mes + "-01' ORDER BY cp.anioPeriodo DESC, cp.mesPeriodo DESC )  "
                + "	  ELSE '' "
                + " END AS modalidad, "
                + " lv.responsable, "
                + " LEFT(per.nombres, 1) + "
                + " SUBSTRING(per.apellidos, 1, "
                + "    CASE "
                + "        WHEN CHARINDEX(' ', per.apellidos) - 1 < 0 "
                + "        THEN LEN(per.apellidos) "
                + "        ELSE CHARINDEX(' ', per.apellidos) - 1 "
                + "    END "
                + ") AS descResponsable, "
                + queryComprasFilasColumna
                + " lv.registro, lv.registroUsuario, lv.registroFecha, lv.registroHora,"
                + " lv.revisionSunat, lv.revisionSunatUsuario, lv.revisionSunatFecha,"
                + " lv.seeVentas, lv.extVentas, "
                + " lv.validacion, lv.validacionUsuario, lv.validacionFecha, lv.validacionHora,"
                + " lv.confirmacion, lv.confirmacionUsuario, lv.confirmacionFecha, lv.confirmacionHora,"
                + " lv.observacion, lv.version "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN clientsTipoServicio cts ON c.idTipoServicio = cts.id "
                + " LEFT JOIN ( SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6 ) cso ON c.ruc = cso.idCliente "
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 "
                + " AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= '" + anio + "-" + mes + "-01' AND "
                + " ( CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= '" + anio + "-" + mes
                + "-01' OR si.stAnioHasta IS NULL ) "
                + " LEFT JOIN cronogramaPDT cr ON '" + anio + "' = cr.anio AND '" + mes + "' = cr.mes "
                + " LEFT JOIN cronogramaCovidPDT crC ON '" + anio + "' = crC.anio AND '" + mes + "' = crC.mes "
                + " LEFT JOIN cronogramaPricoPDT crP ON '" + anio + "' = crP.anio AND '" + mes + "' = crP.mes "
                + " LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = '" + anio + "' AND crPle.mes = '" + mes + "' "
                + " LEFT JOIN CRONOGRAMACOVIDPLE_A crPleC ON crPleC.anio = '" + anio + "' AND crPleC.mes = '" + mes
                + "' "
                + " LEFT JOIN cronogramaPricoPLE_A crPleP ON crPleP.anio = '" + anio + "' AND crPleP.mes = '" + mes
                + "'  "
                + " LEFT JOIN cronogramaSire crS ON '" + anio + "' = crS.anio AND '" + mes + "' = crS.mes "
                + " LEFT JOIN uit u ON u.anio = '" + lastAnio + "' "
                + " LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = '" + anio + "'  AND lp.mes = '" + mes
                + "' "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = '" + anio + "'  AND lv.mes = '" + mes
                + "' "
                + " LEFT JOIN personal per ON lv.responsable = per.dni "
                + queryComprasFilasJoin
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= '" + anio + "-" + mes + "-01' AND "
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= '" + anio + "-" + mes
                + "-01' OR p.stAnioHasta IS NULL ) ");

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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("anioAux", anioAux)
                .addValue("mesAux", mesAux);

        if (req != null) {
            QueryUtils.appendIntFilter(sql, params, "c.idEstado", req.getEstadoFiltro(), "estados");
            QueryUtils.appendIntFilter(sql, params, "c.idGrupoEconomico", req.getGrupoEconomicoFiltro(), "gruposEcon");
            QueryUtils.appendStringFilter(sql, params, "c.y", req.getGrupoFiltro(), "grupos");
            if (req.getResponsableFiltro() != null && !req.getResponsableFiltro().isEmpty()) {
                if (req.getResponsableFiltro().contains("0")) {
                    sql.append(
                            " AND (lv.responsable IN (:responsables) OR lv.responsable IS NULL OR lv.responsable = '' OR lv.responsable = '0') ");
                } else {
                    sql.append(" AND lv.responsable IN (:responsables) ");
                }
                params.addValue("responsables", req.getResponsableFiltro());
            }
            QueryUtils.appendIntFilter(sql, params, "lv.seeVentas", req.getSeeVentasFiltro(), "seeVentas");
            QueryUtils.appendIntFilter(sql, params, "lv.extVentas", req.getExtVentasFiltro(), "extVentas");
            QueryUtils.appendIntFilter(sql, params, "lv.registro", req.getRegistroFiltro(), "registros");
            QueryUtils.appendIntFilter(sql, params, "lv.revisionSunat", req.getRevisionSunatFiltro(), "revisiones");
            QueryUtils.appendIntFilter(sql, params, "lv.validacion", req.getValidacionFiltro(), "validaciones");
            QueryUtils.appendIntFilter(sql, params, "lv.confirmacion", req.getConfirmacionFiltro(), "confirmaciones");

            if (req.getCategoriaStoreFiltro() != null && !req.getCategoriaStoreFiltro().isEmpty()) {
                sql.append(" AND (CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END) IN (:categoriaStore) ");
                params.addValue("categoriaStore", req.getCategoriaStoreFiltro());
            }

            if (req.getFechaVencimientoMin() != null && !req.getFechaVencimientoMin().isEmpty()) {
                String fVencSql = getFechaVencimientoSql(anio, mes, anioAux);
                sql.append(" AND (").append(fVencSql).append(") >= :fVencMin ");
                params.addValue("fVencMin", req.getFechaVencimientoMin());
            }

            if (req.getFechaVencimientoMax() != null && !req.getFechaVencimientoMax().isEmpty()) {
                String fVencSql = getFechaVencimientoSql(anio, mes, anioAux);
                sql.append(" AND (").append(fVencSql).append(") <= :fVencMax ");
                params.addValue("fVencMax", req.getFechaVencimientoMax());
            }
        }

        sql.append(" ORDER BY c.y ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapRowToListDTO(rs, anio, mes));
    }

    public LoginVentaDTO getOne(String ruc, String anio, String mes) {
        String sql = "SELECT c.ruc, c.razonSocial, c.sunatSire, c.externoSire, c.gerenciaSire, "
                + "lv.version, lv.responsable, "
                + "lv.seeVentas, lv.extVentas, "
                + "lv.registro, lv.registroUsuario, lv.registroFecha, lv.registroHora, "
                + "lv.revisionSunat, lv.revisionSunatUsuario, lv.revisionSunatFecha, "
                + "lv.validacion, lv.validacionUsuario, lv.validacionFecha, lv.validacionHora, "
                + "lv.confirmacion, lv.confirmacionUsuario, lv.confirmacionFecha, lv.confirmacionHora, "
                + "lv.observacion "
                + "FROM cliente c "
                + "LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = :anio AND lv.mes = :mes "
                + "WHERE c.ruc = :ruc";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("ruc", ruc);

        List<LoginVentaDTO> clientes = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            LoginVentaDTO dto = new LoginVentaDTO();
            dto.setRuc(rs.getString("ruc"));
            dto.setRazonSocial(rs.getString("razonSocial"));
            dto.setAnio(anio);
            dto.setMes(mes);

            int sunatSire = rs.getInt("sunatSire");
            int externoSire = rs.getInt("externoSire");
            int gerenciaSire = rs.getInt("gerenciaSire");
            List<String> sireList = new ArrayList<>();
            if (sunatSire != 0) sireList.add("SEE");
            if (externoSire != 0) sireList.add("EXT");
            if (gerenciaSire != 0) sireList.add("GCOM");
            dto.setSire(String.join(", ", sireList));

            dto.setVersion(rs.getInt("version"));
            dto.setResponsable(rs.getString("responsable"));
            dto.setSeeVentas(rs.getInt("seeVentas"));
            dto.setExtVentas(rs.getInt("extVentas"));
            dto.setRegistro(rs.getInt("registro"));
            dto.setRegistroUsuario(rs.getString("registroUsuario"));
            dto.setRegistroFecha(rs.getString("registroFecha"));
            dto.setRegistroHora(rs.getString("registroHora"));
            dto.setRevisionSunat(rs.getInt("revisionSunat"));
            dto.setRevisionSunatUsuario(rs.getString("revisionSunatUsuario"));
            dto.setRevisionSunatFecha(rs.getString("revisionSunatFecha"));
            dto.setValidacion(rs.getInt("validacion"));
            dto.setValidacionUsuario(rs.getString("validacionUsuario"));
            dto.setValidacionFecha(rs.getString("validacionFecha"));
            dto.setValidacionHora(rs.getString("validacionHora"));
            dto.setConfirmacion(rs.getInt("confirmacion"));
            dto.setConfirmacionUsuario(rs.getString("confirmacionUsuario"));
            dto.setConfirmacionFecha(rs.getString("confirmacionFecha"));
            dto.setConfirmacionHora(rs.getString("confirmacionHora"));
            dto.setObservacion(rs.getString("observacion"));
            return dto;
        });

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
            case "registro":
                queryProcesoFecha = " :fechaI <= lv.registroFecha AND lv.registroFecha <= :fechaF AND lv.registro IN (1,2) ";
                break;
            case "validacion":
                queryProcesoFecha = " :fechaI <= lv.validacionFecha AND lv.validacionFecha <= :fechaF AND lv.validacion IN (1,2) ";
                break;
            case "confirmacion":
                queryProcesoFecha = " :fechaI <= lv.confirmacionFecha AND lv.confirmacionFecha <= :fechaF AND lv.confirmacion = 1 ";
                break;
        }

        if (queryProcesoFecha.isEmpty()) {
            return 0;
        }

        String query = "SELECT COUNT(*) AS total \n"
                + " FROM cliente c \n"
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + " LEFT JOIN loginVentas lv ON lv.idCliente = c.ruc \n"
                + " WHERE " + queryProcesoFecha + " \n"
                + " AND CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND \n"
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL ) \n"
                + " AND (:anio IS NULL OR :anio = '' OR (lv.anio = :anio AND lv.mes = :mes)) ";

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

    public List<LoginVentaListDTO> listExcelDiario(String proceso, String fecha, String anio, String mes) {
        return listExcelDiario(proceso, fecha, fecha, anio, mes);
    }

    public List<LoginVentaListDTO> listExcelDiario(String proceso, String fechaI, String fechaF, String anio, String mes) {
        if (fechaI == null || fechaI.isEmpty()) {
            fechaI = fechaF;
        }
        if (fechaF == null || fechaF.isEmpty()) {
            fechaF = fechaI;
        }

        String queryProcesoFecha = "";

        switch (proceso) {
            case "registro":
                queryProcesoFecha = " :fechaI <= lv.registroFecha AND lv.registroFecha <= :fechaF ";
                break;
            case "validacion":
                queryProcesoFecha = " :fechaI <= lv.validacionFecha AND lv.validacionFecha <= :fechaF ";
                break;
            case "confirmacion":
                queryProcesoFecha = " :fechaI <= lv.confirmacionFecha AND lv.confirmacionFecha <= :fechaF ";
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
                + " lv.registro, lv.registroUsuario, lv.registroFecha, \n"
                + " lv.validacion, lv.validacionUsuario, lv.validacionFecha, \n"
                + " lv.confirmacion, lv.confirmacionUsuario, lv.confirmacionFecha, \n"
                + " lv.version, lv.responsable, lv.seeVentas, lv.extVentas, lv.observacion, '' as descResponsable, \n"
                + " c.sunatSire, c.externoSire, c.gerenciaSire, \n"
                + " 0 as revisionSunat, NULL as revisionSunatUsuario, NULL as revisionSunatFecha, NULL as validacionHora, NULL as confirmacionHora, NULL as registroHora, NULL as fechaVencimiento, '' as modalidad, \n"
                + " 0 as comprasFilas \n"
                + " FROM cliente c \n"
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id \n"
                + " LEFT JOIN clientsTipoServicio tS ON c.idTipoServicio = tS.id \n"
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id \n"
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc AND lp.anio = :anio  AND lp.mes = :mes \n"
                + " LEFT JOIN loginVentas lv ON lv.idCliente = c.ruc AND lv.anio = :anio  AND lv.mes = :mes \n"
                + " WHERE " + queryProcesoFecha + " ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fechaI", fechaI)
                .addValue("fechaF", fechaF)
                .addValue("anio", anio)
                .addValue("mes", mes);

        return jdbcTemplate.query(query, params, (rs, rowNum) -> mapRowToListDTO(rs, anio, mes));
    }

    public int insertUpdate(LoginVentaDTO dto) {
        String checkSql = "SELECT version FROM loginVentas WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes";
        MapSqlParameterSource checkParams = new MapSqlParameterSource()
                .addValue("idCliente", dto.getRuc())
                .addValue("anio", dto.getAnio())
                .addValue("mes", dto.getMes());

        List<Integer> versions = jdbcTemplate.query(checkSql, checkParams, (rs, rowNum) -> rs.getInt("version"));

        if (versions.isEmpty()) {
            String insertSql = "INSERT INTO loginVentas (idCliente, anio, mes, responsable, registro, registroUsuario, registroFecha, registroHora, "
                    + "revisionSunat, revisionSunatUsuario, revisionSunatFecha, "
                    + "validacion, validacionUsuario, validacionFecha, validacionHora, "
                    + "confirmacion, confirmacionUsuario, confirmacionFecha, confirmacionHora, "
                    + "seeVentas, extVentas, observacion, version) "
                    + "VALUES (:idCliente, :anio, :mes, :responsable, :registro, :registroUsuario, :registroFecha, :registroHora, "
                    + ":revisionSunat, :revisionSunatUsuario, :revisionSunatFecha, "
                    + ":validacion, :validacionUsuario, :validacionFecha, :validacionHora, "
                    + ":confirmacion, :confirmacionUsuario, :confirmacionFecha, :confirmacionHora, "
                    + ":seeVentas, :extVentas, :observacion, 1)";
            MapSqlParameterSource insertParams = new MapSqlParameterSource()
                    .addValue("idCliente", dto.getRuc())
                    .addValue("anio", dto.getAnio())
                    .addValue("mes", dto.getMes())
                    .addValue("responsable", dto.getResponsable())
                    .addValue("registro", dto.getRegistro())
                    .addValue("registroUsuario", dto.getRegistroUsuario())
                    .addValue("registroFecha", dto.getRegistroFecha())
                    .addValue("registroHora", dto.getRegistroHora())
                    .addValue("revisionSunat", dto.getRevisionSunat())
                    .addValue("revisionSunatUsuario", dto.getRevisionSunatUsuario())
                    .addValue("revisionSunatFecha", dto.getRevisionSunatFecha())
                    .addValue("validacion", dto.getValidacion())
                    .addValue("validacionUsuario", dto.getValidacionUsuario())
                    .addValue("validacionFecha", dto.getValidacionFecha())
                    .addValue("validacionHora", dto.getValidacionHora())
                    .addValue("confirmacion", dto.getConfirmacion())
                    .addValue("confirmacionUsuario", dto.getConfirmacionUsuario())
                    .addValue("confirmacionFecha", dto.getConfirmacionFecha())
                    .addValue("confirmacionHora", dto.getConfirmacionHora())
                    .addValue("seeVentas", dto.getSeeVentas())
                    .addValue("extVentas", dto.getExtVentas())
                    .addValue("observacion", dto.getObservacion());

            jdbcTemplate.update(insertSql, insertParams);
            return 1;
        } else {
            Integer currentVersion = versions.get(0);
            if (dto.getVersion() != null && dto.getVersion() != 0 && !dto.getVersion().equals(currentVersion)) {
                throw new org.springframework.dao.OptimisticLockingFailureException("Version mismatch");
            }

            String updateSql = "UPDATE loginVentas SET "
                    + "responsable = :responsable, registro = :registro, registroUsuario = :registroUsuario, registroFecha = :registroFecha, registroHora = :registroHora, "
                    + "revisionSunat = :revisionSunat, revisionSunatUsuario = :revisionSunatUsuario, revisionSunatFecha = :revisionSunatFecha, "
                    + "validacion = :validacion, validacionUsuario = :validacionUsuario, validacionFecha = :validacionFecha, validacionHora = :validacionHora, "
                    + "confirmacion = :confirmacion, confirmacionUsuario = :confirmacionUsuario, confirmacionFecha = :confirmacionFecha, confirmacionHora = :confirmacionHora, "
                    + "seeVentas = :seeVentas, extVentas = :extVentas, observacion = :observacion, version = version + 1 "
                    + "WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes AND version = :version";

            MapSqlParameterSource updateParams = new MapSqlParameterSource()
                    .addValue("idCliente", dto.getRuc())
                    .addValue("anio", dto.getAnio())
                    .addValue("mes", dto.getMes())
                    .addValue("responsable", dto.getResponsable())
                    .addValue("registro", dto.getRegistro())
                    .addValue("registroUsuario", dto.getRegistroUsuario())
                    .addValue("registroFecha", dto.getRegistroFecha())
                    .addValue("registroHora", dto.getRegistroHora())
                    .addValue("revisionSunat", dto.getRevisionSunat())
                    .addValue("revisionSunatUsuario", dto.getRevisionSunatUsuario())
                    .addValue("revisionSunatFecha", dto.getRevisionSunatFecha())
                    .addValue("validacion", dto.getValidacion())
                    .addValue("validacionUsuario", dto.getValidacionUsuario())
                    .addValue("validacionFecha", dto.getValidacionFecha())
                    .addValue("validacionHora", dto.getValidacionHora())
                    .addValue("confirmacion", dto.getConfirmacion())
                    .addValue("confirmacionUsuario", dto.getConfirmacionUsuario())
                    .addValue("confirmacionFecha", dto.getConfirmacionFecha())
                    .addValue("confirmacionHora", dto.getConfirmacionHora())
                    .addValue("seeVentas", dto.getSeeVentas())
                    .addValue("extVentas", dto.getExtVentas())
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

    private LoginVentaListDTO mapRowToListDTO(ResultSet rs, String anio, String mes) throws SQLException {
        LoginVentaListDTO dto = new LoginVentaListDTO();

        // 1. Datos del Cliente
        dto.setRuc(rs.getString("ruc"));
        dto.setY(rs.getString("y"));
        dto.setRazonSocial(rs.getString("razonSocial"));
        dto.setNombreCorto(rs.getString("nombreCorto"));

        // 2. Clasificaciones y Grupos
        int estadoId = rs.getInt("idEstado");
        dto.setEstadoId(estadoId);
        dto.setEstadoDescripcion(rs.getString("estado"));

        dto.setCategoriaStore(rs.getObject("categoriaStore", Integer.class));
        
        // Regimen Tributario
        java.util.List<String> rTs = new java.util.ArrayList<>();
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

        int grupoEconomicoId = rs.getInt("idGrupoEconomico");
        dto.setGrupoEconomicoId(grupoEconomicoId);
        dto.setDescGrupoEconomico(rs.getString("descGrupoEconomico"));

        dto.setTipoServicioAbr(rs.getString("tipoServicioAbr"));
        dto.setTipoServicioDesc(rs.getString("tipoServicio"));

        // 3. Sire Global (del Cliente)
        dto.setSunatSire(rs.getObject("sunatSire", Integer.class));
        dto.setExternoSire(rs.getObject("externoSire", Integer.class));
        dto.setGerenciaSire(rs.getObject("gerenciaSire", Integer.class));

        java.util.List<String> sireList = new java.util.ArrayList<>();
        if (dto.getSunatSire() != null && dto.getSunatSire() != 0) sireList.add("SEE");
        if (dto.getExternoSire() != null && dto.getExternoSire() != 0) sireList.add("EXT");
        if (dto.getGerenciaSire() != null && dto.getGerenciaSire() != 0) sireList.add("GCOM");
        dto.setSireGlobal(String.join(", ", sireList));

        // 4. Datos de loginVentas
        if (hasColumn(rs, "responsable")) dto.setResponsable(rs.getString("responsable"));
        if (hasColumn(rs, "registro")) {
            dto.setRegistro(rs.getObject("registro", Integer.class));
            dto.setRegistroUsuario(rs.getString("registroUsuario"));
            dto.setRegistroFecha(rs.getString("registroFecha"));
        }

        if (hasColumn(rs, "revisionSunat")) {
            dto.setRevisionSunat(rs.getObject("revisionSunat", Integer.class));
            dto.setRevisionSunatUsuario(rs.getString("revisionSunatUsuario"));
            dto.setRevisionSunatFecha(rs.getString("revisionSunatFecha"));
        }

        if (hasColumn(rs, "validacion")) {
            dto.setValidacion(rs.getObject("validacion", Integer.class));
            dto.setValidacionUsuario(rs.getString("validacionUsuario"));
            dto.setValidacionFecha(rs.getString("validacionFecha"));
        }

        if (hasColumn(rs, "confirmacion")) {
            dto.setConfirmacion(rs.getObject("confirmacion", Integer.class));
            dto.setConfirmacionUsuario(rs.getString("confirmacionUsuario"));
            dto.setConfirmacionFecha(rs.getString("confirmacionFecha"));
        }

        if (hasColumn(rs, "seeVentas")) dto.setSeeVentas(rs.getObject("seeVentas", Integer.class));
        if (hasColumn(rs, "extVentas")) dto.setExtVentas(rs.getObject("extVentas", Integer.class));

        // 5. Calculados
        dto.setAnio(anio);
        dto.setMes(mes);
        
        dto.setFechaVencimiento(rs.getString("fechaVencimiento"));

        dto.setMovimiento(rs.getString("movimiento"));
        dto.setDescResponsable(rs.getString("descResponsable"));

        int cFilas = rs.getObject("comprasFilas", Integer.class) != null ? rs.getObject("comprasFilas", Integer.class) : 0;
        dto.setComprasFilas(cFilas);
        
        String fPle = rs.getString("fPle");
        dto.setPle((fPle == null || fPle.isEmpty()) ? "N/A" : "PLE");
        
        if (hasColumn(rs, "observacion")) dto.setObservacion(rs.getString("observacion"));
        dto.setPrioridad(rs.getString("prioridad"));

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
