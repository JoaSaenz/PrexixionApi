package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.entities.LoginProcesos;
import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.entities.SignerNivel;
import com.joa.prexixionapi.entities.ServicioRegistro;
import com.joa.prexixionapi.utils.DateUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class LoginProcesosRepository {

    @PersistenceContext
    private EntityManager em;

    private String getBaseSelect() {
        return "SELECT c.idEstado, cE.descripcion AS estado, "
                + " s.idCategoria, nc.abreviatura as abrCategoria, nc.descripcion as descCategoria, "
                + " CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore, "
                + " c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, "
                + " c.ruc, c.y, c.razonSocial, c.nombreCorto AS nombreCortoSigner, "
                + " grt.idRegimenTributario AS gestionRegimenTributario, rt.abreviatura AS abrGestionRegimenTributario, "
                + " (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio) FROM clienteAltaCom y WHERE y.idCliente = c.ruc ORDER BY acAnioPeriodoInicio DESC, acMesPeriodoInicio DESC) AS periodoInicioCom, "
                + " lp.movimiento, lp.anio, lp.mes, "
                + " lv.confirmacion as confirmacionVentas, lv.confirmacionUsuario as confirmacionUsuarioVentas, lv.confirmacionFecha as confirmacionFechaVentas, "
                + " lc.confirmacion as confirmacionCompras, lc.confirmacionUsuario as confirmacionUsuarioCompras, lc.confirmacionFecha as confirmacionFechaCompras, "
                + " lp.idPropuestaVentas, lp.idPropuestaCompras, "
                + " lp.preLiquidacion, lp.preLiquidacionFecha, lp.preLiquidacionUsuario, lp.preLiquidacionHora, "
                + " lp.confirmacion, lp.confirmacionFecha, lp.confirmacionUsuario, lp.confirmacionHora, "
                + " c.solU, c.solC, c.soldierU, c.soldierC, lp.observacion, "
                + " si.idCliente AS rucSire, siData.registrado AS sireCV, "
                + " pR.idTipo as idTipoRegistro, pR.fecha as fechaRegistro, pR.nroOrden as nroOrdenRegistro, "
                + " COALESCE(pDataN.ventasG, 0) + COALESCE(pDataN.ventasNetas10,0) + COALESCE(pDataN.ventasNg,0) + COALESCE(pDataN.expFactPer,0) + COALESCE(pDataN.expEmbrPer,0) + COALESCE(pDataN.ivapVentasGravadas,0) AS totalVentas, "
                + " COALESCE(pDataN.comprasG, 0) + COALESCE(pDataN.comprasNetas10,0) + COALESCE(pDataN.comprasMixtas,0) + COALESCE(pDataN.comprasNgE,0) + COALESCE(pDataN.impComprasG,0) + COALESCE(pDataN.comprasNg,0) AS totalCompras, "
                + " pDataN.igvPorPagar, pDataN.rentaPorPagar, "
                + " lp.version, "
                + " per2.dni as responsable2Dni, "
                + " LEFT(per2.nombres, 1) + "
                + " SUBSTRING(per2.apellidos, 1, "
                + "    CASE "
                + "        WHEN CHARINDEX(' ', per2.apellidos) - 1 < 0 "
                + "        THEN LEN(per2.apellidos) "
                + "        ELSE CHARINDEX(' ', per2.apellidos) - 1 "
                + "    END "
                + ") AS descResponsable2, "
                + " CASE WHEN (:anioMesInt >= 202310) "
                + "      THEN CASE WHEN si.idCliente IS NOT NULL "
                + "                THEN CASE WHEN c.y = '0' THEN crS.fecha0 WHEN c.y = '1' THEN crS.fecha1 WHEN c.y = '2' THEN crS.fecha2 "
                + "                          WHEN c.y = '3' THEN crS.fecha3 WHEN c.y = '4' THEN crS.fecha4 WHEN c.y = '5' THEN crS.fecha5 "
                + "                          WHEN c.y = '6' THEN crS.fecha6 WHEN c.y = '7' THEN crS.fecha7 WHEN c.y = '8' THEN crS.fecha8 "
                + "                          WHEN c.y = '9' THEN crS.fecha9 END "
                + "                ELSE CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "                          WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "                          WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "                          WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END END "
                + "      ELSE CASE WHEN c.fPle IS NOT NULL THEN "
                + "                CASE WHEN c.fPle != '' THEN "
                + "                     CASE WHEN (:anioMesInt >= 202002) AND (:anioMesInt <= 202008) THEN "
                + "                          CASE WHEN (:anioMesInt = 202002) THEN "
                + "                               CASE WHEN (c.y IN (0,1)) OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = :lastAnio) >= (u.valor * 2300)) THEN "
                + "                                    CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 "
                + "                                              WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 "
                + "                                              WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 "
                + "                                              WHEN c.y = '9' THEN crPle.fecha9 END "
                + "                                    ELSE "
                + "                                         CASE WHEN c.fPrico = '' THEN "
                + "                                              CASE WHEN c.y = '0' THEN crPleC.fecha0 WHEN c.y = '1' THEN crPleC.fecha1 WHEN c.y = '2' THEN crPleC.fecha2 "
                + "                                                        WHEN c.y = '3' THEN crPleC.fecha3 WHEN c.y = '4' THEN crPleC.fecha4 WHEN c.y = '5' THEN crPleC.fecha5 "
                + "                                                        WHEN c.y = '6' THEN crPleC.fecha6 WHEN c.y = '7' THEN crPleC.fecha7 WHEN c.y = '8' THEN crPleC.fecha8 "
                + "                                                        WHEN c.y = '9' THEN crPleC.fecha9 END "
                + "                                              ELSE "
                + "                                                   CASE WHEN c.y = '0' THEN crPleP.fecha0 WHEN c.y = '1' THEN crPleP.fecha1 WHEN c.y = '2' THEN crPleP.fecha2 "
                + "                                                             WHEN c.y = '3' THEN crPleP.fecha3 WHEN c.y = '4' THEN crPleP.fecha4 WHEN c.y = '5' THEN crPleP.fecha5 "
                + "                                                             WHEN c.y = '6' THEN crPleP.fecha6 WHEN c.y = '7' THEN crPleP.fecha7 WHEN c.y = '8' THEN crPleP.fecha8 "
                + "                                                             WHEN c.y = '9' THEN crPleP.fecha9 END END "
                + "                                    END "
                + "                               WHEN (:anioMesInt > 202002) THEN "
                + "                                    CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = :lastAnio) >= (u.valor * 5000)) THEN "
                + "                                         CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 "
                + "                                                   WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 "
                + "                                                   WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 "
                + "                                                   WHEN c.y = '9' THEN crPle.fecha9 END "
                + "                                         ELSE "
                + "                                              CASE WHEN c.fPrico = '' THEN "
                + "                                                   CASE WHEN c.y = '0' THEN crPleC.fecha0 WHEN c.y = '1' THEN crPleC.fecha1 WHEN c.y = '2' THEN crPleC.fecha2 "
                + "                                                             WHEN c.y = '3' THEN crPleC.fecha3 WHEN c.y = '4' THEN crPleC.fecha4 WHEN c.y = '5' THEN crPleC.fecha5 "
                + "                                                             WHEN c.y = '6' THEN crPleC.fecha6 WHEN c.y = '7' THEN crPleC.fecha7 WHEN c.y = '8' THEN crPleC.fecha8 "
                + "                                                             WHEN c.y = '9' THEN crPleC.fecha9 END "
                + "                                                   ELSE "
                + "                                                        CASE WHEN c.y = '0' THEN crPleP.fecha0 WHEN c.y = '1' THEN crPleP.fecha1 WHEN c.y = '2' THEN crPleP.fecha2 "
                + "                                                                  WHEN c.y = '3' THEN crPleP.fecha3 WHEN c.y = '4' THEN crPleP.fecha4 WHEN c.y = '5' THEN crPleP.fecha5 "
                + "                                                                  WHEN c.y = '6' THEN crPleP.fecha6 WHEN c.y = '7' THEN crPleP.fecha7 WHEN c.y = '8' THEN crPleP.fecha8 "
                + "                                                                  WHEN c.y = '9' THEN crPleP.fecha9 END END "
                + "                                         END END "
                + "                          ELSE "
                + "                               CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 "
                + "                                         WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 "
                + "                                         WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 "
                + "                                         WHEN c.y = '9' THEN crPle.fecha9 END END "
                + "                     ELSE "
                + "                          CASE WHEN (:anioMesInt >= 202002) AND (:anioMesInt <= 202008) THEN "
                + "                               CASE WHEN (:anioMesInt = 202002) THEN "
                + "                                    CASE WHEN (c.y = 0) OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = :lastAnio) >= (u.valor * 2300)) THEN "
                + "                                         CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "                                                   WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "                                                   WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "                                                   WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END "
                + "                                         ELSE "
                + "                                              CASE WHEN c.fPrico = '' THEN "
                + "                                                   CASE WHEN c.y = '0' THEN crC.fecha0 WHEN c.y = '1' THEN crC.fecha1 WHEN c.y = '2' THEN crC.fecha2 "
                + "                                                             WHEN c.y = '3' THEN crC.fecha3 WHEN c.y = '4' THEN crC.fecha4 WHEN c.y = '5' THEN crC.fecha5 "
                + "                                                             WHEN c.y = '6' THEN crC.fecha6 WHEN c.y = '7' THEN crC.fecha7 WHEN c.y = '8' THEN crC.fecha8 "
                + "                                                             WHEN c.y = '9' THEN crC.fecha9 END "
                + "                                                   ELSE "
                + "                                                        CASE WHEN c.y = '0' THEN crP.fecha0 WHEN c.y = '1' THEN crP.fecha1 WHEN c.y = '2' THEN crP.fecha2 "
                + "                                                                  WHEN c.y = '3' THEN crP.fecha3 WHEN c.y = '4' THEN crP.fecha4 WHEN c.y = '5' THEN crP.fecha5 "
                + "                                                                  WHEN c.y = '6' THEN crP.fecha6 WHEN c.y = '7' THEN crP.fecha7 WHEN c.y = '8' THEN crP.fecha8 "
                + "                                                                  WHEN c.y = '9' THEN crP.fecha9 END END "
                + "                                         END "
                + "                               WHEN (:anioMesInt > 202002) THEN "
                + "                                    CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = :lastAnio) >= (u.valor * 5000)) THEN "
                + "                                         CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "                                                   WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "                                                   WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "                                                   WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END "
                + "                                         ELSE "
                + "                                              CASE WHEN c.fPrico = '' THEN "
                + "                                                   CASE WHEN c.y = '0' THEN crC.fecha0 WHEN c.y = '1' THEN crC.fecha1 WHEN c.y = '2' THEN crC.fecha2 "
                + "                                                             WHEN c.y = '3' THEN crC.fecha3 WHEN c.y = '4' THEN crC.fecha4 WHEN c.y = '5' THEN crC.fecha5 "
                + "                                                             WHEN c.y = '6' THEN crC.fecha6 WHEN c.y = '7' THEN crC.fecha7 WHEN c.y = '8' THEN crC.fecha8 "
                + "                                                             WHEN c.y = '9' THEN crC.fecha9 END "
                + "                                                   ELSE "
                + "                                                        CASE WHEN c.y = '0' THEN crP.fecha0 WHEN c.y = '1' THEN crP.fecha1 WHEN c.y = '2' THEN crP.fecha2 "
                + "                                                                  WHEN c.y = '3' THEN crP.fecha3 WHEN c.y = '4' THEN crP.fecha4 WHEN c.y = '5' THEN crP.fecha5 "
                + "                                                                  WHEN c.y = '6' THEN crP.fecha6 WHEN c.y = '7' THEN crP.fecha7 WHEN c.y = '8' THEN crP.fecha8 "
                + "                                                                  WHEN c.y = '9' THEN crP.fecha9 END END "
                + "                                         END END "
                + "                          ELSE "
                + "                               CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "                                         WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "                                         WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 "
                + "                                         WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END END END "
                + "      END END AS fVencimiento "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN signerNiveles s ON c.ruc = s.idCliente "
                + " LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id "
                + " LEFT JOIN (SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6) cso ON c.ruc = cso.idCliente "
                + " LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 "
                + "      AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= :startDate "
                + "      AND (CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= :startDate OR si.stAnioHasta IS NULL) "
                + " LEFT JOIN cronogramaPDT cr ON :anio = cr.anio AND :mes = cr.mes "
                + " LEFT JOIN cronogramaCovidPDT crC ON :anio = crC.anio AND :mes = crC.mes "
                + " LEFT JOIN cronogramaPricoPDT crP ON :anio = crP.anio AND :mes = crP.mes "
                + " LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = :anio AND crPle.mes = :mes "
                + " LEFT JOIN CRONOGRAMACOVIDPLE_A crPleC ON crPleC.anio = :anio AND crPleC.mes = :mes "
                + " LEFT JOIN cronogramaPricoPLE_A crPleP ON crPleP.anio = :anio AND crPleP.mes = :mes "
                + " LEFT JOIN cronogramaSire crS ON :anio = crS.anio AND :mes = crS.mes "
                + " LEFT JOIN uit u ON u.anio = :lastAnio "
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc AND lp.anio = :anio AND lp.mes = :mes "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = :anio AND lv.mes = :mes "
                + " LEFT JOIN loginCompras lc ON c.ruc = lc.idCliente AND lc.anio = :anio AND lc.mes = :mes "
                + " LEFT JOIN pdt621DataNew pDataN ON lp.ruc = pDataN.idCliente AND lp.anio = pDataN.anio AND lp.mes = pDataN.mes "
                + " LEFT JOIN sireData siData ON c.ruc = siData.idCliente AND siData.anio = :anio AND siData.mes = :mes "
                + " LEFT JOIN pdt621registros pR ON p.idCliente = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes "
                + "      AND pR.id = (select MAX(id) from pdt621Registros x where x.idCliente = c.ruc AND x.anio = :anio AND x.mes = :mes AND x.idTipo IN (3,4,5) ) "
                + " LEFT JOIN gestionRegimenesTributarios grt ON c.ruc = grt.idCliente AND grt.anio = :anio AND grt.mes = :mes "
                + " LEFT JOIN regimenesTributarios rt ON grt.idRegimenTributario = rt.id "
                + " LEFT JOIN personal per2 ON lp.dniResponsable2RTB = per2.dni "
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate "
                + "   AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL) ";
    }

    private void bindBaseParameters(Query query, String anio, String mes) {
        int lastAnio = Integer.parseInt(anio) - 1;
        int anioMesInt = Integer.parseInt(anio + mes);
        String startDate = anio + "-" + mes + "-01";

        query.setParameter("anio", anio);
        query.setParameter("mes", mes);
        query.setParameter("lastAnio", lastAnio);
        query.setParameter("anioMesInt", anioMesInt);
        query.setParameter("startDate", startDate);
    }

    @SuppressWarnings("unchecked")
    public List<LoginProcesos> list(String anio, String mes, String estados, String grupos, String equipo2) {
        StringBuilder sql = new StringBuilder(getBaseSelect());

        if (estados != null && !estados.trim().isEmpty()) {
            sql.append(" AND c.idEstado IN (:estados) ");
        }

        if (grupos != null && !grupos.trim().isEmpty()) {
            sql.append(" AND c.y IN (:grupos) ");
        }

        if (equipo2 != null && !equipo2.trim().isEmpty()) {
            List<String> eqList = Arrays.stream(equipo2.split(",")).map(String::trim).collect(Collectors.toList());
            if (eqList.contains("0") || eqList.contains("00000000")) {
                sql.append(" AND (lp.dniResponsable2RTB IN (:equipo2) OR lp.dniResponsable2RTB IS NULL OR LTRIM(RTRIM(lp.dniResponsable2RTB)) = '') ");
            } else {
                sql.append(" AND lp.dniResponsable2RTB IN (:equipo2) ");
            }
        }

        sql.append(" ORDER BY c.y, c.razonSocial ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);
        bindBaseParameters(query, anio, mes);

        if (estados != null && !estados.trim().isEmpty()) {
            query.setParameter("estados", Arrays.stream(estados.split(",")).map(String::trim).map(Integer::parseInt)
                    .collect(Collectors.toList()));
        }

        if (grupos != null && !grupos.trim().isEmpty()) {
            query.setParameter("grupos",
                    Arrays.stream(grupos.split(",")).map(String::trim).collect(Collectors.toList()));
        }

        if (equipo2 != null && !equipo2.trim().isEmpty()) {
            query.setParameter("equipo2",
                    Arrays.stream(equipo2.split(",")).map(String::trim).collect(Collectors.toList()));
        }

        List<Tuple> tuples = query.getResultList();
        return mapTuples(tuples, anio, mes);
    }

    @SuppressWarnings("unchecked")
    public LoginProcesos getOne(String ruc, String anio, String mes) {
        String sql = " SELECT c.razonSocial, "
                + " (SELECT SUBSTRING ( "
                + "     (SELECT ',' + cp.plCorreo AS 'data()' FROM clientePersonal cp where cp.idCliente = c.ruc FOR XML PATH('')), 2, 9999) AS Correo) AS correos, "
                + " lp.movimiento, "
                + " lp.dniResponsable2RTB, "
                + " lv.confirmacion as confirmacionVentas, lv.confirmacionUsuario as confirmacionUsuarioVentas, lv.confirmacionFecha as confirmacionFechaVentas, "
                + " lc.confirmacion as confirmacionCompras, lc.confirmacionUsuario as confirmacionUsuarioCompras, lc.confirmacionFecha as confirmacionFechaCompras, "
                + " lp.idPropuestaVentas, lp.idPropuestaCompras, "
                + " lp.preLiquidacion, lp.preLiquidacionUsuario, lp.preLiquidacionFecha, lp.preLiquidacionHora, "
                + " lp.confirmacion, lp.confirmacionUsuario, lp.confirmacionFecha, lp.confirmacionHora, "
                + " lp.observacion, lp.version "
                + " FROM cliente c "
                + " LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = :anio AND lp.mes = :mes "
                + " LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = :anio AND lv.mes = :mes "
                + " LEFT JOIN loginCompras lc ON c.ruc = lc.idCliente AND lc.anio = :anio AND lc.mes = :mes "
                + " WHERE c.ruc = :ruc ";

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("ruc", ruc);
        query.setParameter("anio", anio);
        query.setParameter("mes", mes);

        List<Tuple> tuples = query.getResultList();
        if (tuples.isEmpty()) {
            return null;
        }

        Tuple tuple = tuples.get(0);
        LoginProcesos obj = new LoginProcesos();
        obj.setRuc(ruc);
        obj.setAnio(anio);
        obj.setMes(mes);
        obj.setRazonSocial(getStringSafely(tuple, "razonSocial"));
        obj.setCorreos(getStringSafely(tuple, "correos"));
        obj.setMovimiento(getStringSafely(tuple, "movimiento"));
        obj.setDniResponsable2RTB(getStringSafely(tuple, "dniResponsable2RTB"));

        obj.setConfirmacionVentas(getIntegerSafely(tuple, "confirmacionVentas"));
        obj.setConfirmacionUsuarioVentas(getStringSafely(tuple, "confirmacionUsuarioVentas"));
        obj.setConfirmacionFechaVentas(getStringSafely(tuple, "confirmacionFechaVentas"));

        obj.setConfirmacionCompras(getIntegerSafely(tuple, "confirmacionCompras"));
        obj.setConfirmacionUsuarioCompras(getStringSafely(tuple, "confirmacionUsuarioCompras"));
        obj.setConfirmacionFechaCompras(getStringSafely(tuple, "confirmacionFechaCompras"));

        obj.setPropuestaVentas(new Gclass(getIntegerSafely(tuple, "idPropuestaVentas")));
        obj.setPropuestaCompras(new Gclass(getIntegerSafely(tuple, "idPropuestaCompras")));

        obj.setPreLiquidacion(getIntegerSafely(tuple, "preLiquidacion"));
        obj.setPreLiquidacionFecha(getStringSafely(tuple, "preLiquidacionFecha"));
        obj.setPreLiquidacionUsuario(getStringSafely(tuple, "preLiquidacionUsuario"));
        obj.setPreLiquidacionHora(getStringSafely(tuple, "preLiquidacionHora"));

        obj.setConfirmacion(getIntegerSafely(tuple, "confirmacion"));
        obj.setConfirmacionFecha(getStringSafely(tuple, "confirmacionFecha"));
        obj.setConfirmacionUsuario(getStringSafely(tuple, "confirmacionUsuario"));
        obj.setConfirmacionHora(getStringSafely(tuple, "confirmacionHora"));

        obj.setVersion(getIntegerSafely(tuple, "version"));
        obj.setObservacion(getStringSafely(tuple, "observacion") == null ? "" : getStringSafely(tuple, "observacion"));

        return obj;
    }

    // --- REPORTE DIARIO METHODS ---
    @SuppressWarnings("unchecked")
    public List<LoginProcesos> listExcelDiario(String proceso, String fechaI, String fechaF) {
        String queryProceso = "";
        String queryProcesoFecha = "";
        String orderBy = "";

        switch (proceso) {
            case "preLiquidacion":
                queryProceso = " lp.preLiquidacion, lp.preLiquidacionFecha, lp.preLiquidacionUsuario, ";
                queryProcesoFecha = " :fechaI <= lp.preLiquidacionFecha AND lp.preLiquidacionFecha <= :fechaF ";
                orderBy = " ORDER BY lp.preLiquidacionFecha ";
                break;
            case "confirmacion":
                queryProceso = " lp.confirmacion, lp.confirmacionFecha, lp.confirmacionUsuario, ";
                queryProcesoFecha = " :fechaI <= lp.confirmacionFecha AND lp.confirmacionFecha <= :fechaF ";
                orderBy = " ORDER BY lp.confirmacionFecha ";
                break;
        }

        String sql = " SELECT c.ruc, c.y, c.razonSocial, c.nombreCorto AS nombreCortoSigner, c.idEstado, cE.descripcion AS estado, "
                + " c.idTipoServicio, tS.abreviatura as tipoServicioAbr, tS.descripcion as tipoServicio, lp.movimiento, "
                + " c.prioridad, lp.anio, lp.mes, "
                + queryProceso
                + " c.solU, c.solC, c.soldierU, c.soldierC, lp.observacion, "
                + " c.fPle, "
                + " CASE WHEN c.ruc = lie.ruc THEN 'STORECOM' ELSE 'ENTERCOM' END AS areaEncargada "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN clientsTipoServicio tS ON c.idTipoServicio = tS.id "
                + " LEFT JOIN loginInventariosEmpresas lie ON c.ruc = lie.ruc "
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc "
                + " WHERE " + queryProcesoFecha
                + orderBy;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("fechaI", fechaI);
        query.setParameter("fechaF", fechaF);

        List<Tuple> tuples = query.getResultList();
        List<LoginProcesos> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
            LoginProcesos obj = new LoginProcesos();
            obj.setRuc(getStringSafely(tuple, "ruc"));
            Character yChar = getCharacterSafely(tuple, "y");
            obj.setY(yChar != null ? yChar.toString() : null);
            obj.setRazonSocial(getStringSafely(tuple, "razonSocial"));
            obj.setNombreCortoSigner(getStringSafely(tuple, "nombreCortoSigner"));
            obj.setIdEstado(getIntegerSafely(tuple, "idEstado"));
            obj.setEstado(getStringSafely(tuple, "estado"));

            obj.setIdTipoServicio(getIntegerSafely(tuple, "idTipoServicio"));
            obj.setTipoServicioAbr(getStringSafely(tuple, "tipoServicioAbr"));
            obj.setTipoServicio(getStringSafely(tuple, "tipoServicio"));

            obj.setMovimiento(getStringSafely(tuple, "movimiento"));

            obj.setPrioridad(getStringSafely(tuple, "prioridad"));
            obj.setAnio(getStringSafely(tuple, "anio"));
            obj.setMes(getStringSafely(tuple, "mes"));

            switch (proceso) {
                case "preLiquidacion":
                    obj.setPreLiquidacion(getIntegerSafely(tuple, "preLiquidacion"));
                    obj.setPreLiquidacionFecha(getStringSafely(tuple, "preLiquidacionFecha"));
                    obj.setPreLiquidacionUsuario(getStringSafely(tuple, "preLiquidacionUsuario"));
                    break;
                case "confirmacion":
                    obj.setConfirmacion(getIntegerSafely(tuple, "confirmacion"));
                    obj.setConfirmacionFecha(getStringSafely(tuple, "confirmacionFecha"));
                    obj.setConfirmacionUsuario(getStringSafely(tuple, "confirmacionUsuario"));
                    break;
            }

            obj.setSolU(getStringSafely(tuple, "solU"));
            obj.setSolC(getStringSafely(tuple, "solC"));
            obj.setSoldierU(getStringSafely(tuple, "soldierU"));
            obj.setSoldierC(getStringSafely(tuple, "soldierC"));
            obj.setObservacion(
                    getStringSafely(tuple, "observacion") == null ? "" : getStringSafely(tuple, "observacion"));

            String fPleVal = getStringSafely(tuple, "fPle");
            obj.setPle((fPleVal == null || fPleVal.isEmpty()) ? "" : "PLE");

            obj.setAreaEncargada(getStringSafely(tuple, "areaEncargada"));

            list.add(obj);
        }
        return list;
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
            case "validerCompras":
                queryProcesoFecha = " :fechaI <= lp.validacionFecha AND lp.validacionFecha <= :fechaF AND lp.preLiquidacion = 1 ";
                break;
            case "validerVentas":
                queryProcesoFecha = " :fechaI <= lp.validacionFecha AND lp.validacionFecha <= :fechaF AND lp.preLiquidacion = 1 ";
                break;
            case "preLiquidacion":
                queryProcesoFecha = " :fechaI <= lp.preLiquidacionFecha AND lp.preLiquidacionFecha <= :fechaF AND lp.preLiquidacion IN (1,2) ";
                break;
            case "confirmacion":
                queryProcesoFecha = " :fechaI <= lp.confirmacionFecha AND lp.confirmacionFecha <= :fechaF AND lp.confirmacion IN (1,2) ";
                break;
        }

        String query = "SELECT COUNT(*) AS total "
                + " FROM cliente c "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc "
                + " WHERE " + queryProcesoFecha
                + "   AND CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate "
                + "   AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL) ";

        String startDate = anio + "-" + mes + "-01";

        Number count = (Number) em.createNativeQuery(query)
                .setParameter("fechaI", fechaI)
                .setParameter("fechaF", fechaF)
                .setParameter("startDate", startDate)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public int countTotal(String anio, String mes) {
        String query = "SELECT COUNT(*) AS total "
                + " FROM cliente c "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate "
                + "   AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL) ";

        String startDate = anio + "-" + mes + "-01";

        Number count = (Number) em.createNativeQuery(query)
                .setParameter("startDate", startDate)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    // --- HELPER METHODS FOR SAFE TUPLE MAPPING ---
    private String getStringSafely(Tuple tuple, String alias) {
        try {
            Object obj = tuple.get(alias);
            return obj != null ? obj.toString().trim() : null;
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
            String s = obj.toString().trim();
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
            if (obj instanceof Number)
                return ((Number) obj).intValue();
            return Integer.parseInt(obj.toString().trim());
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    private Double getDoubleSafely(Tuple tuple, String alias) {
        try {
            Object obj = tuple.get(alias);
            if (obj == null)
                return 0.0;
            if (obj instanceof Number)
                return ((Number) obj).doubleValue();
            return Double.parseDouble(obj.toString().trim());
        } catch (IllegalArgumentException e) {
            return 0.0;
        }
    }

    private List<LoginProcesos> mapTuples(List<Tuple> tuples, String anio, String mes) {
        List<LoginProcesos> list = new ArrayList<>();
        for (Tuple tuple : tuples) {

            LoginProcesos obj = new LoginProcesos();
            obj.setRuc(getStringSafely(tuple, "ruc"));

            Character yChar = getCharacterSafely(tuple, "y");
            obj.setY(yChar != null ? yChar.toString() : null);

            obj.setRazonSocial(getStringSafely(tuple, "razonSocial"));
            obj.setNombreCortoSigner(getStringSafely(tuple, "nombreCortoSigner"));
            obj.setIdEstado(getIntegerSafely(tuple, "idEstado"));
            obj.setEstado(getStringSafely(tuple, "estado"));
            obj.setGrupoEconomico(new Gclass(getIntegerSafely(tuple, "idGrupoEconomico"),
                    getStringSafely(tuple, "descGrupoEconomico")));

            SignerNivel sn = new SignerNivel();
            sn.setCategoria(new Gclass(getIntegerSafely(tuple, "idCategoria"), getStringSafely(tuple, "abrCategoria"),
                    getStringSafely(tuple, "descCategoria")));
            sn.setCategoriaStore(getIntegerSafely(tuple, "categoriaStore"));
            obj.setSignerNivel(sn);

            obj.setGestionRegimenTributario(getIntegerSafely(tuple, "gestionRegimenTributario"));
            obj.setAbrGestionRegimenTributario(getStringSafely(tuple, "abrGestionRegimenTributario"));

            obj.setPeriodoInicioCom(getStringSafely(tuple, "periodoInicioCom"));
            obj.setPeriodoConcatenado(anio + "-" + mes);
            obj.setAnio(anio);
            obj.setMes(mes);

            obj.setfVencimiento(getStringSafely(tuple, "fVencimiento"));
            if (obj.getfVencimiento() != null && !obj.getfVencimiento().isEmpty()) {
                String[] parts = obj.getfVencimiento().split("-");
                if (parts.length >= 3) {
                    obj.setDiaVencimiento(parts[2]);
                    obj.setMesVencimiento(DateUtils.getNameStMonth(parts[1]));
                }
                obj.setDifference((int) DateUtils.getDifferenceInDays(obj.getfVencimiento()));
            }

            obj.setMovimiento(getStringSafely(tuple, "movimiento"));

            obj.setVentasTotales(getDoubleSafely(tuple, "totalVentas"));
            obj.setComprasTotales(getDoubleSafely(tuple, "totalCompras"));
            obj.setIgvPorPagar(getDoubleSafely(tuple, "igvPorPagar"));
            obj.setRentaPorPagar(getDoubleSafely(tuple, "rentaPorPagar"));

            // VARIABLES LOGIN VENTAS
            obj.setConfirmacionVentas(getIntegerSafely(tuple, "confirmacionVentas"));
            obj.setConfirmacionUsuarioVentas(getStringSafely(tuple, "confirmacionUsuarioVentas"));
            obj.setConfirmacionFechaVentas(getStringSafely(tuple, "confirmacionFechaVentas"));

            // VARIABLES LOGIN COMPRAS
            obj.setConfirmacionCompras(getIntegerSafely(tuple, "confirmacionCompras"));
            obj.setConfirmacionUsuarioCompras(getStringSafely(tuple, "confirmacionUsuarioCompras"));
            obj.setConfirmacionFechaCompras(getStringSafely(tuple, "confirmacionFechaCompras"));

            obj.setPreLiquidacion(getIntegerSafely(tuple, "preLiquidacion"));
            obj.setPreLiquidacionFecha(getStringSafely(tuple, "preLiquidacionFecha"));
            obj.setPreLiquidacionUsuario(getStringSafely(tuple, "preLiquidacionUsuario"));
            obj.setPreLiquidacionHora(getStringSafely(tuple, "preLiquidacionHora"));

            obj.setConfirmacion(getIntegerSafely(tuple, "confirmacion"));
            obj.setConfirmacionFecha(getStringSafely(tuple, "confirmacionFecha"));
            obj.setConfirmacionUsuario(getStringSafely(tuple, "confirmacionUsuario"));
            obj.setConfirmacionHora(getStringSafely(tuple, "confirmacionHora"));

            // REGISTROS (PDT)
            List<ServicioRegistro> registros = new ArrayList<>();
            if (getIntegerSafely(tuple, "idTipoRegistro") != 0) {
                ServicioRegistro ultimoRegistro = new ServicioRegistro();
                ultimoRegistro.setTipo(new Gclass(getIntegerSafely(tuple, "idTipoRegistro")));
                ultimoRegistro.setFecha(getStringSafely(tuple, "fechaRegistro"));
                ultimoRegistro.setNroOrden(getStringSafely(tuple, "nroOrdenRegistro"));
                registros.add(ultimoRegistro);
            }
            obj.setRegistros(registros);

            // ENVOY SIRE
            if (getStringSafely(tuple, "rucSire") == null) {
                obj.setSireCV(2);
            } else {
                obj.setSireCV(getIntegerSafely(tuple, "sireCV"));
            }

            obj.setObservacion(
                    getStringSafely(tuple, "observacion") == null ? "" : getStringSafely(tuple, "observacion"));

            // RESPONSABLES RTB
            String respDni = getStringSafely(tuple, "responsable2Dni");
            if (respDni == null || respDni.isEmpty() || respDni.equals("0")) {
                obj.setDniResponsable2RTB("");
                obj.setResponsable2RTB("");
            } else {
                obj.setDniResponsable2RTB(respDni);
                obj.setResponsable2RTB(getStringSafely(tuple, "descResponsable2"));
            }

            // EXTRA DETAILS FOR EXCEL
            obj.setPropuestaVentas(new Gclass(getIntegerSafely(tuple, "idPropuestaVentas")));
            obj.setPropuestaCompras(new Gclass(getIntegerSafely(tuple, "idPropuestaCompras")));

            obj.setSolU(getStringSafely(tuple, "solU"));
            obj.setSolC(getStringSafely(tuple, "solC"));
            obj.setSoldierU(getStringSafely(tuple, "soldierU"));
            obj.setSoldierC(getStringSafely(tuple, "soldierC"));

            obj.setVersion(getIntegerSafely(tuple, "version"));

            int pdt621Registrado = 0;
            if (obj.getRegistros() != null && !obj.getRegistros().isEmpty()) {
                ServicioRegistro reg = obj.getRegistros().get(0);
                if (reg != null && reg.getNroOrden() != null && !reg.getNroOrden().trim().equals("")) {
                    pdt621Registrado = 1;
                }
            }
            int ordVal = 0;
            if (obj.getConfirmacionVentas() != null)
                ordVal += obj.getConfirmacionVentas();
            if (obj.getConfirmacionCompras() != null)
                ordVal += obj.getConfirmacionCompras();
            if (obj.getPreLiquidacion() != null)
                ordVal += obj.getPreLiquidacion();
            if (obj.getConfirmacion() != null)
                ordVal += obj.getConfirmacion();
            if (obj.getSireCV() != null)
                ordVal += obj.getSireCV();
            ordVal += pdt621Registrado;
            obj.setOrden(ordVal);

            list.add(obj);
        }
        return list;
    }
}
