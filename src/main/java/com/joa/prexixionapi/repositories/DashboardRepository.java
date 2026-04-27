package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.CronogramaDetailDTO;
import com.joa.prexixionapi.dto.FiscalizacionSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<FiscalizacionSummaryDTO> findActiveAuditsTax() {
        String sql = "SELECT TOP 5 CASE WHEN ISNULL(c.nombreCorto, '') <> '' THEN c.nombreCorto ELSE c.razonSocial END AS razonSocial, " +
                     "ft.descripcion as tipo, fe.descripcion as estado, " +
                     "f.fPresentacion as fechaLimite, p.nombres + ' ' + p.apellidos as responsable " +
                     "FROM fiscalizaciones f " +
                     "INNER JOIN cliente c ON f.ruc = c.ruc " +
                     "INNER JOIN fiscalizacionesTipos ft ON f.idTipo = ft.id " +
                     "INNER JOIN fiscalizacionesEstados fe ON f.idEstado = fe.id " +
                     "LEFT JOIN personal p ON f.responsableDni = p.dni " +
                     "WHERE f.idEstado IN (1, 2, 5) " + 
                     "ORDER BY f.fPresentacion DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> FiscalizacionSummaryDTO.builder()
                .razonSocial(rs.getString("razonSocial"))
                .tipo(rs.getString("tipo"))
                .estado(rs.getString("estado"))
                .fechaLimite(rs.getString("fechaLimite"))
                .responsable(rs.getString("responsable"))
                .build());
    }

    public List<FiscalizacionSummaryDTO> findActiveAuditsPay() {
        String sql = "SELECT TOP 5 CASE WHEN ISNULL(c.nombreCorto, '') <> '' THEN c.nombreCorto ELSE c.razonSocial END AS razonSocial, " +
                     "fpd.descripcion as tipo, fle.descripcion as estado, " +
                     "fl.fLimiteRespuesta as fechaLimite, p.nombres + ' ' + p.apellidos as responsable " +
                     "FROM fiscalizacionesLaborales fl " +
                     "INNER JOIN cliente c ON fl.idCliente = c.ruc " +
                     "LEFT JOIN fiscalizacionesLaboralesDocumento fpd ON fl.idFLDocumento = fpd.id " +
                     "LEFT JOIN fiscalizacionesLaboralesEstado fle ON fl.idFLEstado = fle.id " +
                     "LEFT JOIN personal p ON fl.idResponsable = p.dni " +
                     "WHERE fl.idFLEstado IN (2, 10) " + 
                     "ORDER BY fl.fLimiteRespuesta DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> FiscalizacionSummaryDTO.builder()
                .razonSocial(rs.getString("razonSocial"))
                .tipo(rs.getString("tipo"))
                .estado(rs.getString("estado"))
                .fechaLimite(rs.getString("fechaLimite"))
                .responsable(rs.getString("responsable"))
                .build());
    }

    public List<FiscalizacionSummaryDTO> findActiveAuditsReclamos() {
        String sql = "SELECT TOP 5 CASE WHEN ISNULL(c.nombreCorto, '') <> '' THEN c.nombreCorto ELSE c.razonSocial END AS razonSocial, " +
                     "ft.descripcion as tipo, fe.descripcion as estado, " +
                     "f.fPresentacion as fechaLimite, p.nombres + ' ' + p.apellidos as responsable " +
                     "FROM fiscalizaciones f " +
                     "INNER JOIN cliente c ON f.ruc = c.ruc " +
                     "INNER JOIN fiscalizacionesTipos ft ON f.idTipo = ft.id " +
                     "INNER JOIN fiscalizacionesEstados fe ON f.idEstado = fe.id " +
                     "LEFT JOIN personal p ON f.responsableDni = p.dni " +
                     "WHERE f.idEstado = 8 " + 
                     "ORDER BY f.fPresentacion DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> FiscalizacionSummaryDTO.builder()
                .razonSocial(rs.getString("razonSocial"))
                .tipo(rs.getString("tipo"))
                .estado(rs.getString("estado"))
                .fechaLimite(rs.getString("fechaLimite"))
                .responsable(rs.getString("responsable"))
                .build());
    }

    public List<CronogramaDetailDTO> findUpcomingSchedules(String periodo) {
        // This is a simplified version, ideally would join with client RUCS or similar
        // For the dashboard summary, we often want the general ones or for specific clients
        String sql = "SELECT 'PDT' as tipo, anio + '-' + mes as periodo, 'Varios' as ruc, fecha0 as fechaVencimiento " +
                     "FROM CRONOGRAMAPDT WHERE anio + '-' + mes >= ? ORDER BY anio, mes ASC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> CronogramaDetailDTO.builder()
                .tipo(rs.getString("tipo"))
                .periodo(rs.getString("periodo"))
                .ruc(rs.getString("ruc"))
                .fechaVencimiento(rs.getString("fechaVencimiento"))
                .estado("PENDIENTE")
                .build(), periodo);
    }
    
    public long countPendingTasks() {
        // Contamos solo tareas en 'Stand By' (1) o 'Proceso' (3) según requerimiento
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tareasDiarias WHERE idEstado IN (1, 3)", Long.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public com.joa.prexixionapi.dto.ProcesosSummaryDTO getProcesosSummary(String anio, String mes) {
        String sql = "SELECT lp.recepcion, lp.archivo, lp.preLiquidacion, lp.confirmacion, " +
                     "ple.registrado as pleCV, pR.idTipo " +
                     "FROM cliente c " +
                     "INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 " +
                     "LEFT JOIN loginProcesos lp ON lp.ruc = c.ruc AND lp.anio = ? AND lp.mes = ? " +
                     "LEFT JOIN pleComprasVentasData ple ON ple.idCliente = c.ruc AND ple.anio = ? AND ple.mes = ? " +
                     "LEFT JOIN pdt621registros pR ON p.idCliente = pR.idCliente AND pR.anio = ? AND pR.mes = ? " +
                     "WHERE c.idEstado = 1"; // Solo clientes activos

        com.joa.prexixionapi.dto.ProcesosSummaryDTO summary = new com.joa.prexixionapi.dto.ProcesosSummaryDTO();
        
        jdbcTemplate.query(sql, rs -> {
            summary.setTotal(summary.getTotal() + 1);
            
            // Recepcion
            int rec = rs.getInt("recepcion");
            if (rec == 1) summary.setRecepcionSi(summary.getRecepcionSi() + 1);
            else if (rec == 0) summary.setRecepcionNo(summary.getRecepcionNo() + 1);
            else summary.setRecepcionNa(summary.getRecepcionNa() + 1);
            
            // Archivo
            int arc = rs.getInt("archivo");
            if (arc == 1) summary.setArchivoSi(summary.getArchivoSi() + 1);
            else if (arc == 0) summary.setArchivoNo(summary.getArchivoNo() + 1);
            else summary.setArchivoNa(summary.getArchivoNa() + 1);
            
            // Pre-Liquidacion (Validacion en el UI)
            int pre = rs.getInt("preLiquidacion");
            if (pre == 1) summary.setValidacionSi(summary.getValidacionSi() + 1);
            else if (pre == 0) summary.setValidacionNo(summary.getValidacionNo() + 1);
            else summary.setValidacionNa(summary.getValidacionNa() + 1);
            
            // PDT (Basado en si hay un registro de tipo PDT 3,4,5)
            if (rs.getInt("idTipo") > 0) summary.setPdtSi(summary.getPdtSi() + 1);
            else summary.setPdtNo(summary.getPdtNo() + 1);
            
            // PLE
            int pl = rs.getInt("pleCV");
            if (pl == 1) summary.setPleSi(summary.getPleSi() + 1);
            else if (pl == 0) summary.setPleNo(summary.getPleNo() + 1);
            else summary.setPleNa(summary.getPleNa() + 1);
            
        }, anio, mes, anio, mes, anio, mes);

        return summary;
    }

    public Double getUIT() {
        try {
            return jdbcTemplate.queryForObject("SELECT TOP 1 valor FROM uit ORDER BY anio DESC", Double.class);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public com.joa.prexixionapi.dto.OperationStatsDTO getOperationStats(String anio, String mes) {
        com.joa.prexixionapi.dto.OperationStatsDTO stats = new com.joa.prexixionapi.dto.OperationStatsDTO();

        // 1. Inventarios (loginInventarios - confirmado = 1 es completado)
        String sqlInv = "SELECT " +
                       "SUM(CASE WHEN li.confirmado = 1 THEN 1 ELSE 0 END) as si, " +
                       "SUM(CASE WHEN li.confirmado = 0 OR li.confirmado IS NULL THEN 1 ELSE 0 END) as no " +
                       "FROM cliente c " +
                       "INNER JOIN clienteServiciosOtros p ON c.ruc = p.idCliente AND p.soIdServicioOtro = 6 " + // Servicio de Inventarios
                       "LEFT JOIN loginInventarios li ON li.ruc = c.ruc AND li.anio = ? AND li.mes = ? " +
                       "WHERE c.idEstado = 1";
        
        jdbcTemplate.query(sqlInv, rs -> {
            stats.setInventariosSi(rs.getInt("si"));
            stats.setInventariosNo(rs.getInt("no"));
        }, anio, mes);

        // 2. Balances (loginBalances - vb = 1 es Visto Bueno/Completado)
        String sqlBal = "SELECT " +
                       "SUM(CASE WHEN lb.vb = 1 THEN 1 ELSE 0 END) as si, " +
                       "SUM(CASE WHEN lb.vb = 0 OR lb.vb IS NULL THEN 1 ELSE 0 END) as no " +
                       "FROM cliente c " +
                       "INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 1 " + // Contabilidad
                       "LEFT JOIN loginBalances lb ON lb.ruc = c.ruc AND lb.anio = ? AND lb.mes = ? " +
                       "WHERE c.idEstado = 1";

        jdbcTemplate.query(sqlBal, rs -> {
            stats.setBalancesSi(rs.getInt("si"));
            stats.setBalancesNo(rs.getInt("no"));
        }, anio, mes);

        // 3. PDT 621 (pdt621registros)
        String sql621 = "SELECT " +
                       "SUM(CASE WHEN pR.id IS NOT NULL THEN 1 ELSE 0 END) as si, " +
                       "SUM(CASE WHEN pR.id IS NULL THEN 1 ELSE 0 END) as no " +
                       "FROM cliente c " +
                       "INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 1 " + // Contabilidad
                       "LEFT JOIN pdt621registros pR ON pR.idCliente = c.ruc AND pR.anio = ? AND pR.mes = ? AND pR.idTipo IN (3,4,5) " +
                       "WHERE c.idEstado = 1";
        
        jdbcTemplate.query(sql621, rs -> {
            stats.setPdt621Si(rs.getInt("si"));
            stats.setPdt621No(rs.getInt("no"));
        }, anio, mes);

        // 4. PDT 601
        String sql601 = "SELECT " +
                       "SUM(CASE WHEN pR.id IS NOT NULL THEN 1 ELSE 0 END) as si, " +
                       "SUM(CASE WHEN pR.id IS NULL THEN 1 ELSE 0 END) as no " +
                       "FROM cliente c " +
                       "INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 " +
                       "LEFT JOIN pdt601registros pR ON pR.idCliente = c.ruc AND pR.anio = ? AND pR.mes = ? AND pR.idTipo IN (3,4,5) " +
                       "WHERE c.idEstado = 1";

        jdbcTemplate.query(sql601, rs -> {
            stats.setPdt601Si(rs.getInt("si"));
            stats.setPdt601No(rs.getInt("no"));
        }, anio, mes);

        return stats;
    }

    public com.joa.prexixionapi.dto.PaycomStatsDTO getPaycomStats(String anio, String mes) {
        String sql = "SELECT " +
                     "AVG(CAST(CASE WHEN lp.vistoBueno = 1 THEN 100 ELSE 0 END AS FLOAT)) as tareoAvance, " +
                     "AVG(CAST(CASE WHEN lr.take = 1 THEN 100 ELSE 0 END AS FLOAT)) as rxhAvance, " +
                     "AVG(CAST(CASE WHEN p6.pdt601 = 1 THEN 100 ELSE 0 END AS FLOAT)) as envoyAvance, " +
                     "COUNT(*) as total " +
                     "FROM cliente c " +
                     "INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 " +
                     "LEFT JOIN loginPlame lp ON lp.ruc = c.ruc AND lp.anio = ? AND lp.mes = ? " +
                     "LEFT JOIN loginRxh lr ON lr.ruc = c.ruc AND lr.anio = ? AND lr.mes = ? " +
                     "LEFT JOIN Pdt601Data p6 ON p6.idCliente = c.ruc AND p6.anio = ? AND p6.mes = ? " +
                     "WHERE c.idEstado = 1";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> com.joa.prexixionapi.dto.PaycomStatsDTO.builder()
                .tareoAvance(Math.round(rs.getDouble("tareoAvance") * 100.0) / 100.0)
                .rxhAvance(Math.round(rs.getDouble("rxhAvance") * 100.0) / 100.0)
                .envoyAvance(Math.round(rs.getDouble("envoyAvance") * 100.0) / 100.0)
                .totalProcesados(rs.getInt("total"))
                .totalPendientes(0)
                .build(), anio, mes, anio, mes, anio, mes);
        } catch (Exception e) {
            return new com.joa.prexixionapi.dto.PaycomStatsDTO();
        }
    }
    public com.joa.prexixionapi.dto.ChartDataDTO getClientStatusDistribution() {
        String sql = "SELECT ce.descripcion, COUNT(*) as cantidad " +
                     "FROM cliente c " +
                     "INNER JOIN clientsEstados ce ON c.idEstado = ce.id " +
                     "GROUP BY ce.descripcion";
        
        com.joa.prexixionapi.dto.ChartDataDTO chartData = new com.joa.prexixionapi.dto.ChartDataDTO(new java.util.ArrayList<>(), new java.util.ArrayList<>());
        
        jdbcTemplate.query(sql, rs -> {
            chartData.getLabels().add(rs.getString("descripcion"));
            chartData.getData().add(rs.getLong("cantidad"));
        });
        
        return chartData;
    }

    public com.joa.prexixionapi.dto.ChartDataDTO getTaskStatusDistribution() {
        String sql = "SELECT tde.descripcion, COUNT(*) as cantidad " +
                     "FROM tareasDiarias td " +
                     "INNER JOIN tareasDiariasEstados tde ON td.idEstado = tde.id " +
                     "GROUP BY tde.descripcion";
        
        com.joa.prexixionapi.dto.ChartDataDTO chartData = new com.joa.prexixionapi.dto.ChartDataDTO(new java.util.ArrayList<>(), new java.util.ArrayList<>());
        
        jdbcTemplate.query(sql, rs -> {
            chartData.getLabels().add(rs.getString("descripcion"));
            chartData.getData().add(rs.getLong("cantidad"));
        });
        
        return chartData;
    }

    public com.joa.prexixionapi.dto.ChartDataDTO getServiceDistribution() {
        String sql = "SELECT cts.descripcion, COUNT(*) as cantidad " +
                     "FROM cliente c " +
                     "INNER JOIN clientsTipoServicio cts ON c.idTipoServicio = cts.id " +
                     "WHERE c.idEstado = 1 " + // Solo clientes activos
                     "GROUP BY cts.descripcion";
        
        com.joa.prexixionapi.dto.ChartDataDTO chartData = new com.joa.prexixionapi.dto.ChartDataDTO(new java.util.ArrayList<>(), new java.util.ArrayList<>());
        
        jdbcTemplate.query(sql, rs -> {
            chartData.getLabels().add(rs.getString("descripcion"));
            chartData.getData().add(rs.getLong("cantidad"));
        });
        
        return chartData;
    }

    public com.joa.prexixionapi.dto.AttendanceStatsDTO getAttendanceStats() {
        // Obtenemos los marcajes de hoy
        String sql = "SELECT p.pin, p.m_i as mi, p.m_s as ms, p.t_i as ti, p.t_s as ts, p.tarde, " +
                     "per.nombres + ' ' + per.apellidos as nombre " +
                     "FROM ( " +
                     "  SELECT pin, CONVERT(date, time) as fecha, CONVERT(time(0), time) as tiempo, " +
                     "  CASE " +
                     "    WHEN CONVERT(time, time) < '10:00:00' AND CONVERT(time, time) > '05:00:00' THEN 'm_i' " +
                     "    WHEN CONVERT(time, time) < '13:30:00' AND CONVERT(time, time) > '12:00:00' THEN 'm_s' " +
                     "    WHEN CONVERT(time, time) < '15:00:00' AND CONVERT(time, time) > '13:31:00' THEN 't_i' " +
                     "    WHEN CONVERT(time, time) >= '17:00:00' THEN 't_s' " +
                     "    ELSE 'tarde' " +
                     "  END AS Turno " +
                     "  FROM ZKAccess1.dbo.acc_monitor_log " +
                     "  WHERE CONVERT(date, time) = CONVERT(date, GETDATE()) " +
                     ") AS SourceTable PIVOT(min(tiempo) FOR [Turno] IN ([m_i],[m_s],[t_i],[t_s],[tarde])) AS p " +
                     "INNER JOIN bty.dbo.personal per ON p.pin = per.dni COLLATE DATABASE_DEFAULT " +
                     "WHERE per.idEstado in (1,2)";

        com.joa.prexixionapi.dto.AttendanceStatsDTO stats = com.joa.prexixionapi.dto.AttendanceStatsDTO.builder()
                .lateDetails(new java.util.ArrayList<>())
                .build();

        jdbcTemplate.query(sql, rs -> {
            boolean isPresent = false;
            String mi = rs.getString("mi");
            String ms = rs.getString("ms");
            String ti = rs.getString("ti");
            String ts = rs.getString("ts");
            String tarde = rs.getString("tarde");

            // Lógica de presencia: Si marcó entrada y no ha marcado la salida correspondiente
            if (mi != null && ms == null) isPresent = true;
            if (ti != null && ts == null) isPresent = true;

            if (isPresent) stats.setPresentCount(stats.getPresentCount() + 1);

            // Lógica de tardanza
            boolean isLate = false;
            String lateTime = "";
            
            // Si marcó entrada mañana tarde (> 09:05)
            if (mi != null && mi.compareTo("09:05:59") > 0) {
                isLate = true;
                lateTime = mi;
            }
            // Si marcó entrada tarde tarde (> 14:05)
            if (ti != null && ti.compareTo("14:05:59") > 0) {
                isLate = true;
                lateTime = ti;
            }
            // O si tiene un marcaje en el turno 'tarde' (fuera de ventanas normales)
            if (tarde != null) {
                isLate = true;
                lateTime = tarde;
            }

            if (isLate) {
                stats.setLateCount(stats.getLateCount() + 1);
                stats.getLateDetails().add(com.joa.prexixionapi.dto.AttendanceDetailDTO.builder()
                        .name(rs.getString("nombre"))
                        .time(lateTime)
                        .status("TARDE")
                        .build());
            }
        });

        return stats;
    }

    public List<com.joa.prexixionapi.dto.TareaDiariaDTO> findTasksForUser(String dni) {
        String sql = "SELECT TOP 50 td.id, td.empresa, tdp.descripcion as proceso, td.detalle, " +
                     "td.porcentajeAvanceTrabajo, tde.descripcion as estado, td.idEstado, " +
                     "p.nombres + ' ' + p.apellidos as responsable, td.fechaInicio, td.fechaFin " +
                     "FROM tareasDiarias td " +
                     "LEFT JOIN tareasDiariasProcesos tdp ON td.idProceso = tdp.id " +
                     "LEFT JOIN tareasDiariasEstados tde ON td.idEstado = tde.id " +
                     "LEFT JOIN personal p ON td.idResponsable = p.dni " +
                     "WHERE (td.idResponsable = ? OR td.supervisadoPor = ? " +
                     "OR td.idResponsable IN (SELECT dniAsistente FROM asignacionAsistentes WHERE dniJefeArea = ?)) " +
                     "AND td.idEstado IN (1, 3) " +
                     "ORDER BY td.idEstado DESC, td.id DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> com.joa.prexixionapi.dto.TareaDiariaDTO.builder()
                .id(rs.getLong("id"))
                .empresa(rs.getString("empresa"))
                .proceso(rs.getString("proceso"))
                .detalle(rs.getString("detalle"))
                .porcentajeAvance(rs.getDouble("porcentajeAvanceTrabajo"))
                .idEstado(rs.getInt("idEstado"))
                .estado(rs.getString("estado"))
                .responsable(rs.getString("responsable"))
                .fechaInicio(rs.getString("fechaInicio"))
                .fechaFin(rs.getString("fechaFin"))
                .build(), dni, dni, dni);
    }
}
