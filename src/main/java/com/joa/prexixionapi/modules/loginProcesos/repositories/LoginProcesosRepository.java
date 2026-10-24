package com.joa.prexixionapi.modules.loginProcesos.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosRequest;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosListDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosAvanceDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosVentaDTO;
import com.joa.prexixionapi.modules.loginProcesos.repositories.LoginProcesosRepository;
import com.joa.prexixionapi.common.utils.QueryUtils;

@Repository
@RequiredArgsConstructor
public class LoginProcesosRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public String[] getVencimientoLimits(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);

        String sql = "SELECT MIN(v.fVencimiento), MAX(v.fVencimiento) FROM ( SELECT "
                + getFechaVencimientoSql(anio, mes, lastAnio) + " AS fVencimiento "
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
                + " LEFT JOIN Pdt621Bancos pb ON pDataN.idBancoPdt621 = pb.id "
                + " LEFT JOIN Pdt621Bancos pbRenta ON pDataN.rentaBanco = pbRenta.id "
                + " LEFT JOIN sireData siData ON c.ruc = siData.idCliente AND siData.anio = :anio AND siData.mes = :mes "
                + " LEFT JOIN pdt621registros pR ON p.idCliente = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes "
                + "      AND pR.id = (select MAX(id) from pdt621Registros x where x.idCliente = c.ruc AND x.anio = :anio AND x.mes = :mes AND x.idTipo IN (3,4,5) ) "
                + " LEFT JOIN gestionRegimenesTributarios grt ON c.ruc = grt.idCliente AND grt.anio = :anio AND grt.mes = :mes "
                + " LEFT JOIN regimenesTributarios rt ON grt.idRegimenTributario = rt.id "
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate "
                + "   AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL) ) v "
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

    public List<LoginProcesosListDTO> list(String anio, String mes, LoginProcesosRequest req) {
        StringBuilder sql = new StringBuilder(buildSelectQuery(anio, mes));
        MapSqlParameterSource params = buildBaseParams(anio, mes);
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);

        if (req != null) {
            QueryUtils.appendIntFilter(sql, params, "c.idEstado", req.getEstadoFiltro(), "estados");
            QueryUtils.appendStringFilter(sql, params, "c.y", req.getGrupoFiltro(), "grupos");
            QueryUtils.appendIntFilter(sql, params, "s.idCategoria", req.getCategoriaFiltro(), "categorias");

            if (req.getCategoriaStoreFiltro() != null && !req.getCategoriaStoreFiltro().isEmpty()) {
                sql.append(" AND (CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END) IN (:categoriaStore) ");
                params.addValue("categoriaStore", req.getCategoriaStoreFiltro());
            }

            QueryUtils.appendIntFilter(sql, params, "c.idGrupoEconomico", req.getGrupoEconomicoFiltro(),
                    "grupoEconomico");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(lv.confirmacion, 0)", req.getConfirmacionVentaFiltro(),
                    "confirmacionVentas");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(lc.confirmacion, 0)", req.getConfirmacionCompraFiltro(),
                    "confirmacionCompras");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(lp.preLiquidacion, 0)", req.getPreLiquidacionFiltro(),
                    "preLiquidacion");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(lp.confirmacion, 0)", req.getConfirmacionFiltro(),
                    "confirmacion");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(siData.registrado, 0)", req.getSireCvFiltro(), "sireCv");

            if (req.getPdtFiltro() != null && !req.getPdtFiltro().isEmpty()) {
                List<Integer> list = req.getPdtFiltro();
                if (list.contains(1) && !list.contains(0)) {
                    sql.append(" AND (pR.nroOrden IS NOT NULL AND TRIM(pR.nroOrden) <> '') ");
                } else if (list.contains(0) && !list.contains(1)) {
                    sql.append(" AND (pR.nroOrden IS NULL OR TRIM(pR.nroOrden) = '') ");
                }
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

        sql.append(" ORDER BY c.y, c.razonSocial ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapRowToListDTO(rs, anio, mes));
    }

    public LoginProcesosDTO getOne(String ruc, String anio, String mes) {
        String sql = """
                SELECT c.razonSocial,
                (SELECT SUBSTRING (
                    (SELECT ',' + cp.plCorreo AS 'data()' FROM clientePersonal cp where cp.idCliente = c.ruc FOR XML PATH('')), 2, 9999) AS Correo) AS correos,
                lp.movimiento,
                lv.confirmacion as confirmacionVentas, lv.confirmacionUsuario as confirmacionUsuarioVentas, lv.confirmacionFecha as confirmacionFechaVentas,
                lc.confirmacion as confirmacionCompras, lc.confirmacionUsuario as confirmacionUsuarioCompras, lc.confirmacionFecha as confirmacionFechaCompras,
                lp.idPropuestaVentas, lp.idPropuestaCompras,
                lp.preLiquidacion, lp.preLiquidacionUsuario, lp.preLiquidacionFecha, lp.preLiquidacionHora,
                lp.confirmacion, lp.confirmacionUsuario, lp.confirmacionFecha, lp.confirmacionHora,
                lp.observacion, lp.version
                FROM cliente c
                LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = :anio AND lp.mes = :mes
                LEFT JOIN loginVentas lv ON c.ruc = lv.idCliente AND lv.anio = :anio AND lv.mes = :mes
                LEFT JOIN loginCompras lc ON c.ruc = lc.idCliente AND lc.anio = :anio AND lc.mes = :mes
                WHERE c.ruc = :ruc
                """;

        MapSqlParameterSource params = buildBaseParams(anio, mes);
        params.addValue("ruc", ruc);

        List<LoginProcesosDTO> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            LoginProcesosDTO obj = new LoginProcesosDTO();
            obj.setRuc(ruc);
            obj.setAnio(anio);
            obj.setMes(mes);
            obj.setRazonSocial(rs.getString("razonSocial"));
            obj.setCorreos(rs.getString("correos"));
            obj.setMovimiento(rs.getString("movimiento"));

            obj.setConfirmacionVentas(rs.getObject("confirmacionVentas", Integer.class));
            obj.setConfirmacionUsuarioVentas(rs.getString("confirmacionUsuarioVentas"));
            obj.setConfirmacionFechaVentas(rs.getString("confirmacionFechaVentas"));

            obj.setConfirmacionCompras(rs.getObject("confirmacionCompras", Integer.class));
            obj.setConfirmacionUsuarioCompras(rs.getString("confirmacionUsuarioCompras"));
            obj.setConfirmacionFechaCompras(rs.getString("confirmacionFechaCompras"));

            Integer idPV = rs.getObject("idPropuestaVentas", Integer.class);
            obj.setIdPropuestaVentas(idPV);
            obj.setPropuestaVentas(idPV != null ? new GclassDTO(idPV) : null);

            Integer idPC = rs.getObject("idPropuestaCompras", Integer.class);
            obj.setIdPropuestaCompras(idPC);
            obj.setPropuestaCompras(idPC != null ? new GclassDTO(idPC) : null);

            obj.setPreLiquidacion(rs.getObject("preLiquidacion", Integer.class));
            obj.setPreLiquidacionFecha(rs.getString("preLiquidacionFecha"));
            obj.setPreLiquidacionUsuario(rs.getString("preLiquidacionUsuario"));
            obj.setPreLiquidacionHora(rs.getString("preLiquidacionHora"));

            obj.setConfirmacion(rs.getObject("confirmacion", Integer.class));
            obj.setConfirmacionFecha(rs.getString("confirmacionFecha"));
            obj.setConfirmacionUsuario(rs.getString("confirmacionUsuario"));
            obj.setConfirmacionHora(rs.getString("confirmacionHora"));

            obj.setVersion(rs.getObject("version", Integer.class));
            String obs = rs.getString("observacion");
            obj.setObservacion(obs == null ? "" : obs);
            return obj;
        });

        return list.isEmpty() ? null : list.get(0);
    }

    public int countTotal(String anio, String mes) {
        String query = """
                SELECT COUNT(*) AS total
                FROM cliente c
                INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4
                WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate
                  AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL)
                """;

        String startDateStr = anio + "-" + mes + "-01";
        MapSqlParameterSource params = new MapSqlParameterSource("startDate", startDateStr);

        Integer count = jdbcTemplate.queryForObject(query, params, Integer.class);
        return count != null ? count : 0;
    }

    public int countAcumulado(String proceso, String fecha, String anio, String mes) {
        java.time.LocalDate date = java.time.LocalDate.parse(fecha);
        String fechaI = date.withDayOfMonth(1).toString();
        String fechaF = date.minusDays(1).toString();

        String queryProcesoFecha = "";
        switch (proceso) {
            case "preLiquidacion":
                queryProcesoFecha = " :fechaI <= lp.preLiquidacionFecha AND lp.preLiquidacionFecha <= :fechaF AND lp.preLiquidacion IN (1,2) ";
                break;
            case "confirmacion":
                queryProcesoFecha = " :fechaI <= lp.confirmacionFecha AND lp.confirmacionFecha <= :fechaF AND lp.confirmacion IN (1,2) ";
                break;
        }

        if (queryProcesoFecha.isEmpty()) {
            return 0;
        }

        String query = "SELECT COUNT(*) AS total "
                + " FROM cliente c "
                + " INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 "
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc "
                + " WHERE " + queryProcesoFecha
                + "   AND CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate "
                + "   AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL) "
                + "   AND (:anio IS NULL OR :anio = '' OR (lp.anio = :anio AND lp.mes = :mes)) ";

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

    public List<LoginProcesosListDTO> listExcelDiario(String proceso, String fechaI, String fechaF) {
        return listExcelDiario(proceso, fechaI, fechaF, null, null);
    }

    public List<LoginProcesosListDTO> listExcelDiario(String proceso, String fechaI, String fechaF, String anio, String mes) {
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

        String sql = " SELECT c.ruc, c.y, c.razonSocial, c.nombreCorto, c.idEstado, cE.descripcion AS estado, "
                + " c.idTipoServicio, tS.abreviatura as tipoServicioAbr, tS.descripcion as tipoServicio, lp.movimiento, "
                + " c.prioridad, lp.anio, lp.mes, "
                + queryProceso
                + " lp.observacion, c.fPle "
                + " FROM cliente c "
                + " INNER JOIN clientsEstados cE ON c.idEstado = cE.id "
                + " LEFT JOIN clientsTipoServicio tS ON c.idTipoServicio = tS.id "
                + " LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc "
                + " WHERE " + queryProcesoFecha
                + "   AND (:anio IS NULL OR :anio = '' OR (lp.anio = :anio AND lp.mes = :mes)) "
                + orderBy;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fechaI", fechaI)
                .addValue("fechaF", fechaF)
                .addValue("anio", anio)
                .addValue("mes", mes);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            String fPleVal = rs.getString("fPle");
            String obs = rs.getString("observacion");

            LoginProcesosListDTO.LoginProcesosListDTOBuilder builder = LoginProcesosListDTO.builder()
                    .ruc(rs.getString("ruc"))
                    .y(rs.getString("y"))
                    .razonSocial(rs.getString("razonSocial"))
                    .nombreCorto(rs.getString("nombreCorto"))
                    .idEstado(rs.getInt("idEstado"))
                    .estado(rs.getString("estado"))
                    .tipoServicio(rs.getString("tipoServicio"))
                    .movimiento(rs.getString("movimiento"))
                    .prioridad(rs.getString("prioridad"))
                    .anio(rs.getString("anio"))
                    .mes(rs.getString("mes"))
                    .observacion(obs == null ? "" : obs)
                    .ple((fPleVal == null || fPleVal.isEmpty()) ? "" : "PLE");

            switch (proceso) {
                case "preLiquidacion":
                    builder.preLiquidacion(rs.getInt("preLiquidacion"))
                            .preLiquidacionFecha(rs.getString("preLiquidacionFecha"))
                            .preLiquidacionUsuario(rs.getString("preLiquidacionUsuario"));
                    break;
                case "confirmacion":
                    builder.confirmacion(rs.getInt("confirmacion"))
                            .confirmacionFecha(rs.getString("confirmacionFecha"))
                            .confirmacionUsuario(rs.getString("confirmacionUsuario"));
                    break;
            }

            return builder.build();
        });
    }

    public int insertUpdate(LoginProcesosDTO dto) {
        String checkSql = "SELECT version FROM loginProcesos WHERE ruc = :ruc AND anio = :anio AND mes = :mes";
        MapSqlParameterSource checkParams = new MapSqlParameterSource()
                .addValue("ruc", dto.getRuc())
                .addValue("anio", dto.getAnio())
                .addValue("mes", dto.getMes());

        List<Integer> versions = jdbcTemplate.query(checkSql, checkParams, (rs, rowNum) -> rs.getInt("version"));

        if (versions.isEmpty()) {
            String insertSql = "INSERT INTO loginProcesos (ruc, anio, mes, movimiento, "
                    + "confirmacion, confirmacionUsuario, confirmacionFecha, confirmacionHora, "
                    + "preLiquidacion, preLiquidacionUsuario, preLiquidacionFecha, preLiquidacionHora, "
                    + "idPropuestaVentas, idPropuestaCompras, observacion, version) "
                    + "VALUES (:ruc, :anio, :mes, :movimiento, "
                    + ":confirmacion, :confirmacionUsuario, :confirmacionFecha, :confirmacionHora, "
                    + ":preLiquidacion, :preLiquidacionUsuario, :preLiquidacionFecha, :preLiquidacionHora, "
                    + ":idPropuestaVentas, :idPropuestaCompras, :observacion, 1)";

            MapSqlParameterSource insertParams = new MapSqlParameterSource()
                    .addValue("ruc", dto.getRuc())
                    .addValue("anio", dto.getAnio())
                    .addValue("mes", dto.getMes())
                    .addValue("movimiento", dto.getMovimiento())
                    .addValue("confirmacion", dto.getConfirmacion())
                    .addValue("confirmacionUsuario", dto.getConfirmacionUsuario())
                    .addValue("confirmacionFecha", dto.getConfirmacionFecha())
                    .addValue("confirmacionHora", dto.getConfirmacionHora())
                    .addValue("preLiquidacion", dto.getPreLiquidacion())
                    .addValue("preLiquidacionUsuario", dto.getPreLiquidacionUsuario())
                    .addValue("preLiquidacionFecha", dto.getPreLiquidacionFecha())
                    .addValue("preLiquidacionHora", dto.getPreLiquidacionHora());

            Integer idPV = null;
            if (dto.getPropuestaVentas() != null
                    && dto.getPropuestaVentas().getId() != 0) {
                idPV = dto.getPropuestaVentas().getId();
            }
            Integer idPC = null;
            if (dto.getPropuestaCompras() != null
                    && dto.getPropuestaCompras().getId() != 0) {
                idPC = dto.getPropuestaCompras().getId();
            }
            insertParams.addValue("idPropuestaVentas", idPV);
            insertParams.addValue("idPropuestaCompras", idPC);
            insertParams.addValue("observacion", dto.getObservacion());

            jdbcTemplate.update(insertSql, insertParams);
            return 1;
        } else {
            Integer currentVersion = versions.get(0);
            if (dto.getVersion() != null && dto.getVersion() != 0 && !dto.getVersion().equals(currentVersion)) {
                throw new org.springframework.dao.OptimisticLockingFailureException("Version mismatch");
            }

            String updateSql = "UPDATE loginProcesos SET "
                    + "movimiento = :movimiento, "
                    + "confirmacion = :confirmacion, confirmacionUsuario = :confirmacionUsuario, confirmacionFecha = :confirmacionFecha, confirmacionHora = :confirmacionHora, "
                    + "preLiquidacion = :preLiquidacion, preLiquidacionUsuario = :preLiquidacionUsuario, preLiquidacionFecha = :preLiquidacionFecha, preLiquidacionHora = :preLiquidacionHora, "
                    + "idPropuestaVentas = :idPropuestaVentas, idPropuestaCompras = :idPropuestaCompras, "
                    + "observacion = :observacion, version = version + 1 "
                    + "WHERE ruc = :ruc AND anio = :anio AND mes = :mes AND version = :version";

            MapSqlParameterSource updateParams = new MapSqlParameterSource()
                    .addValue("ruc", dto.getRuc())
                    .addValue("anio", dto.getAnio())
                    .addValue("mes", dto.getMes())
                    .addValue("movimiento", dto.getMovimiento())
                    .addValue("confirmacion", dto.getConfirmacion())
                    .addValue("confirmacionUsuario", dto.getConfirmacionUsuario())
                    .addValue("confirmacionFecha", dto.getConfirmacionFecha())
                    .addValue("confirmacionHora", dto.getConfirmacionHora())
                    .addValue("preLiquidacion", dto.getPreLiquidacion())
                    .addValue("preLiquidacionUsuario", dto.getPreLiquidacionUsuario())
                    .addValue("preLiquidacionFecha", dto.getPreLiquidacionFecha())
                    .addValue("preLiquidacionHora", dto.getPreLiquidacionHora());

            Integer idPV = null;
            if (dto.getPropuestaVentas() != null
                    && dto.getPropuestaVentas().getId() != 0) {
                idPV = dto.getPropuestaVentas().getId();
            }
            Integer idPC = null;
            if (dto.getPropuestaCompras() != null
                    && dto.getPropuestaCompras().getId() != 0) {
                idPC = dto.getPropuestaCompras().getId();
            }
            updateParams.addValue("idPropuestaVentas", idPV);
            updateParams.addValue("idPropuestaCompras", idPC);
            updateParams.addValue("observacion", dto.getObservacion());
            updateParams.addValue("version", currentVersion != null ? currentVersion : 0);

            int updated = jdbcTemplate.update(updateSql, updateParams);
            if (updated == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Version mismatch or record deleted");
            }
            return 2;
        }
    }

    private String getFechaVencimientoSql(String anio, String mes, String lastAnio) {
        return "CASE WHEN (" + anio + mes + " >= 202310) "
                + "      THEN CASE WHEN si.idCliente IS NOT NULL "
                + "                THEN CASE WHEN c.y = '0' THEN crS.fecha0 WHEN c.y = '1' THEN crS.fecha1 WHEN c.y = '2' THEN crS.fecha2 "
                + "                          WHEN c.y = '3' THEN crS.fecha3 WHEN c.y = '4' THEN crS.fecha4 WHEN c.y = '5' THEN crS.fecha5 "
                + "                          WHEN c.y = '6' THEN crS.fecha6 WHEN c.y = '7' THEN crS.fecha7 WHEN c.y = '8' THEN crS.fecha8 "
                + "                          WHEN c.y = '9' THEN crS.fecha9 END "
                + "                ELSE CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 "
                + "                          WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 "
                + "                          WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN crS.fecha8 "
                + "                          WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab END END "
                + "      ELSE CASE WHEN c.fPle IS NOT NULL THEN "
                + "                CASE WHEN c.fPle != '' THEN "
                + "                     CASE WHEN (" + anio + mes + " >= 202002) AND (" + anio + mes
                + " <= 202008) THEN "
                + "                          CASE WHEN (" + anio + mes + " = 202002) THEN "
                + "                               CASE WHEN (c.y IN ('0','1')) OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 2300)) THEN "
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
                + "                               WHEN (" + anio + mes + " > 202002) THEN "
                + "                                    CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 5000)) THEN "
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
                + "                          CASE WHEN (" + anio + mes + " >= 202002) AND (" + anio + mes
                + " <= 202008) THEN "
                + "                               CASE WHEN (" + anio + mes + " = 202002) THEN "
                + "                                    CASE WHEN (c.y = '0') OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 2300)) THEN "
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
                + "                               WHEN (" + anio + mes + " > 202002) THEN "
                + "                                    CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = "
                + lastAnio + ") >= (u.valor * 5000)) THEN "
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
                + "      END END ";
    }

    private String buildSelectQuery(String anio, String mes) {
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);

        return "SELECT c.idEstado, cE.descripcion AS estado, "
                + " s.idCategoria, nc.abreviatura as abrCategoria, nc.descripcion as descCategoria, "
                + " CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore, "
                + " c.idGrupoEconomico, ge.descripcion as descGrupoEconomico, "
                + " c.ruc, c.y, c.razonSocial, c.nombreCorto, "
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
                + " pDataN.igvJusto, pDataN.pago, pDataN.idBancoPdt621, pb.descripcion AS descBancoPdt621, pDataN.pagarImpuestos, "
                + " pDataN.renta3Categoria, pDataN.rentaPago, pDataN.rentaBanco, pbRenta.descripcion AS descRentaBanco, pDataN.rentaPagarImpuestos, "
                + " lp.version, "
                + getFechaVencimientoSql(anio, mes, lastAnio) + " AS fVencimiento "
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
                + " LEFT JOIN Pdt621Bancos pb ON pDataN.idBancoPdt621 = pb.id "
                + " LEFT JOIN Pdt621Bancos pbRenta ON pDataN.rentaBanco = pbRenta.id "
                + " LEFT JOIN sireData siData ON c.ruc = siData.idCliente AND siData.anio = :anio AND siData.mes = :mes "
                + " LEFT JOIN pdt621registros pR ON p.idCliente = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes "
                + "      AND pR.id = (select MAX(id) from pdt621Registros x where x.idCliente = c.ruc AND x.anio = :anio AND x.mes = :mes AND x.idTipo IN (3,4,5) ) "
                + " LEFT JOIN gestionRegimenesTributarios grt ON c.ruc = grt.idCliente AND grt.anio = :anio AND grt.mes = :mes "
                + " LEFT JOIN regimenesTributarios rt ON grt.idRegimenTributario = rt.id "
                + " WHERE CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :startDate "
                + "   AND (CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :startDate OR p.stAnioHasta IS NULL) ";
    }

    private MapSqlParameterSource buildBaseParams(String anio, String mes) {
        String startDateStr = anio + "-" + mes + "-01";
        String lastAnio = String.valueOf(Integer.parseInt(anio) - 1);

        return new MapSqlParameterSource()
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("startDate", startDateStr)
                .addValue("lastAnio", lastAnio);
    }

    private LoginProcesosListDTO mapRowToListDTO(ResultSet rs, String anio, String mes) throws SQLException {
        int pdt621Registrado = 0;
        int idTipoRegistro = rs.getInt("idTipoRegistro");
        if (idTipoRegistro != 0) {
            String nroOrden = rs.getString("nroOrdenRegistro");
            if (nroOrden != null && !nroOrden.trim().isEmpty()) {
                pdt621Registrado = 1;
            }
        }

        Integer sireCV = null;
        if (rs.getString("rucSire") == null) {
            sireCV = 2;
        } else {
            sireCV = rs.getInt("sireCV");
        }

        String fVenc = rs.getString("fVencimiento");
        Integer diff = null;
        if (fVenc != null && !fVenc.isEmpty()) {
            try {
                diff = (int) DateUtils.getDifferenceInDays(fVenc);
            } catch (Exception e) {
            }
        }

        Integer confirmacionVentas = rs.getObject("confirmacionVentas", Integer.class);
        Integer confirmacionCompras = rs.getObject("confirmacionCompras", Integer.class);
        Integer preLiquidacion = rs.getObject("preLiquidacion", Integer.class);
        Integer confirmacion = rs.getObject("confirmacion", Integer.class);

        int cantSiNa = 0;
        if (confirmacionVentas != null && confirmacionVentas == 1) cantSiNa++;
        if (confirmacionCompras != null && confirmacionCompras == 1) cantSiNa++;
        if (preLiquidacion != null && (preLiquidacion == 1 || preLiquidacion == 2)) cantSiNa++;
        if (confirmacion != null && (confirmacion == 1 || confirmacion == 2)) cantSiNa++;
        if (sireCV != null && (sireCV == 1 || sireCV == 2)) cantSiNa++;
        if (pdt621Registrado == 1) cantSiNa++;

        int avance = (int) Math.round((cantSiNa * 100.0) / 6.0);
        String avanceColorClass = "bg-danger";
        if (avance >= 80) {
            avanceColorClass = "bg-success";
        } else if (avance >= 50) {
            avanceColorClass = "bg-warning";
        }

        return LoginProcesosListDTO.builder()
                .ruc(rs.getString("ruc"))
                .y(rs.getString("y"))
                .razonSocial(rs.getString("razonSocial"))
                .nombreCorto(rs.getString("nombreCorto"))
                .idEstado(rs.getInt("idEstado"))
                .estado(rs.getString("estado"))
                .categoriaAbreviatura(rs.getString("abrCategoria"))
                .categoriaStore(rs.getInt("categoriaStore"))
                .grupoEconomico(rs.getString("descGrupoEconomico"))
                .periodoInicioCom(rs.getString("periodoInicioCom"))
                .periodoConcatenado(anio + "-" + mes)
                .anio(anio)
                .mes(mes)
                .abrGestionRegimenTributario(rs.getString("abrGestionRegimenTributario"))
                .fechaVencimiento(fVenc)
                .difference(diff)
                .movimiento(rs.getString("movimiento"))
                .ventasTotales(rs.getDouble("totalVentas"))
                .comprasTotales(rs.getDouble("totalCompras"))
                .igvPorPagar(rs.getDouble("igvPorPagar"))
                .rentaPorPagar(rs.getDouble("rentaPorPagar"))
                .confirmacionVentas(confirmacionVentas)
                .confirmacionUsuarioVentas(rs.getString("confirmacionUsuarioVentas"))
                .confirmacionFechaVentas(rs.getString("confirmacionFechaVentas"))
                .confirmacionCompras(confirmacionCompras)
                .confirmacionUsuarioCompras(rs.getString("confirmacionUsuarioCompras"))
                .confirmacionFechaCompras(rs.getString("confirmacionFechaCompras"))
                .preLiquidacion(preLiquidacion)
                .preLiquidacionUsuario(rs.getString("preLiquidacionUsuario"))
                .preLiquidacionFecha(rs.getString("preLiquidacionFecha"))
                .confirmacion(confirmacion)
                .confirmacionUsuario(rs.getString("confirmacionUsuario"))
                .confirmacionFecha(rs.getString("confirmacionFecha"))
                .sireCV(sireCV)
                .observacion(rs.getString("observacion"))
                .pdtRegistrado(pdt621Registrado)
                .avance(avance)
                .avanceColorClass(avanceColorClass)
                .descIgvJusto(rs.getInt("igvJusto") == 1 ? "SI" : "NO")
                .descPago(rs.getInt("pago") == 1 ? "SI" : (rs.getInt("pago") == 2 ? "NO" : "-"))
                .descBancoPdt621(rs.getString("descBancoPdt621"))
                .descPagarImpuesto(rs.getInt("pagarImpuestos") == 1 ? "CLIENTE" : "G.COM")
                .descRenta3Categoria(rs.getInt("renta3Categoria") == 1 ? "SI" : "NO")
                .descRentaPago(rs.getInt("rentaPago") == 1 ? "SI" : (rs.getInt("rentaPago") == 2 ? "NO" : "-"))
                .descRentaBanco(rs.getString("descRentaBanco"))
                .descRentaPagarImpuesto(rs.getInt("rentaPagarImpuestos") == 1 ? "CLIENTE" : "G.COM")
                .build();
    }

    public List<LoginProcesosAvanceDTO> listAvanceMensual(String anio, String mes, String fechaInicio, String fechaFin) {
        String fechaMes01 = anio + "-" + mes + "-01";
        String sql = "SELECT \n"
                + "  id, \n"
                + "  proceso, \n"
                + "  fecha, \n"
                + "  SUM(avanceTM) AS avanceTM, \n"
                + "  SUM(avanceTT) AS avanceTT \n"
                + "FROM ( \n"
                + "  -- INLET: recepcionFecha \n"
                + "  SELECT 1 AS id, 'INLET' AS proceso, lp.recepcionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lp.recepcionHora > '06:00' AND lp.recepcionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lp.recepcionHora > '13:00' AND lp.recepcionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginProcesos lp \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lp.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lp.anio = :anio AND lp.mes = :mes AND \n"
                + "  lp.recepcionFecha >= :fechaInicio AND lp.recepcionFecha < :fechaFin \n"
                + "  GROUP BY lp.recepcionFecha \n"
                + "  UNION ALL \n"
                + "  -- WARRANT: archivoFecha \n"
                + "  SELECT 2 AS id, 'WARRANT' AS proceso, lp.archivoFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lp.archivoHora > '06:00' AND lp.archivoHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lp.archivoHora > '13:00' AND lp.archivoHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginProcesos lp \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lp.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lp.anio = :anio AND lp.mes = :mes AND \n"
                + "  lp.archivoFecha >= :fechaInicio AND lp.archivoFecha < :fechaFin \n"
                + "  GROUP BY lp.archivoFecha \n"
                + "  UNION ALL \n"
                + "  -- V-REG: registroFecha \n"
                + "  SELECT 3 AS id, 'V-REG' AS proceso, lv.registroFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lv.registroHora > '06:00' AND lv.registroHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lv.registroHora > '13:00' AND lv.registroHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginVentas lv \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lv.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lv.anio = :anio AND lv.mes = :mes AND \n"
                + "  lv.registroFecha >= :fechaInicio AND lv.registroFecha < :fechaFin \n"
                + "  GROUP BY lv.registroFecha \n"
                + "  UNION ALL \n"
                + "  -- V-VAL: validacionFecha \n"
                + "  SELECT 4 AS id, 'V-VAL' AS proceso, lv.validacionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lv.validacionHora > '06:00' AND lv.validacionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lv.validacionHora > '13:00' AND lv.validacionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginVentas lv \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lv.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lv.anio = :anio AND lv.mes = :mes AND \n"
                + "  lv.validacionFecha >= :fechaInicio AND lv.validacionFecha < :fechaFin \n"
                + "  GROUP BY lv.validacionFecha \n"
                + "  UNION ALL \n"
                + "  -- V-CON: confirmacionFecha \n"
                + "  SELECT 5 AS id, 'V-CON' AS proceso, lv.confirmacionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lv.confirmacionHora > '06:00' AND lv.confirmacionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lv.confirmacionHora > '13:00' AND lv.confirmacionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginVentas lv \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lv.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lv.anio = :anio AND lv.mes = :mes AND \n"
                + "  lv.confirmacionFecha >= :fechaInicio AND lv.confirmacionFecha < :fechaFin \n"
                + "  GROUP BY lv.confirmacionFecha \n"
                + "  UNION ALL \n"
                + "  -- C-REG: registroGeneralFecha \n"
                + "  SELECT 6 AS id, 'C-REG' AS proceso, lc.registroGeneralFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lc.registroGeneralHora > '06:00' AND lc.registroGeneralHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lc.registroGeneralHora > '13:00' AND lc.registroGeneralHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginCompras lc \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lc.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lc.anio = :anio AND lc.mes = :mes AND \n"
                + "  lc.registroGeneralFecha >= :fechaInicio AND lc.registroGeneralFecha < :fechaFin \n"
                + "  GROUP BY lc.registroGeneralFecha \n"
                + "  UNION ALL \n"
                + "  -- C-VAL: validacionFecha \n"
                + "  SELECT 7 AS id, 'C-VAL' AS proceso, lc.validacionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lc.validacionHora > '06:00' AND lc.validacionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lc.validacionHora > '13:00' AND lc.validacionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginCompras lc \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lc.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lc.anio = :anio AND lc.mes = :mes AND \n"
                + "  lc.validacionFecha >= :fechaInicio AND lc.validacionFecha < :fechaFin \n"
                + "  GROUP BY lc.validacionFecha \n"
                + "  UNION ALL \n"
                + "  -- C-CON: confirmacionFecha \n"
                + "  SELECT 8 AS id, 'C-CON' AS proceso, lc.confirmacionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lc.confirmacionHora > '06:00' AND lc.confirmacionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lc.confirmacionHora > '13:00' AND lc.confirmacionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginCompras lc \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lc.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lc.anio = :anio AND lc.mes = :mes AND \n"
                + "  lc.confirmacionFecha >= :fechaInicio AND lc.confirmacionFecha < :fechaFin \n"
                + "  GROUP BY lc.confirmacionFecha \n"
                + "  UNION ALL \n"
                + "  -- TAKE BACK: preLiquidacionFecha \n"
                + "  SELECT 9 AS id, 'TAKE BACK' AS proceso, lp.preLiquidacionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lp.preLiquidacionHora > '06:00' AND lp.preLiquidacionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lp.preLiquidacionHora > '13:00' AND lp.preLiquidacionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginProcesos lp \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lp.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lp.anio = :anio AND lp.mes = :mes AND \n"
                + "  lp.preLiquidacionFecha >= :fechaInicio AND lp.preLiquidacionFecha < :fechaFin \n"
                + "  GROUP BY lp.preLiquidacionFecha \n"
                + "  UNION ALL \n"
                + "  -- TAKE: confirmacionFecha \n"
                + "  SELECT 10 AS id, 'TAKE' AS proceso, lp.confirmacionFecha AS fecha, \n"
                + "         COUNT(CASE WHEN lp.confirmacionHora > '06:00' AND lp.confirmacionHora <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN lp.confirmacionHora > '13:00' AND lp.confirmacionHora <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM loginProcesos lp \n"
                + "  INNER JOIN clienteServiciosTributarios p ON lp.ruc = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  lp.anio = :anio AND lp.mes = :mes AND \n"
                + "  lp.confirmacionFecha >= :fechaInicio AND lp.confirmacionFecha < :fechaFin \n"
                + "  GROUP BY lp.confirmacionFecha \n"
                + "  UNION ALL \n"
                + "  -- ENVOY PDT: fDeclaracion \n"
                + "  SELECT 11 AS id, 'ENVOY PDT' AS proceso, pdt.fDeclaracion AS fecha, \n"
                + "         COUNT(CASE WHEN pdt.hDeclaracion > '06:00' AND pdt.hDeclaracion <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN pdt.hDeclaracion > '13:00' AND pdt.hDeclaracion <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM pdt621DataNew pdt \n"
                + "  INNER JOIN clienteServiciosTributarios p ON pdt.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  pdt.anio = :anio AND pdt.mes = :mes AND \n"
                + "  pdt.fDeclaracion >= :fechaInicio AND pdt.fDeclaracion < :fechaFin \n"
                + "  AND pdt.hDeclaracion IS NOT NULL \n"
                + "  GROUP BY pdt.fDeclaracion \n"
                + "  UNION ALL \n"
                + "  -- ENVOY PLE: fDeclaracion \n"
                + "  SELECT 12 AS id, 'ENVOY PLE' AS proceso, pl.fDeclaracion AS fecha, \n"
                + "         COUNT(CASE WHEN pl.hDeclaracion > '06:00' AND pl.hDeclaracion <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN pl.hDeclaracion > '13:00' AND pl.hDeclaracion <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM pleComprasVentasData pl \n"
                + "  INNER JOIN clienteServiciosTributarios p ON pl.idCliente = p.idCliente AND stIdServicioTributario = 5 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  pl.anio = :anio AND pl.mes = :mes AND \n"
                + "  pl.fDeclaracion >= :fechaInicio AND pl.fDeclaracion < :fechaFin \n"
                + "  AND pl.hDeclaracion IS NOT NULL \n"
                + "  GROUP BY pl.fDeclaracion \n"
                + "  UNION ALL \n"
                + "  -- ENVOY SIRE: fDeclaracion \n"
                + "  SELECT 13 AS id, 'ENVOY SIRE' AS proceso, si.fDeclaracion AS fecha, \n"
                + "         COUNT(CASE WHEN si.hDeclaracion > '06:00' AND si.hDeclaracion <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN si.hDeclaracion > '13:00' AND si.hDeclaracion <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM sireData si \n"
                + "  INNER JOIN clienteServiciosTributarios p ON si.idCliente = p.idCliente AND stIdServicioTributario = 15 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  si.anio = :anio AND si.mes = :mes AND \n"
                + "  si.fDeclaracion >= :fechaInicio AND si.fDeclaracion < :fechaFin \n"
                + "  AND si.hDeclaracion IS NOT NULL \n"
                + "  GROUP BY si.fDeclaracion \n"
                + "  UNION ALL \n"
                + "  -- ENVOY MAIL: fMail \n"
                + "  SELECT 14 AS id, 'ENVOY MAIL' AS proceso, pdt.fMail AS fecha, \n"
                + "         COUNT(CASE WHEN pdt.hMail > '06:00' AND pdt.hMail <= '13:00' THEN 1 END) AS avanceTM, \n"
                + "         COUNT(CASE WHEN pdt.hMail > '13:00' AND pdt.hMail <= '24:00' THEN 1 END) AS avanceTT \n"
                + "  FROM pdt621DataNew pdt \n"
                + "  INNER JOIN clienteServiciosTributarios p ON pdt.idCliente = p.idCliente AND p.stIdServicioTributario = 4 \n"
                + "  WHERE \n"
                + "  CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= :fechaMes01 AND \n"
                + "  ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= :fechaMes01 OR p.stAnioHasta IS NULL ) \n"
                + "  AND \n"
                + "  pdt.anio = :anio AND pdt.mes = :mes AND \n"
                + "  pdt.fMail >= :fechaInicio AND pdt.fMail < :fechaFin \n"
                + "  AND pdt.hMail IS NOT NULL \n"
                + "  GROUP BY pdt.fMail \n"
                + ") AS resumen \n"
                + "GROUP BY id, proceso, fecha \n"
                + "ORDER BY id, fecha ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fechaMes01", fechaMes01)
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("fechaInicio", fechaInicio)
                .addValue("fechaFin", fechaFin);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> LoginProcesosAvanceDTO.builder()
                .idProceso(rs.getInt("id"))
                .descProceso(rs.getString("proceso"))
                .fechaAvance(rs.getString("fecha"))
                .avanceTM(rs.getInt("avanceTM"))
                .avanceTT(rs.getInt("avanceTT"))
                .build());
    }

    public List<LoginProcesosVentaDTO> getListaVentas(String ruc, String anio, String mes) {
        String sql = """
            SELECT v.idVenta, v.ruc, v.anio, v.mes, v.idTipo, v.serie, v.desde, v.hasta, v.fchUltima, tC.descripcion AS TipoDescripcion
            FROM loginProcesos_registroVentas v
            LEFT JOIN loginProcesos_tipoComprobante tC ON v.idTipo = tC.id
            WHERE v.ruc = :ruc AND v.anio = :anio AND v.mes = :mes
            ORDER BY v.serie ASC
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", ruc)
                .addValue("anio", anio)
                .addValue("mes", mes);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> LoginProcesosVentaDTO.builder()
                .idVenta(rs.getInt("idVenta"))
                .ruc(rs.getString("ruc"))
                .anio(rs.getString("anio"))
                .mes(rs.getString("mes"))
                .tipoVenta(new GclassDTO(rs.getInt("idTipo"), rs.getString("TipoDescripcion")))
                .serie(rs.getString("serie"))
                .desde(rs.getString("desde"))
                .hasta(rs.getString("hasta"))
                .fchUltima(rs.getString("fchUltima"))
                .build());
    }

    public int deleteVenta(int idVenta) {
        String sql = "DELETE FROM loginProcesos_registroVentas WHERE idVenta = :idVenta";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("idVenta", idVenta);
        return jdbcTemplate.update(sql, params);
    }
}

