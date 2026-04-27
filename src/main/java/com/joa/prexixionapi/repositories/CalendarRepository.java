package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.CalendarEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class CalendarRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<CalendarEventDTO> findCronogramaPDT() {
        String sql = "SELECT titulo, fecha FROM ( " +
                "SELECT 'DJ PDT RUC 0' AS titulo, [fecha0] AS fecha FROM cronogramaPDT UNION ALL " +
                "SELECT 'DJ PDT RUC 1' AS titulo, [fecha1] AS fecha FROM cronogramaPDT UNION ALL " +
                "SELECT 'DJ PDT RUC 2 - 3' AS titulo, [fecha2] AS fecha FROM cronogramaPDT UNION ALL " +
                "SELECT 'DJ PDT RUC 4 - 5' AS titulo, [fecha4] AS fecha FROM cronogramaPDT UNION ALL " +
                "SELECT 'DJ PDT RUC 6 - 7' AS titulo, [fecha6] AS fecha FROM cronogramaPDT UNION ALL " +
                "SELECT 'DJ PDT RUC 8 - 9' AS titulo, [fecha8] AS fecha FROM cronogramaPDT UNION ALL " +
                "SELECT 'DJ PDT BUCS' AS titulo, [fechab] AS fecha FROM cronogramaPDT" +
                ") AS fechas ORDER BY fecha;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("pdt")
                .build());
    }

    public List<CalendarEventDTO> findCronogramaSire() {
        String sql = "SELECT titulo, fecha FROM ( " +
                "SELECT 'DJ SIRE RUC 0' AS titulo, [fecha0] AS fecha FROM cronogramaSire UNION ALL " +
                "SELECT 'DJ SIRE RUC 1' AS titulo, [fecha1] AS fecha FROM cronogramaSire UNION ALL " +
                "SELECT 'DJ SIRE RUC 2 - 3' AS titulo, [fecha2] AS fecha FROM cronogramaSire UNION ALL " +
                "SELECT 'DJ SIRE RUC 4 - 5' AS titulo, [fecha4] AS fecha FROM cronogramaSire UNION ALL " +
                "SELECT 'DJ SIRE RUC 6 - 7' AS titulo, [fecha6] AS fecha FROM cronogramaSire UNION ALL " +
                "SELECT 'DJ SIRE RUC 8 - 9' AS titulo, [fecha8] AS fecha FROM cronogramaSire" +
                ") AS fechas ORDER BY fecha;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("sire")
                .build());
    }

    public List<CalendarEventDTO> findCronogramaPLE() {
        String sql = "SELECT CASE " +
                "	WHEN mes = '01' THEN 'DJ PRICO ENE' " +
                "	WHEN mes = '02' THEN 'DJ PRICO FEB' " +
                "	WHEN mes = '03' THEN 'DJ PRICO MAR' " +
                "	WHEN mes = '04' THEN 'DJ PRICO ABR' " +
                "	WHEN mes = '05' THEN 'DJ PRICO MAY' " +
                "	WHEN mes = '06' THEN 'DJ PRICO JUN' " +
                "	WHEN mes = '07' THEN 'DJ PRICO JUL' " +
                "	WHEN mes = '08' THEN 'DJ PRICO AGO' " +
                "	WHEN mes = '09' THEN 'DJ PRICO SEP' " +
                "	WHEN mes = '10' THEN 'DJ PRICO OCT' " +
                "	WHEN mes = '11' THEN 'DJ PRICO NOV' " +
                "	WHEN mes = '12' THEN 'DJ PRICO DIC' " +
                "END AS titulo, fecha FROM cronogramaPLE_Diario ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("ple")
                .build());
    }

    public List<CalendarEventDTO> findCronogramaAFP() {
        String sql = "SELECT CASE " +
                "	WHEN mes = '01' THEN 'DJ AFP ENE' " +
                "	WHEN mes = '02' THEN 'DJ AFP FEB' " +
                "	WHEN mes = '03' THEN 'DJ AFP MAR' " +
                "	WHEN mes = '04' THEN 'DJ AFP ABR' " +
                "	WHEN mes = '05' THEN 'DJ AFP MAY' " +
                "	WHEN mes = '06' THEN 'DJ AFP JUN' " +
                "	WHEN mes = '07' THEN 'DJ AFP JUL' " +
                "	WHEN mes = '08' THEN 'DJ AFP AGO' " +
                "	WHEN mes = '09' THEN 'DJ AFP SEP' " +
                "	WHEN mes = '10' THEN 'DJ AFP OCT' " +
                "	WHEN mes = '11' THEN 'DJ AFP NOV' " +
                "	WHEN mes = '12' THEN 'DJ AFP DIC' " +
                "END AS titulo, fecha FROM cronogramaAfp ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("afp")
                .build());
    }

    public List<CalendarEventDTO> findCronogramaDetracciones() {
        String sql = "SELECT CASE " +
                "	WHEN mes = '01' THEN 'DJ DETRAC ENE' " +
                "	WHEN mes = '02' THEN 'DJ DETRAC FEB' " +
                "	WHEN mes = '03' THEN 'DJ DETRAC MAR' " +
                "	WHEN mes = '04' THEN 'DJ DETRAC ABR' " +
                "	WHEN mes = '05' THEN 'DJ DETRAC MAY' " +
                "	WHEN mes = '06' THEN 'DJ DETRAC JUN' " +
                "	WHEN mes = '07' THEN 'DJ DETRAC JUL' " +
                "	WHEN mes = '08' THEN 'DJ DETRAC AGO' " +
                "	WHEN mes = '09' THEN 'DJ DETRAC SEP' " +
                "	WHEN mes = '10' THEN 'DJ DETRAC OCT' " +
                "	WHEN mes = '11' THEN 'DJ DETRAC NOV' " +
                "	WHEN mes = '12' THEN 'DJ DETRAC DIC' " +
                "END AS titulo, fecha FROM cronogramaDetracciones ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("detracciones")
                .build());
    }

    public List<CalendarEventDTO> findCronogramaAnual() {
        String sql = "SELECT titulo, fecha FROM ( " +
                "SELECT 'DJ ANUAL RUC 0' AS titulo, [fecha0] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 1' AS titulo, [fecha1] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 2' AS titulo, [fecha2] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 3' AS titulo, [fecha3] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 4' AS titulo, [fecha4] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 5' AS titulo, [fecha5] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 6' AS titulo, [fecha6] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 7' AS titulo, [fecha7] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 8' AS titulo, [fecha8] AS fecha FROM cronogramaAnual UNION ALL " +
                "SELECT 'DJ ANUAL RUC 9' AS titulo, [fecha9] AS fecha FROM cronogramaAnual" +
                ") AS fechas ORDER BY fecha;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("anual")
                .build());
    }

    public List<CalendarEventDTO> findFeriados() {
        String sql = "SELECT titulo, fecha FROM cronogramaFeriados ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("feriados")
                .build());
    }

    public List<CalendarEventDTO> findMisa() {
        String sql = "SELECT 'MISA MENSUAL' as titulo, m.* FROM cronogramaMisaMensual m ORDER BY m.fecha, m.horaI asc ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String fecha = rs.getString("fecha");
            String horaI = rs.getString("horaI");
            String horaF = rs.getString("horaF");
            return CalendarEventDTO.builder()
                    .title(rs.getString("titulo"))
                    .start(fecha + "T" + horaI + ":00")
                    .end(fecha + "T" + horaF + ":00")
                    .type("misa")
                    .build();
        });
    }

    public List<CalendarEventDTO> findDiasFestivos() {
        String sql = "SELECT titulo, fecha FROM cronogramaDiasFestivos ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .allDay(true)
                .type("diasFestivos")
                .build());
    }

    public List<CalendarEventDTO> findTramitesSunat() {
        String sql = "SELECT CONCAT('TS ', CASE " +
                "WHEN r.idInstitucion = 1 THEN '-' " +
                "WHEN r.idInstitucion = 2 THEN 'AFP' " +
                "WHEN r.idInstitucion = 3 THEN 'ESSAL' " +
                "WHEN r.idInstitucion = 4 THEN 'MINTRA' " +
                "WHEN r.idInstitucion = 5 THEN 'SIS' " +
                "WHEN r.idInstitucion = 6 THEN 'SUNAF' " +
                "WHEN r.idInstitucion = 7 THEN 'SUNAT' " +
                "WHEN r.idInstitucion = 1002 THEN 'TRIB F.' END, ' | ', " +
                "CASE WHEN ISNULL(cli.nombreCorto, '') <> '' THEN cli.nombreCorto ELSE cli.razonSocial END) AS titulo, " +
                "r.fTentativa, rm.descripcion as descMotivo, r.idEstadoGeneral " +
                "FROM reclamos r " +
                "LEFT JOIN cliente cli ON r.ruc = cli.ruc " +
                "LEFT JOIN reclamosMotivos rm ON r.idMotivo = rm.id " +
                "WHERE r.fTentativa is not null";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fTentativa"))
                .color("#782219")
                .topic(rs.getString("descMotivo"))
                .stateTramitesSunat(rs.getInt("idEstadoGeneral"))
                .type("tramitesSunat")
                .build());
    }

    public List<CalendarEventDTO> findCumpleanos(String dni) {
        Map<String, Object> personalInfo;
        try {
            personalInfo = jdbcTemplate.queryForMap(
                "SELECT idPuesto, idSubarea FROM personal WHERE dni = ?", dni);
        } catch (Exception e) {
            return Collections.emptyList();
        }
        int puesto = (Integer) personalInfo.get("idPuesto");
        int subArea = (Integer) personalInfo.get("idSubarea");

        String sql;
        if ((subArea == 1 && puesto == 2) || (subArea == 10 && puesto == 5)) {
            sql = "SELECT dni, nombre, apellido, fNacimiento, " +
                  "CASE WHEN SUM(CASE WHEN flag = 'G' THEN 1 ELSE 0 END) > 0 THEN 'G' ELSE 'S' END AS flag " +
                  "FROM ( " +
                  "SELECT dni, SUBSTRING(p.apellidos, 1, CASE WHEN CHARINDEX(' ', p.apellidos)-1 < 0 THEN LEN(p.apellidos) ELSE CHARINDEX(' ', p.apellidos)-1 END) as apellido, " +
                  "SUBSTRING(p.nombres, 1, CASE WHEN CHARINDEX(' ', p.nombres)-1 < 0 THEN LEN(p.nombres) ELSE CHARINDEX(' ', p.nombres)-1 END) as nombre, " +
                  "fNacimiento, 'G' as flag FROM personal p WHERE idEstado = 2 AND fNacimiento != '' " +
                  "UNION " +
                  "SELECT cp.plDni as dni, " +
                  "SUBSTRING(cp.plApellido, 1, CASE WHEN CHARINDEX(' ', cp.plApellido)-1 < 0 THEN LEN(cp.plApellido) ELSE CHARINDEX(' ', cp.plApellido)-1 END) as apellido, " +
                  "SUBSTRING(cp.plNombre, 1, CASE WHEN CHARINDEX(' ', cp.plNombre)-1 < 0 THEN LEN(cp.plNombre) ELSE CHARINDEX(' ', cp.plNombre)-1 END) as nombre, " +
                  "cp.plFNacimiento as fNacimiento, 'S' as flag " +
                  "FROM clientePersonal cp " +
                  "LEFT JOIN cliente c on cp.idCliente = c.ruc " +
                  "WHERE c.idEstado in (1,3,5,6,7) AND cp.plFNacimiento != '' " +
                  ") Z GROUP BY dni, apellido, nombre, fNacimiento ORDER BY dni ";
        } else {
            sql = "SELECT dni, " +
                  "SUBSTRING(p.apellidos, 1, CASE WHEN CHARINDEX(' ', p.apellidos)-1 < 0 THEN LEN(p.apellidos) ELSE CHARINDEX(' ', p.apellidos)-1 END) as apellido, " +
                  "SUBSTRING(p.nombres, 1, CASE WHEN CHARINDEX(' ', p.nombres)-1 < 0 THEN LEN(p.nombres) ELSE CHARINDEX(' ', p.nombres)-1 END) as nombre, " +
                  "fNacimiento, 'G' as flag " +
                  "FROM personal p WHERE idEstado = 2 AND fNacimiento != '' ORDER BY dni ";
        }

        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("flag") + " - " + rs.getString("nombre") + " " + rs.getString("apellido"))
                .start(rs.getString("fNacimiento"))
                .flagReunion(rs.getString("flag")) // re-using flagReunion for flag to minimize DTO size
                .allDay(true)
                .type("cumpleanios")
                .build());
    }

    public List<CalendarEventDTO> findObligaciones(String dni) {
        Map<String, Object> personalInfo;
        try {
            personalInfo = jdbcTemplate.queryForMap(
                "SELECT idPuesto, idArea FROM personal WHERE dni = ?", dni);
        } catch (Exception e) {
            return Collections.emptyList();
        }
        int idPuesto = (Integer) personalInfo.get("idPuesto");
        int idArea = (Integer) personalInfo.get("idArea");

        String sql = "SELECT titulo, fecha, color, area FROM ( " +
                "SELECT titulos.titulo, titulos.fecha, '#041562' AS color, 8 AS area FROM cronogramaPDT CROSS APPLY ( VALUES ('DJ PDT RUC 0', fecha0), ('DJ PDT RUC 1', fecha1), ('DJ PDT RUC 2 - 3', fecha2), ('DJ PDT RUC 4 - 5', fecha4), ('DJ PDT RUC 6 - 7', fecha6), ('DJ PDT RUC 8 - 9', fecha8), ('DJ PDT BUCS', fechab) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#11468F' AS color, 8 AS area FROM cronogramaSire CROSS APPLY ( VALUES ('DJ SIRE RUC 0', fecha0), ('DJ SIRE RUC 1', fecha1), ('DJ SIRE RUC 2 - 3', fecha2), ('DJ SIRE RUC 4 - 5', fecha4), ('DJ SIRE RUC 6 - 7', fecha6), ('DJ SIRE RUC 8 - 9', fecha8) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT 'DJ PRICO ' + CASE mes WHEN '01' THEN 'ENE' WHEN '02' THEN 'FEB' WHEN '03' THEN 'MAR' WHEN '04' THEN 'ABR' WHEN '05' THEN 'MAY' WHEN '06' THEN 'JUN' WHEN '07' THEN 'JUL' WHEN '08' THEN 'AGO' WHEN '09' THEN 'SEP' WHEN '10' THEN 'OCT' WHEN '11' THEN 'NOV' WHEN '12' THEN 'DIC' END AS titulo, fecha, '#1230AE' AS color, 8 AS area FROM cronogramaPLE_Diario UNION ALL " +
                "SELECT 'DJ AFP ' + CASE mes WHEN '01' THEN 'ENE' WHEN '02' THEN 'FEB' WHEN '03' THEN 'MAR' WHEN '04' THEN 'ABR' WHEN '05' THEN 'MAY' WHEN '06' THEN 'JUN' WHEN '07' THEN 'JUL' WHEN '08' THEN 'AGO' WHEN '09' THEN 'SEP' WHEN '10' THEN 'OCT' WHEN '11' THEN 'NOV' WHEN '12' THEN 'DIC' END AS titulo, fecha, '#e355e1' AS color, 4 AS area FROM cronogramaAfp UNION ALL " +
                "SELECT 'CTS ' + CASE mes WHEN '05' THEN 'MAY' WHEN '11' THEN 'NOV' END AS titulo, fecha, '#725CAD' AS color, 4 AS area FROM cronogramaCts UNION ALL " +
                "SELECT 'GRATIFICACION ' + CASE mes WHEN '07' THEN 'JUL' WHEN '12' THEN 'DIC' END AS titulo, fecha, '#7E8EF1' AS color, 4 AS area FROM cronogramaGratificacion UNION ALL " +
                "SELECT 'DJ DETRAC ' + CASE mes WHEN '01' THEN 'ENE' WHEN '02' THEN 'FEB' WHEN '03' THEN 'MAR' WHEN '04' THEN 'ABR' WHEN '05' THEN 'MAY' WHEN '06' THEN 'JUN' WHEN '07' THEN 'JUL' WHEN '08' THEN 'AGO' WHEN '09' THEN 'SEP' WHEN '10' THEN 'OCT' WHEN '11' THEN 'NOV' WHEN '12' THEN 'DIC' END AS titulo, fecha, '#800080' AS color, 8 AS area FROM cronogramaDetracciones UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#33b2ff' AS color, 8 AS area FROM cronogramaAnual CROSS APPLY ( VALUES ('DJ ANUAL RUC 0', fecha0), ('DJ ANUAL RUC 1', fecha1), ('DJ ANUAL RUC 2', fecha2), ('DJ ANUAL RUC 3', fecha3), ('DJ ANUAL RUC 4', fecha4), ('DJ ANUAL RUC 5', fecha5), ('DJ ANUAL RUC 6', fecha6), ('DJ ANUAL RUC 7', fecha7), ('DJ ANUAL RUC 8', fecha8), ('DJ ANUAL RUC 9', fecha9) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#F49BAB' AS color, 8 AS area FROM cronogramaReporteLocal CROSS APPLY ( VALUES ('DJ REP LOCAL RUC 0', fecha0), ('DJ REP LOCAL RUC 1', fecha1), ('DJ REP LOCAL RUC 2 - 3', fecha2), ('DJ REP LOCAL RUC 4 - 5', fecha4), ('DJ REP LOCAL RUC 6 - 7', fecha6), ('DJ REP LOCAL RUC 8 - 9', fecha8), ('DJ REP LOCAL BUCS', fechab) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#483AA0' AS color, 8 AS area FROM cronogramaDaot CROSS APPLY ( VALUES ('DJ DAOT RUC 0', fecha0), ('DJ DAOT RUC 1', fecha1), ('DJ DAOT RUC 2 - 3', fecha2), ('DJ DAOT RUC 4 - 5', fecha4), ('DJ DAOT RUC 6 - 7', fecha6), ('DJ DAOT RUC 8 - 9', fecha8) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#129990' AS color, 8 AS area FROM cronogramaDonaciones CROSS APPLY ( VALUES ('DJ DONAC RUC 0', fecha0), ('DJ DONAC RUC 1', fecha1), ('DJ DONAC RUC 2 - 3', fecha2), ('DJ DONAC RUC 4 - 5', fecha4), ('DJ DONAC RUC 6 - 7', fecha6), ('DJ DONAC RUC 8 - 9', fecha8) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#4A102A' AS color, 8 AS area FROM cronogramaRfEcr CROSS APPLY ( VALUES ('DJ RF ECR RUC 0', fecha0), ('DJ RF ECR RUC 1', fecha1), ('DJ RF ECR RUC 2 - 3', fecha2), ('DJ RF ECR RUC 4 - 5', fecha4), ('DJ RF ECR RUC 6 - 7', fecha6), ('DJ RF ECR RUC 8 - 9', fecha8) ) AS titulos(titulo, fecha) UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#C95792' AS color, 8 AS area FROM cronogramaPDT CROSS APPLY ( VALUES ('DJ PDT 648 0', fecha0), ('DJ PDT 648 1', fecha1), ('DJ PDT 648 2 - 3', fecha2), ('DJ PDT 648 4 - 5', fecha4), ('DJ PDT 648 6 - 7', fecha6), ('DJ PDT 648 8 - 9', fecha8) ) AS titulos(titulo, fecha) WHERE mes = '03' AND titulos.fecha IS NOT NULL UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#88304E' AS color, 8 AS area FROM cronogramaPDT CROSS APPLY ( VALUES ('DJ B. FINAL 0', fecha0), ('DJ B. FINAL 1', fecha1), ('DJ B. FINAL 2 - 3', fecha2), ('DJ B. FINAL 4 - 5', fecha4), ('DJ B. FINAL 6 - 7', fecha6), ('DJ B. FINAL 8 - 9', fecha8) ) AS titulos(titulo, fecha) WHERE mes IN ('10','12') AND titulos.fecha IS NOT NULL UNION ALL " +
                "SELECT titulos.titulo, titulos.fecha, '#5F8B4C' AS color, 8 AS area FROM cronogramaPDT CROSS APPLY ( VALUES ('DJ IF SSERIF 0', fecha0), ('DJ IF SSERIF 1', fecha1), ('DJ IF SSERIF 2 - 3', fecha2), ('DJ IF SSERIF 4 - 5', fecha4), ('DJ IF SSERIF 6 - 7', fecha6), ('DJ IF SSERIF 8 - 9', fecha8) ) AS titulos(titulo, fecha) WHERE mes IN ('06','12') AND titulos.fecha IS NOT NULL UNION ALL " +
                "SELECT 'DEV ISC ' + CASE mes WHEN '03' THEN 'MAR' WHEN '06' THEN 'JUN' WHEN '09' THEN 'SEP' WHEN '12' THEN 'DIC' END AS titulo, fecha, '#640D5F' AS color, 8 AS area FROM cronogramaDevolucionesISC UNION ALL " +
                "SELECT 'DEV LIB. DEO ' + CASE mes WHEN '01' THEN 'ENE' WHEN '04' THEN 'ABR' WHEN '07' THEN 'JUL' WHEN '10' THEN 'OCT' END AS titulo, fecha, '#493D9E' AS color, 8 AS area FROM cronogramaDevolucionesLiberacionDEO " +
                ") AS calendario WHERE fecha IS NOT NULL ";

        if (idPuesto == 3) {
            if (idArea == 2) {
                sql += "AND (area in (2,4) OR color = '#041562') ";
            } else if (idArea == 4) {
                sql += "AND (area = " + idArea + " OR color = '#041562') ";
            } else {
                sql += "AND area = " + idArea + " ";
            }
        } else if (idPuesto == 1 || idPuesto == 4 || idPuesto == 5 || idPuesto == 6 || idPuesto == 7 || idPuesto == 8 || idPuesto == 9 || idPuesto == 10) {
            if (idArea == 4) {
                sql += "AND (area = " + idArea + " OR color = '#041562') ";
            } else {
                sql += "AND area = " + idArea + " ";
            }
        }
        sql += "ORDER BY fecha ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fecha"))
                .color(rs.getString("color"))
                .type("obligaciones")
                .build());
    }

    public List<CalendarEventDTO> findFiscalizacionesPay(String dni) {
        Map<String, Object> personalInfo;
        try {
            personalInfo = jdbcTemplate.queryForMap(
                "SELECT idPuesto, idArea FROM personal WHERE dni = ?", dni);
        } catch (Exception e) {
            return Collections.emptyList();
        }
        int idPuesto = (Integer) personalInfo.get("idPuesto");
        int idArea = (Integer) personalInfo.get("idArea");

        String sql = "WITH base AS ( SELECT fl.idFLEntidad, fl.idFLDocumento, fl.idFLEstado, fl.fLimiteRespuesta, 4 as area, " +
                "CASE WHEN ISNULL(cli.nombreCorto, '') <> '' THEN cli.nombreCorto ELSE cli.razonSocial END AS nombreEmpresa, " +
                "ROW_NUMBER() OVER ( PARTITION BY fl.idFLEntidad, fl.idFLDocumento, fl.idFLEstado, fl.fLimiteRespuesta ORDER BY cli.nombreCorto ) AS rn, " +
                "COUNT(*) OVER ( PARTITION BY fl.idFLEntidad, fl.idFLDocumento, fl.idFLEstado, fl.fLimiteRespuesta ) AS totalEmpresas " +
                "FROM fiscalizacionesLaborales fl INNER JOIN cliente cli ON fl.idCliente = cli.ruc WHERE fl.idFLEstado IN (2, 5, 10) ), " +
                "eventosPrincipales AS ( SELECT idFLEntidad, idFLDocumento, idFLEstado, fLimiteRespuesta, area, nombreEmpresa, rn, totalEmpresas FROM base WHERE rn <= 5 ), " +
                "eventosExtras AS ( SELECT idFLEntidad, idFLDocumento, idFLEstado, fLimiteRespuesta, area, NULL AS nombreEmpresa, 6 AS rn, totalEmpresas FROM base WHERE rn = 1 AND totalEmpresas > 5 ) " +
                "SELECT CONCAT( 'FP ', CASE WHEN e.idFLEntidad = 1 THEN 'SUNAF' WHEN e.idFLEntidad = 2 THEN 'SUNAT' WHEN e.idFLEntidad = 3 THEN 'MINTRA' WHEN e.idFLEntidad = 4 THEN 'CONAF' END, ' | ', " +
                "CASE WHEN e.idFLDocumento = 1 THEN 'REQ. INF.' WHEN e.idFLDocumento = 2 THEN 'COMUNI.' WHEN e.idFLDocumento = 3 THEN 'COMPAR.' WHEN e.idFLDocumento = 4 THEN 'CARTA IND.' WHEN e.idFLDocumento = 5 THEN 'ESQUELA' WHEN e.idFLDocumento = 6 THEN 'CARTA DIS.' WHEN e.idFLDocumento = 7 THEN 'ORD. INSP.' WHEN e.idFLDocumento = 8 THEN 'FORMAL.' END, '   ', " +
                "CASE WHEN e.rn <= 5 THEN e.nombreEmpresa ELSE CONCAT('+ ', e.totalEmpresas - 5, ' más') END ) AS titulo, " +
                "fLimiteRespuesta, '08:00' AS hora, area, idFLEstado FROM ( SELECT * FROM eventosPrincipales UNION ALL SELECT * FROM eventosExtras ) AS e ";

        if (idPuesto == 3) {
            if (idArea == 2) {
                sql += "WHERE e.area in (2,4) ";
            } else {
                sql += "WHERE e.area = " + idArea + " ";
            }
        } else if (idPuesto == 1 || idPuesto == 4 || idPuesto == 5 || idPuesto == 6 || idPuesto == 7 || idPuesto == 8 || idPuesto == 9 || idPuesto == 10) {
            sql += "WHERE e.area = " + idArea + " ";
        }
        sql += "ORDER BY fLimiteRespuesta, titulo; ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> CalendarEventDTO.builder()
                .title(rs.getString("titulo"))
                .start(rs.getString("fLimiteRespuesta") + "T" + rs.getString("hora") + ":00")
                .stateFiscalizacionPay(rs.getInt("idFLEstado"))
                .type("fiscalizacionPay")
                .build());
    }

    public List<CalendarEventDTO> findReuniones() {
        String sql = "SELECT r.idCliente, " +
                "CASE WHEN EXISTS (SELECT aux.ruc FROM cliente aux WHERE r.idCliente = aux.ruc) " +
                "		THEN " +
                "			CASE WHEN (SELECT aux.nombreCorto FROM cliente aux WHERE r.idCliente = aux.ruc) != '' " +
                "					THEN (SELECT aux.nombreCorto FROM cliente aux WHERE r.idCliente = aux.ruc) " +
                "					ELSE r.razonSocial " +
                "			END " +
                "		ELSE r.razonSocial " +
                "END razonSocial, " +
                "r.id, r.tipo, r.fecha, r.horaI, r.horaF, r.idEstado, re.descripcion as estadoDescripcion, r.otros, " +
                "(SELECT TOP 1 tema FROM reunionesTemas WHERE idReunion = r.id) as tema, " +
                "(SELECT TOP 1 " +
                "CASE " +
                "	WHEN idArea = 1 THEN '-' " +
                "	WHEN idArea = 2 THEN 'FIR' " +
                "	WHEN idArea = 3 THEN 'HUB' " +
                "	WHEN idArea = 4 THEN 'PAY' " +
                "	WHEN idArea = 7 THEN 'UP' " +
                "	WHEN idArea = 8 THEN 'BE' " +
                "END AS area " +
                "FROM reunionesAreas WHERE idReunion = r.id) as area, " +
                "CONCAT( " +
                "        (SELECT TOP 1 " +
                "            SUBSTRING(nombres, 1, " +
                "                CASE " +
                "                    WHEN CHARINDEX(' ', nombres) - 1 < 0 " +
                "                    THEN LEN(nombres) " +
                "                    ELSE CHARINDEX(' ', nombres) - 1 " +
                "                END " +
                "            ) " +
                "         FROM personal " +
                "         WHERE dni IN (SELECT dni FROM reunionesParticipantesInternos WHERE idReunion = r.id) " +
                "        ), ' ', " +
                "        (SELECT TOP 1 " +
                "            SUBSTRING(apellidos, 1, " +
                "                CASE " +
                "                    WHEN CHARINDEX(' ', apellidos) - 1 < 0  " +
                "                    THEN LEN(apellidos) " +
                "                    ELSE CHARINDEX(' ', apellidos) - 1 " +
                "                END " +
                "            ) " +
                "         FROM personal " +
                "         WHERE dni IN (SELECT dni FROM reunionesParticipantesInternos WHERE idReunion = r.id) " +
                "        ) " +
                "    ) as gear " +
                "FROM reuniones r " +
                "LEFT JOIN reunionesEstados re ON r.idEstado = re.id " +
                "ORDER BY r.fecha, r.horaI asc ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String fecha = rs.getString("fecha");
            String horaI = rs.getString("horaI");
            String horaF = rs.getString("horaF");
            String start = fecha + "T" + horaI + ":00";
            String end = fecha + "T" + horaF + ":00";

            return CalendarEventDTO.builder()
                    .title(rs.getString("area") + " | " + rs.getString("razonSocial"))
                    .start(start)
                    .end(end)
                    .attendee(rs.getString("gear"))
                    .topic(rs.getString("tema"))
                    .state(rs.getInt("idEstado"))
                    .stateDescripcion(rs.getString("estadoDescripcion"))
                    .flagReunion(rs.getString("tipo"))
                    .type("reunion")
                    .build();
        });
    }

    public List<CalendarEventDTO> findFiscalizaciones(String dni) {
        Map<String, Object> personalInfo;
        try {
            personalInfo = jdbcTemplate.queryForMap(
                "SELECT idPuesto, idArea FROM personal WHERE dni = ?", dni);
        } catch (Exception e) {
            return Collections.emptyList();
        }
        int idPuesto = (Integer) personalInfo.get("idPuesto");
        int idArea = (Integer) personalInfo.get("idArea");

        String sql = "SELECT x.ruc, x.razonSocial, x.idModo, x.modo, x.fPresentacion, x.hora, x.idEstado, x.estadoDescripcion, x.gear, x.area " +
                "FROM " +
                "( SELECT f.ruc, " +
                "CASE WHEN (SELECT aux.nombreCorto FROM cliente aux WHERE f.ruc = aux.ruc) != '' " +
                "	THEN CONCAT ('FT SUNAT | ', (SELECT aux.nombreCorto FROM cliente aux WHERE f.ruc = aux.ruc)) " +
                "	ELSE CONCAT ('FT SUNAT | ', c.razonSocial) " +
                "END razonSocial, " +
                "f.idModo, fm.descripcion AS modo, " +
                "f.fPresentacion, " +
                "CASE WHEN f.hora != '' THEN f.hora ELSE '08:00' " +
                "END hora, " +
                "f.idEstado, fe.descripcion as estadoDescripcion, " +
                "CONCAT( " +
                "        (SELECT TOP 1 " +
                "            SUBSTRING(nombres, 1, " +
                "                CASE " +
                "                    WHEN CHARINDEX(' ', nombres) - 1 < 0 " +
                "                    THEN LEN(nombres) " +
                "                    ELSE CHARINDEX(' ', nombres) - 1 " +
                "                END " +
                "            ) " +
                "         FROM personal " +
                "         WHERE dni = f.responsableDni " +
                "        ), ' ', " +
                "        (SELECT TOP 1 " +
                "            SUBSTRING(apellidos, 1, " +
                "                CASE " +
                "                    WHEN CHARINDEX(' ', apellidos) - 1 < 0  " +
                "                    THEN LEN(apellidos) " +
                "                    ELSE CHARINDEX(' ', apellidos) - 1 " +
                "                END " +
                "            ) " +
                "         FROM personal " +
                "         WHERE dni = f.responsableDni " +
                "        ) " +
                "    ) as gear, " +
                "8 as area " +
                "FROM fiscalizaciones f " +
                "INNER JOIN fiscalizacionesEstados fe ON f.idEstado = fe.id " +
                "INNER JOIN cliente c ON f.ruc = c.ruc " +
                "LEFT JOIN fiscalizacionesModos fm on f.idModo = fm.id " +
                "LEFT JOIN personal p ON f.responsableDni = p.dni " +
                "WHERE f.fPresentacion != '' AND f.idEstado IN (2,3,5) ) as x ";

        if (idPuesto == 3) {
            if (idArea == 2) {
                sql += "WHERE x.area in (2,8) ";
            } else {
                sql += "WHERE x.area = " + idArea + " ";
            }
        } else if (idPuesto == 1 || idPuesto == 4 || idPuesto == 5 || idPuesto == 6 || idPuesto == 7 || idPuesto == 8 || idPuesto == 9 || idPuesto == 10) {
            sql += "WHERE x.area = " + idArea + " ";
        }
        sql += "ORDER BY x.fPresentacion, x.hora ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String fecha = rs.getString("fPresentacion");
            String horaI = rs.getString("hora");
            String start = fecha + "T" + horaI + ":00";
            
            return CalendarEventDTO.builder()
                    .title(rs.getString("razonSocial"))
                    .start(start)
                    .attendee(rs.getString("gear"))
                    .topic("FISCALIZACIÓN")
                    .stateFiscalizacion(rs.getInt("idEstado"))
                    .stateDescripcion(rs.getString("estadoDescripcion"))
                    .flagFiscalizacion(rs.getString("modo"))
                    .type("fiscalizacion")
                    .build();
        });
    }
}
