package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.entities.LoginVenta;
import com.joa.prexixionapi.entities.SignerNivel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import java.util.Arrays;
import java.util.stream.Collectors;

@Repository
public class LoginVentaRepository {

    @PersistenceContext
    private EntityManager em;

    private String getFVencimientoSql(String anio, String mes, String lastAnio) {
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
                + "     	            CASE WHEN (" + anio + mes + " >= 202002) AND (" + anio + mes + " <= 202008) THEN "
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
                + "     	            CASE WHEN (" + anio + mes + " >= 202002) AND (" + anio + mes + " <= 202008) THEN "
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

    private String getBaseFromAndWhere(String anio, String mes, String queryComprasFilasJoin) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        return " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN ( SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6 ) cso ON c.ruc = cso.idCliente "
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 "
                + " AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= '" + anio + "-" + mes + "-01' AND "
                + " ( CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= '" + anio + "-" + mes + "-01' OR si.stAnioHasta IS NULL ) "
                + " LEFT JOIN cronogramaPDT cr ON '" + anio + "' = cr.anio AND '" + mes + "' = cr.mes "
                + " LEFT JOIN cronogramaCovidPDT crC ON '" + anio + "' = crC.anio AND '" + mes + "' = crC.mes "
                + " LEFT JOIN cronogramaPricoPDT crP ON '" + anio + "' = crP.anio AND '" + mes + "' = crP.mes "
                + " LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = '" + anio + "' AND crPle.mes = '" + mes + "' "
                + " LEFT JOIN CRONOGRAMACOVIDPLE_A crPleC ON crPleC.anio = '" + anio + "' AND crPleC.mes = '" + mes + "' "
                + " LEFT JOIN cronogramaPricoPLE_A crPleP ON crPleP.anio = '" + anio + "' AND crPleP.mes = '" + mes + "'  "
                + " LEFT JOIN cronogramaSire crS ON '" + anio + "' = crS.anio AND '" + mes + "' = crS.mes "
                + " LEFT JOIN uit u ON u.anio = '" + lastAnio + "' "
                + " LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = '" + anio + "'  AND lp.mes = '" + mes + "' "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = '" + anio + "'  AND lv.mes = '" + mes + "' "
                + " LEFT JOIN personal per ON lv.responsable = per.dni "
                + queryComprasFilasJoin
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= '" + anio + "-" + mes + "-01' AND "
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= '" + anio + "-" + mes + "-01' OR p.stAnioHasta IS NULL ) ";
    }

    private String getBaseSelect(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        int anioMesInt = Integer.parseInt(anio + mes);

        String queryComprasFilasColumna = "";
        String queryComprasFilasJoin = "";

        if (anioMesInt < 202310) {
            queryComprasFilasColumna = " ple.comprasFilas, ";
            queryComprasFilasJoin = " LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = :anioAux AND ple.mes = :mesAux ";
        } else {
            queryComprasFilasColumna = " sd.comprasFilas, ";
            queryComprasFilasJoin = " LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anioAux AND sd.mes = :mesAux ";
        }

        return "SELECT c.idEstado, cE.descripcion AS estado, "
                + " CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore, "
                + " c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, "
                + " c.ruc, c.y, c.razonSocial, c.nombreCorto, "
                + " c.rTMypeTributario, c.rTRus, c.rTEspecial, c.rTGeneral, c.rTAmazonico, c.rTAgrario, c.rT1ra, c.rT2da, c.rT3ra, c.rT4ta, c.rT5ta, "
                + getFVencimientoSql(anio, mes, lastAnio) + " AS fVencimiento, "
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
                + getBaseFromAndWhere(anio, mes, queryComprasFilasJoin);
    }
    
    public String[] getVencimientoLimits(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);
        int anioMesInt = Integer.parseInt(anio + mes);
        String queryComprasFilasJoin = "";
        
        if (anioMesInt < 202310) {
            queryComprasFilasJoin = " LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = :anioAux AND ple.mes = :mesAux ";
        } else {
            queryComprasFilasJoin = " LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anioAux AND sd.mes = :mesAux ";
        }

        String sql = "SELECT MIN(v.fVencimiento), MAX(v.fVencimiento) FROM ( SELECT " 
                   + getFVencimientoSql(anio, mes, lastAnio) + " AS fVencimiento "
                   + getBaseFromAndWhere(anio, mes, queryComprasFilasJoin) + " ) v "
                   + " WHERE v.fVencimiento IS NOT NULL AND LEN(v.fVencimiento) > 0";
                   
        Query query = em.createNativeQuery(sql);
        
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

        query.setParameter("anioAux", anioAux);
        query.setParameter("mesAux", mesAux);
        
        Object[] result = (Object[]) query.getSingleResult();
        String min = result[0] != null ? result[0].toString() : "";
        String max = result[1] != null ? result[1].toString() : "";
        
        return new String[]{min, max};
    }

    @SuppressWarnings("unchecked")
    public List<Cliente> list(String anio, String mes, String estados, String grupos) {
        StringBuilder sql = new StringBuilder(getBaseSelect(anio, mes));

        if (estados != null && !estados.trim().isEmpty()) {
            sql.append(" AND c.idEstado IN (:estados) ");
        }

        if (grupos != null && !grupos.trim().isEmpty()) {
            sql.append(" AND c.y IN (:grupos) ");
        }

        sql.append(" ORDER BY c.y ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);

        // Calculate anioAux and mesAux
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

        query.setParameter("anioAux", anioAux);
        query.setParameter("mesAux", mesAux);

        if (estados != null && !estados.trim().isEmpty()) {
            query.setParameter("estados", parseCsvToIntList(estados));
        }

        if (grupos != null && !grupos.trim().isEmpty()) {
            query.setParameter("grupos", parseCsvToStringList(grupos));
        }

        List<Tuple> tuples = query.getResultList();
        return mapTuples(tuples, anio, mes);
    }

    @SuppressWarnings("unchecked")
    public Cliente getOne(String ruc, String anio, String mes) {
        StringBuilder sql = new StringBuilder(getBaseSelect(anio, mes));
        sql.append(" AND c.ruc = :ruc ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);

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

        query.setParameter("anioAux", anioAux);
        query.setParameter("mesAux", mesAux);
        query.setParameter("ruc", ruc);

        List<Tuple> tuples = query.getResultList();
        List<Cliente> clientes = mapTuples(tuples, anio, mes);
        return clientes.isEmpty() ? null : clientes.get(0);
    }

    private List<Integer> parseCsvToIntList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private List<String> parseCsvToStringList(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private String getStringSafely(Tuple tuple, String alias) {
        try {
            Object obj = tuple.get(alias);
            return obj != null ? obj.toString() : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Character getCharacterSafely(Tuple tuple, String alias) {
        try {
            Object obj = tuple.get(alias);
            if (obj == null)
                return null;
            if (obj instanceof Character)
                return (Character) obj;
            String s = obj.toString();
            return s.isEmpty() ? null : s.charAt(0);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer getIntegerSafely(Tuple tuple, String alias) {
        try {
            Object obj = tuple.get(alias);
            if (obj == null)
                return 0;
            if (obj instanceof Number) {
                return ((Number) obj).intValue();
            }
            try {
                return Integer.parseInt(obj.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    private List<Cliente> mapTuples(List<Tuple> tuples, String anio, String mes) {
        List<Cliente> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Cliente clie = new Cliente();
            clie.setRuc(getStringSafely(tuple, "ruc"));

            Character yChar = getCharacterSafely(tuple, "y");
            clie.setY(yChar != null ? yChar.toString() : null);

            clie.setRazonSocial(getStringSafely(tuple, "razonSocial"));
            clie.setNombreCorto(getStringSafely(tuple, "nombreCorto"));
            clie.setEstado(new Gclass(getIntegerSafely(tuple, "idEstado"), getStringSafely(tuple, "estado")));
            clie.setServicio(new Gclass(getIntegerSafely(tuple, "idTipoServicio"),
                    getStringSafely(tuple, "tipoServicioAbr"), getStringSafely(tuple, "tipoServicio")));
            clie.setGrupoEconomico(new Gclass(getIntegerSafely(tuple, "idGrupoEconomico"),
                    getStringSafely(tuple, "descGrupoEconomico")));

            SignerNivel sn = new SignerNivel();
            sn.setCategoriaStore(getIntegerSafely(tuple, "categoriaStore"));
            clie.setSignerNivel(sn);

            // Regimen Tributario
            List<String> rTs = new ArrayList<>();
            if (getIntegerSafely(tuple, "rT3ra") != 0) {
                if (getIntegerSafely(tuple, "rTMypeTributario") != 0)
                    rTs.add("RM");
                if (getIntegerSafely(tuple, "rTRus") != 0)
                    rTs.add("RUS");
                if (getIntegerSafely(tuple, "rTEspecial") != 0)
                    rTs.add("RE");
                if (getIntegerSafely(tuple, "rTGeneral") != 0)
                    rTs.add("RG");
                if (getIntegerSafely(tuple, "rTAmazonico") != 0)
                    rTs.add("RAM");
                if (getIntegerSafely(tuple, "rTAgrario") != 0)
                    rTs.add("RAG");
            } else if (getIntegerSafely(tuple, "rT1ra") != 0) {
                rTs.add("1ra");
            } else if (getIntegerSafely(tuple, "rT2da") != 0) {
                rTs.add("2da");
            } else if (getIntegerSafely(tuple, "rT4ta") != 0) {
                rTs.add("4ta");
            } else if (getIntegerSafely(tuple, "rT5ta") != 0) {
                rTs.add("5ta");
            }
            clie.setRegimenTributario(rTs.isEmpty() ? "" : rTs.get(rTs.size() - 1));

            // Login Venta properties
            LoginVenta lv = new LoginVenta();
            lv.setResponsable(getStringSafely(tuple, "responsable"));
            lv.setRegistro(getIntegerSafely(tuple, "registro"));
            lv.setRegistroUsuario(getStringSafely(tuple, "registroUsuario"));
            lv.setRegistroFecha(getStringSafely(tuple, "registroFecha"));
            lv.setRegistroHora(getStringSafely(tuple, "registroHora"));
            lv.setRevisionSunat(getIntegerSafely(tuple, "revisionSunat"));
            lv.setRevisionSunatUsuario(getStringSafely(tuple, "revisionSunatUsuario"));
            lv.setRevisionSunatFecha(getStringSafely(tuple, "revisionSunatFecha"));
            lv.setValidacion(getIntegerSafely(tuple, "validacion"));
            lv.setValidacionUsuario(getStringSafely(tuple, "validacionUsuario"));
            lv.setValidacionFecha(getStringSafely(tuple, "validacionFecha"));
            lv.setValidacionHora(getStringSafely(tuple, "validacionHora"));
            lv.setConfirmacion(getIntegerSafely(tuple, "confirmacion"));
            lv.setConfirmacionUsuario(getStringSafely(tuple, "confirmacionUsuario"));
            lv.setConfirmacionFecha(getStringSafely(tuple, "confirmacionFecha"));
            lv.setConfirmacionHora(getStringSafely(tuple, "confirmacionHora"));
            lv.setSeeVentas(getIntegerSafely(tuple, "seeVentas"));
            lv.setExtVentas(getIntegerSafely(tuple, "extVentas"));
            lv.setObservacion(getStringSafely(tuple, "observacion"));
            lv.setVersion(getIntegerSafely(tuple, "version"));
            // lv.setSire(getIntegerSafely(tuple, "sire"));

            Object fVencimientoObj = tuple.get("fVencimiento");
            lv.setfVencimiento(fVencimientoObj != null ? fVencimientoObj.toString() : null);
            if (lv.getfVencimiento() != null && !lv.getfVencimiento().isEmpty()) {
                String[] parts = lv.getfVencimiento().split("-");
                if (parts.length >= 3) {
                    lv.setDiaVencimiento(parts[2]);
                    lv.setMesVencimiento(parts[1]);
                }
                try {
                    java.time.LocalDate fVenc = java.time.LocalDate.parse(lv.getfVencimiento());
                    java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("America/Lima"));
                    lv.setDifference((int) java.time.temporal.ChronoUnit.DAYS.between(fVenc, today));
                } catch (Exception e) {
                    lv.setDifference(-999);
                }
            } else {
                lv.setDifference(-999);
            }
            lv.setAnio(anio);
            lv.setMes(mes);
            lv.setDescResponsable(getStringSafely(tuple, "descResponsable"));
            lv.setMovimiento(getStringSafely(tuple, "movimiento"));
            lv.setComprasFilas(getIntegerSafely(tuple, "comprasFilas"));
            Object modalidadObj = tuple.get("modalidad");
            lv.setModalidad(modalidadObj != null ? modalidadObj.toString() : null);

            lv.setPrioridad(getStringSafely(tuple, "prioridad"));
            String fPle = getStringSafely(tuple, "fPle");
            lv.setPle((fPle == null || fPle.isEmpty()) ? "N/A" : "PLE");

            clie.setSunatSire(getIntegerSafely(tuple, "sunatSire"));
            clie.setExternoSire(getIntegerSafely(tuple, "externoSire"));

            clie.setGerenciaSire(getIntegerSafely(tuple, "gerenciaSire"));

            List<String> sireList = new ArrayList<>();
            String sire = "";
            if (clie.getSunatSire() != 0)
                sireList.add("SEE");
            if (clie.getExternoSire() != 0)
                sireList.add("EXT");
            if (clie.getGerenciaSire() != 0)
                sireList.add("GCOM");
            for (String s : sireList) {
                sire += s + ", ";
            }
            if (sire.length() > 2) {
                sire = sire.substring(0, sire.length() - 2);
            }
            clie.setSire(sire);

            clie.setLoginVenta(lv);
            list.add(clie);
        }
        return list;
    }

    // --- DIARIO REPORT METHODS ---
    public int countTotal(String anio, String mes) {
        String query = "SELECT COUNT(*) AS total \n"
                + " FROM cliente c \n"
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND \n"
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL ) ";

        String startDateStr = anio + "-" + mes + "-01";

        Number count = (Number) em.createNativeQuery(query)
                .setParameter("startDate", startDateStr)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public int countAcumulado(String proceso, String fecha, String anio, String mes) {
        // Obtenemos el inicio de mes de la fecha indicada (ej. 2026-05-15 ->
        // 2026-05-01)
        java.time.LocalDate date = java.time.LocalDate.parse(fecha);
        String fechaI = date.withDayOfMonth(1).toString();
        // fechaF = fecha menos 1 dia
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

        String query = "SELECT COUNT(*) AS total \n"
                + " FROM cliente c \n"
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + " LEFT JOIN loginVentas lv ON lv.idCliente = c.ruc \n"
                + " WHERE " + queryProcesoFecha + " \n"
                + " AND CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate AND \n"
                + " ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL ) ";

        String startDateStr = anio + "-" + mes + "-01";

        Number count = (Number) em.createNativeQuery(query)
                .setParameter("fechaI", fechaI)
                .setParameter("fechaF", fechaF)
                .setParameter("startDate", startDateStr)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public List<Cliente> listExcelDiario(String proceso, String fecha, String anio, String mes) {
        String queryProceso = "";
        String queryProcesoFecha = "";

        switch (proceso) {
            case "registro":
                queryProceso = " lv.registro, lv.registroUsuario, lv.registroFecha, ";
                queryProcesoFecha = " :fecha = lv.registroFecha ";
                break;
            case "validacion":
                queryProceso = " lv.validacion, lv.validacionUsuario, lv.validacionFecha, ";
                queryProcesoFecha = " :fecha = lv.validacionFecha ";
                break;
            case "confirmacion":
                queryProceso = " lv.confirmacion, lv.confirmacionUsuario, lv.confirmacionFecha, ";
                queryProcesoFecha = " :fecha = lv.confirmacionFecha ";
                break;
        }

        String query = " SELECT c.ruc, c.y, c.razonSocial, c.idEstado, cE.descripcion AS estado, \n"
                + " c.idTipoServicio, tS.abreviatura as tipoServicioAbr, tS.descripcion as tipoServicio, c.prioridad, \n"
                + " c.rTMypeTributario, c.rTRus, c.rTEspecial, c.rTGeneral, c.rTAmazonico, c.rTAgrario, c.rT1ra, c.rT2da, c.rT3ra, c.rT4ta, c.rT5ta, c.fPle, \n"
                + " lp.movimiento, lv.anio, lv.mes, \n"
                + queryProceso
                // Additional fields required by mapTuples but not selected in legacy:
                + " lv.version, lv.responsable, lv.seeVentas, lv.extVentas, lv.observacion, \n"
                + " c.sunatSire, c.externoSire, c.gerenciaSire, \n"
                // Fill missing columns required by mapTuples with NULLs (since we only need the
                // specific proceso fields and base fields)
                + " NULL as revisionSunat, NULL as revisionSunatUsuario, NULL as revisionSunatFecha, NULL as validacionHora, NULL as confirmacionHora, NULL as registroHora, NULL as fVencimiento, NULL as sire, NULL as modalidad, \n"
                // Fill the other 2 procesos with NULLs since we do not select them dynamically
                + (proceso.equals("registro")
                        ? "NULL as validacion, NULL as validacionUsuario, NULL as validacionFecha, NULL as confirmacion, NULL as confirmacionUsuario, NULL as confirmacionFecha \n"
                        : proceso.equals("validacion")
                                ? "NULL as registro, NULL as registroUsuario, NULL as registroFecha, NULL as confirmacion, NULL as confirmacionUsuario, NULL as confirmacionFecha \n"
                                : "NULL as registro, NULL as registroUsuario, NULL as registroFecha, NULL as validacion, NULL as validacionUsuario, NULL as validacionFecha \n")
                + " FROM cliente c \n"
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id \n"
                + " LEFT JOIN clientsTipoServicio tS ON c.idTipoServicio = tS.id \n"
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc AND lp.anio = :anio  AND lp.mes = :mes \n"
                + " LEFT JOIN loginVentas lv ON lv.idCliente = c.ruc AND lv.anio = :anio  AND lv.mes = :mes \n"
                + " WHERE " + queryProcesoFecha + " ";

        jakarta.persistence.Query q = em.createNativeQuery(query, jakarta.persistence.Tuple.class)
                .setParameter("fecha", fecha)
                .setParameter("anio", anio)
                .setParameter("mes", mes);

        List<jakarta.persistence.Tuple> tuples = q.getResultList();
        return mapTuples(tuples, anio, mes);
    }
}
