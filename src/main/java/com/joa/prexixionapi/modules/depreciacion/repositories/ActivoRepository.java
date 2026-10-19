package com.joa.prexixionapi.modules.depreciacion.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoDepreciacionDTO;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoRepository;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoDTO;

@Repository
@RequiredArgsConstructor
public class ActivoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ActivoDTO> findByIdCliente(String idCliente) {
        String sql = """
                SELECT 
                    b.id, b.idProveedor, b.proveedor, b.descripcion, b.marca, b.modelo, b.seriePlaca, 
                    b.idTipo, t.descripcion as tipo, 
                    t.depreciacionContable as pctContable, 
                    t.depreciacionTributaria as pctTributario, 
                    b.cuenta, b.documento, b.fechaInicio, b.fechaFinContable, b.fechaFinTributaria, 
                    b.idMoneda, m.descripcion as moneda, tc.t_venta as tipoCambio, b.precioUnitario, b.cantidad, b.costoInicial, 
                    b.idEstado, e.descripcion as estado, b.fechaBaja, b.fechaCompra, b.bloqueado, b.idCliente,
                    (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio) 
                     FROM clienteAltaCom x WHERE x.idCliente = b.idCliente ORDER BY acInicioCom DESC) as altaCom
                FROM activos b
                LEFT JOIN bienesTipos t ON b.idTipo = t.id
                LEFT JOIN monedas m ON b.idMoneda = m.abreviatura
                LEFT JOIN bienesEstados e ON b.idEstado = e.id
                LEFT JOIN prexixion.dbo.tcambio tc ON tc.fecha = b.fechaInicio
                WHERE b.idCliente = :idCliente 
                ORDER BY b.id
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("idCliente", idCliente), (rs, rowNum) -> {
            Integer bloqueado = rs.getObject("bloqueado") != null ? rs.getInt("bloqueado") : null;
            String bloqueadoDesc = (bloqueado != null && bloqueado == 1) ? "SÍ" : "";
            Double costInit = rs.getObject("costoInicial") != null ? rs.getDouble("costoInicial") : 0.0;
            String mon = rs.getString("idMoneda");
            Double tCambio = rs.getObject("tipoCambio") != null ? rs.getDouble("tipoCambio") : null;
            Double pUni = rs.getObject("precioUnitario") != null ? rs.getDouble("precioUnitario") : null;
            Integer cant = rs.getObject("cantidad") != null ? rs.getInt("cantidad") : null;

            if ((costInit == null || costInit <= 0.0) && pUni != null && pUni > 0 && cant != null && cant > 0) {
                if ("USD".equals(mon)) {
                    double tcRaw = (tCambio != null && tCambio > 0) ? tCambio : 1.0;
                    costInit = pUni * cant * tcRaw;
                } else {
                    costInit = pUni * cant;
                }
            }

            return ActivoDTO.builder()
                    .id(rs.getString("id"))
                    .idCliente(rs.getString("idCliente"))
                    .idProveedor(rs.getString("idProveedor"))
                    .proveedor(rs.getString("proveedor"))
                    .descripcion(rs.getString("descripcion"))
                    .marca(rs.getString("marca"))
                    .modelo(rs.getString("modelo"))
                    .seriePlaca(rs.getString("seriePlaca"))
                    .idTipo(rs.getString("idTipo"))
                    .tipo(rs.getString("tipo"))
                    .porcentajeDepreciacionContable(rs.getObject("pctContable") != null ? rs.getDouble("pctContable") : null)
                    .porcentajeDepreciacionTributaria(rs.getObject("pctTributario") != null ? rs.getDouble("pctTributario") : null)
                    .cuenta(rs.getString("cuenta"))
                    .documento(rs.getString("documento"))
                    .fechaInicio(rs.getString("fechaInicio"))
                    .fechaFinContable(rs.getString("fechaFinContable"))
                    .fechaFinTributaria(rs.getString("fechaFinTributaria"))
                    .idMoneda(mon)
                    .moneda(rs.getString("moneda"))
                    .tipoCambio(tCambio)
                    .precioUnitario(pUni)
                    .cantidad(cant)
                    .costoInicial(costInit)
                    .idEstado(rs.getObject("idEstado") != null ? rs.getInt("idEstado") : null)
                    .estado(rs.getString("estado"))
                    .fechaBaja(rs.getString("fechaBaja"))
                    .fechaCompra(rs.getString("fechaCompra"))
                    .bloqueado(bloqueado)
                    .bloqueadoDesc(bloqueadoDesc)
                    .build();
        });
    }

    public ActivoDTO findByClienteAndId(String idCliente, String id) {
        String sql = """
                SELECT 
                    b.id, b.idProveedor, b.proveedor, b.descripcion, b.marca, b.modelo, b.seriePlaca, 
                    b.idTipo, t.descripcion as tipo, 
                    t.depreciacionContable as pctContable, 
                    t.depreciacionTributaria as pctTributario, 
                    b.cuenta, b.documento, b.fechaInicio, b.fechaFinContable, b.fechaFinTributaria, 
                    b.idMoneda, m.descripcion as moneda, tc.t_venta as tipoCambio, b.precioUnitario, b.cantidad, b.costoInicial, 
                    b.idEstado, e.descripcion as estado, b.fechaBaja, b.fechaCompra, b.bloqueado, b.idCliente,
                    (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio) 
                     FROM clienteAltaCom x WHERE x.idCliente = b.idCliente ORDER BY acInicioCom DESC) as altaCom
                FROM activos b
                LEFT JOIN bienesTipos t ON b.idTipo = t.id
                LEFT JOIN monedas m ON b.idMoneda = m.abreviatura
                LEFT JOIN bienesEstados e ON b.idEstado = e.id
                LEFT JOIN prexixion.dbo.tcambio tc ON tc.fecha = b.fechaInicio
                WHERE b.idCliente = :idCliente AND b.id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("id", id);
        List<ActivoDTO> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Integer bloqueado = rs.getObject("bloqueado") != null ? rs.getInt("bloqueado") : null;
            String bloqueadoDesc = (bloqueado != null && bloqueado == 1) ? "SÍ" : "";
            Double costInit = rs.getObject("costoInicial") != null ? rs.getDouble("costoInicial") : 0.0;
            String mon = rs.getString("idMoneda");
            Double tCambio = rs.getObject("tipoCambio") != null ? rs.getDouble("tipoCambio") : null;
            Double pUni = rs.getObject("precioUnitario") != null ? rs.getDouble("precioUnitario") : null;
            Integer cant = rs.getObject("cantidad") != null ? rs.getInt("cantidad") : null;

            if ((costInit == null || costInit <= 0.0) && pUni != null && pUni > 0 && cant != null && cant > 0) {
                if ("USD".equals(mon)) {
                    double tcRaw = (tCambio != null && tCambio > 0) ? tCambio : 1.0;
                    costInit = pUni * cant * tcRaw;
                } else {
                    costInit = pUni * cant;
                }
            }

            return ActivoDTO.builder()
                    .id(rs.getString("id"))
                    .idCliente(rs.getString("idCliente"))
                    .idProveedor(rs.getString("idProveedor"))
                    .proveedor(rs.getString("proveedor"))
                    .descripcion(rs.getString("descripcion"))
                    .marca(rs.getString("marca"))
                    .modelo(rs.getString("modelo"))
                    .seriePlaca(rs.getString("seriePlaca"))
                    .idTipo(rs.getString("idTipo"))
                    .tipo(rs.getString("tipo"))
                    .porcentajeDepreciacionContable(rs.getObject("pctContable") != null ? rs.getDouble("pctContable") : null)
                    .porcentajeDepreciacionTributaria(rs.getObject("pctTributario") != null ? rs.getDouble("pctTributario") : null)
                    .cuenta(rs.getString("cuenta"))
                    .documento(rs.getString("documento"))
                    .fechaInicio(rs.getString("fechaInicio"))
                    .fechaFinContable(rs.getString("fechaFinContable"))
                    .fechaFinTributaria(rs.getString("fechaFinTributaria"))
                    .idMoneda(mon)
                    .moneda(rs.getString("moneda"))
                    .tipoCambio(tCambio)
                    .precioUnitario(pUni)
                    .cantidad(cant)
                    .costoInicial(costInit)
                    .idEstado(rs.getObject("idEstado") != null ? rs.getInt("idEstado") : null)
                    .estado(rs.getString("estado"))
                    .fechaBaja(rs.getString("fechaBaja"))
                    .fechaCompra(rs.getString("fechaCompra"))
                    .bloqueado(bloqueado)
                    .bloqueadoDesc(bloqueadoDesc)
                    .build();
        });
        return list.isEmpty() ? null : list.get(0);
    }

    public long countByClienteAndId(String idCliente, String id) {
        String sql = "SELECT COUNT(*) FROM activos WHERE idCliente = :idCliente AND id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("id", id);
        Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null ? count : 0;
    }

    public int deleteByClienteAndId(String idCliente, String id) {
        String sql = "DELETE FROM activos WHERE idCliente = :idCliente AND id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("id", id);
        return jdbcTemplate.update(sql, params);
    }

    public int updateBloqueo(String idCliente, List<String> ids, int status) {
        if (ids == null || ids.isEmpty()) return 0;
        String sql = "UPDATE activos SET bloqueado = :status WHERE idCliente = :idCliente AND id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("idCliente", idCliente)
                .addValue("ids", ids);
        return jdbcTemplate.update(sql, params);
    }

    public List<ActivoDTO> findActivosFijosRVExcel(String idCliente) {
        String sql = """
                SELECT 
                    b.id, b.idProveedor, b.proveedor, b.descripcion, b.marca, b.modelo, b.seriePlaca, 
                    b.idTipo, t.descripcion as tipo, 
                    t.depreciacionContable as pctContable, 
                    t.depreciacionTributaria as pctTributario, 
                    b.cuenta, b.documento, b.fechaInicio, b.fechaFinContable, b.fechaFinTributaria, 
                    b.idMoneda, m.descripcion as moneda, tc.t_venta as tipoCambio, b.precioUnitario, b.cantidad, b.costoInicial, 
                    b.idEstado, e.descripcion as estado, b.fechaBaja, b.fechaCompra, b.bloqueado, b.idCliente,
                    (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio) 
                     FROM clienteAltaCom x WHERE x.idCliente = b.idCliente ORDER BY acInicioCom DESC) as altaCom
                FROM activos b
                LEFT JOIN bienesTipos t ON b.idTipo = t.id
                LEFT JOIN monedas m ON b.idMoneda = m.abreviatura
                LEFT JOIN bienesEstados e ON b.idEstado = e.id
                LEFT JOIN prexixion.dbo.tcambio tc ON tc.fecha = b.fechaInicio
                WHERE b.idCliente = :idCliente AND b.idTipo = 'RV' 
                ORDER BY b.id
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("idCliente", idCliente), (rs, rowNum) -> {
            Integer bloqueado = rs.getObject("bloqueado") != null ? rs.getInt("bloqueado") : null;
            String bloqueadoDesc = (bloqueado != null && bloqueado == 1) ? "SÍ" : "";
            Double costInit = rs.getObject("costoInicial") != null ? rs.getDouble("costoInicial") : 0.0;
            String mon = rs.getString("idMoneda");
            Double tCambio = rs.getObject("tipoCambio") != null ? rs.getDouble("tipoCambio") : null;
            Double pUni = rs.getObject("precioUnitario") != null ? rs.getDouble("precioUnitario") : null;
            Integer cant = rs.getObject("cantidad") != null ? rs.getInt("cantidad") : null;

            if ((costInit == null || costInit <= 0.0) && pUni != null && pUni > 0 && cant != null && cant > 0) {
                if ("USD".equals(mon)) {
                    double tcRaw = (tCambio != null && tCambio > 0) ? tCambio : 1.0;
                    costInit = pUni * cant * tcRaw;
                } else {
                    costInit = pUni * cant;
                }
            }

            return ActivoDTO.builder()
                    .id(rs.getString("id"))
                    .idCliente(rs.getString("idCliente"))
                    .idProveedor(rs.getString("idProveedor"))
                    .proveedor(rs.getString("proveedor"))
                    .descripcion(rs.getString("descripcion"))
                    .marca(rs.getString("marca"))
                    .modelo(rs.getString("modelo"))
                    .seriePlaca(rs.getString("seriePlaca"))
                    .idTipo(rs.getString("idTipo"))
                    .tipo(rs.getString("tipo"))
                    .porcentajeDepreciacionContable(rs.getObject("pctContable") != null ? rs.getDouble("pctContable") : null)
                    .porcentajeDepreciacionTributaria(rs.getObject("pctTributario") != null ? rs.getDouble("pctTributario") : null)
                    .cuenta(rs.getString("cuenta"))
                    .documento(rs.getString("documento"))
                    .fechaInicio(rs.getString("fechaInicio"))
                    .fechaFinContable(rs.getString("fechaFinContable"))
                    .fechaFinTributaria(rs.getString("fechaFinTributaria"))
                    .idMoneda(mon)
                    .moneda(rs.getString("moneda"))
                    .tipoCambio(tCambio)
                    .precioUnitario(pUni)
                    .cantidad(cant)
                    .costoInicial(costInit)
                    .idEstado(rs.getObject("idEstado") != null ? rs.getInt("idEstado") : null)
                    .estado(rs.getString("estado"))
                    .fechaBaja(rs.getString("fechaBaja"))
                    .fechaCompra(rs.getString("fechaCompra"))
                    .bloqueado(bloqueado)
                    .bloqueadoDesc(bloqueadoDesc)
                    .build();
        });
    }

    public List<ActivoDTO> findActivosFijosExcelWithDepreciations(String idCliente, String anio) {
        String sql = """
                SELECT 
                    b.id, b.idProveedor, b.proveedor, b.descripcion, b.marca, b.modelo, b.seriePlaca, 
                    b.idTipo, t.descripcion as tipo, t.depreciacionContable as pctContable, 
                    t.depreciacionTributaria as pctTributario, b.cuenta, b.documento, b.fechaInicio, 
                    b.fechaFinContable, b.fechaFinTributaria, b.idMoneda, m.descripcion as moneda, 
                    tc.t_venta as tipoCambio, b.precioUnitario, b.cantidad, b.costoInicial, 
                    b.idEstado, e.descripcion as estado, b.fechaBaja, b.fechaCompra, b.bloqueado, b.idCliente,
                    (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio) 
                     FROM clienteAltaCom x WHERE x.idCliente = b.idCliente ORDER BY acInicioCom DESC) as altaCom,
                    -- JOINED DATA
                    d.idCliente as d_idCliente, d.idBien as d_idBien, d.anio as d_anio, d.idTipo as d_idTipo, d.activoSaldoInicial as d_activoSaldoInicial, 
                    d.activoCompras as d_activoCompras, d.activoRetiros as d_activoRetiros, d.activoSaldoFinal as d_activoSaldoFinal, d.inicial as d_inicial, 
                    d.ene as d_ene, d.feb as d_feb, d.mar as d_mar, d.abr as d_abr, d.may as d_may, d.jun as d_jun, d.jul as d_jul, d.ago as d_ago, 
                    d.sep as d_sep, d.oct as d_oct, d.nov as d_nov, d.[dec] as d_dec, d.total as d_total, d.retiros as d_retiros, 
                    d.saldoFinal as d_saldoFinal, d.activoFijo as d_activoFijo, 
                    (d.activoSaldoInicial + d.activoCompras + d.activoRetiros) as valorHistorico, 
                    (d.inicial + d.total - d.retiros) as acumuladoHistorico
                FROM activos b
                LEFT JOIN bienesTipos t ON b.idTipo = t.id
                LEFT JOIN monedas m ON b.idMoneda = m.abreviatura
                LEFT JOIN bienesEstados e ON b.idEstado = e.id
                LEFT JOIN prexixion.dbo.tcambio tc ON tc.fecha = b.fechaInicio
                LEFT JOIN activosDepreciaciones d ON b.idCliente = d.idCliente AND b.id = d.idBien AND d.anio = :anio
                WHERE b.idCliente = :idCliente AND d.anio = :anio
                ORDER BY b.id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("anio", anio);

        return jdbcTemplate.query(sql, params, rs -> {
            Map<String, ActivoDTO> map = new LinkedHashMap<>();
            while (rs.next()) {
                String id = rs.getString("id");
                ActivoDTO dto = map.computeIfAbsent(id, k -> {
                    try {
                        Integer bloqueado = rs.getObject("bloqueado") != null ? rs.getInt("bloqueado") : null;
                        String bloqueadoDesc = (bloqueado != null && bloqueado == 1) ? "SÍ" : "";
                        Double costInit = rs.getObject("costoInicial") != null ? rs.getDouble("costoInicial") : 0.0;
                        String mon = rs.getString("idMoneda");
                        Double tCambio = rs.getObject("tipoCambio") != null ? rs.getDouble("tipoCambio") : null;
                        Double pUni = rs.getObject("precioUnitario") != null ? rs.getDouble("precioUnitario") : null;
                        Integer cant = rs.getObject("cantidad") != null ? rs.getInt("cantidad") : null;

                        if ((costInit == null || costInit <= 0.0) && pUni != null && pUni > 0 && cant != null && cant > 0) {
                            if ("USD".equals(mon)) {
                                double tcRaw = (tCambio != null && tCambio > 0) ? tCambio : 1.0;
                                costInit = pUni * cant * tcRaw;
                            } else {
                                costInit = pUni * cant;
                            }
                        }

                        return ActivoDTO.builder()
                                .id(id)
                                .idCliente(rs.getString("idCliente"))
                                .idProveedor(rs.getString("idProveedor"))
                                .proveedor(rs.getString("proveedor"))
                                .descripcion(rs.getString("descripcion"))
                                .marca(rs.getString("marca"))
                                .modelo(rs.getString("modelo"))
                                .seriePlaca(rs.getString("seriePlaca"))
                                .idTipo(rs.getString("idTipo"))
                                .tipo(rs.getString("tipo"))
                                .porcentajeDepreciacionContable(rs.getObject("pctContable") != null ? rs.getDouble("pctContable") : null)
                                .porcentajeDepreciacionTributaria(rs.getObject("pctTributario") != null ? rs.getDouble("pctTributario") : null)
                                .cuenta(rs.getString("cuenta"))
                                .documento(rs.getString("documento"))
                                .fechaInicio(rs.getString("fechaInicio"))
                                .fechaFinContable(rs.getString("fechaFinContable"))
                                .fechaFinTributaria(rs.getString("fechaFinTributaria"))
                                .idMoneda(mon)
                                .moneda(rs.getString("moneda"))
                                .tipoCambio(tCambio)
                                .precioUnitario(pUni)
                                .cantidad(cant)
                                .costoInicial(costInit)
                                .idEstado(rs.getObject("idEstado") != null ? rs.getInt("idEstado") : null)
                                .estado(rs.getString("estado"))
                                .fechaBaja(rs.getString("fechaBaja"))
                                .fechaCompra(rs.getString("fechaCompra"))
                                .bloqueado(bloqueado)
                                .bloqueadoDesc(bloqueadoDesc)
                                .depreciacionesContables(new ArrayList<>())
                                .depreciacionesTributarias(new ArrayList<>())
                                .build();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                if (rs.getObject("d_idCliente") != null) {
                    Integer idTipo = rs.getObject("d_idTipo") != null ? rs.getInt("d_idTipo") : null;
                    ActivoDepreciacionDTO ad = ActivoDepreciacionDTO.builder()
                            .idCliente(rs.getString("d_idCliente"))
                            .idBien(rs.getString("d_idBien"))
                            .anio(rs.getString("d_anio"))
                            .idTipo(idTipo)
                            .activoSaldoInicial(rs.getObject("d_activoSaldoInicial") != null ? rs.getDouble("d_activoSaldoInicial") : null)
                            .activoCompras(rs.getObject("d_activoCompras") != null ? rs.getDouble("d_activoCompras") : null)
                            .activoRetiros(rs.getObject("d_activoRetiros") != null ? rs.getDouble("d_activoRetiros") : null)
                            .activoSaldoFinal(rs.getObject("d_activoSaldoFinal") != null ? rs.getDouble("d_activoSaldoFinal") : null)
                            .inicial(rs.getObject("d_inicial") != null ? rs.getDouble("d_inicial") : null)
                            .ene(rs.getObject("d_ene") != null ? rs.getDouble("d_ene") : null)
                            .feb(rs.getObject("d_feb") != null ? rs.getDouble("d_feb") : null)
                            .mar(rs.getObject("d_mar") != null ? rs.getDouble("d_mar") : null)
                            .abr(rs.getObject("d_abr") != null ? rs.getDouble("d_abr") : null)
                            .may(rs.getObject("d_may") != null ? rs.getDouble("d_may") : null)
                            .jun(rs.getObject("d_jun") != null ? rs.getDouble("d_jun") : null)
                            .jul(rs.getObject("d_jul") != null ? rs.getDouble("d_jul") : null)
                            .ago(rs.getObject("d_ago") != null ? rs.getDouble("d_ago") : null)
                            .sep(rs.getObject("d_sep") != null ? rs.getDouble("d_sep") : null)
                            .oct(rs.getObject("d_oct") != null ? rs.getDouble("d_oct") : null)
                            .nov(rs.getObject("d_nov") != null ? rs.getDouble("d_nov") : null)
                            .dec(rs.getObject("d_dec") != null ? rs.getDouble("d_dec") : null)
                            .total(rs.getObject("d_total") != null ? rs.getDouble("d_total") : null)
                            .retiros(rs.getObject("d_retiros") != null ? rs.getDouble("d_retiros") : null)
                            .saldoFinal(rs.getObject("d_saldoFinal") != null ? rs.getDouble("d_saldoFinal") : null)
                            .activoFijo(rs.getObject("d_activoFijo") != null ? rs.getDouble("d_activoFijo") : null)
                            .valorHistorico(rs.getObject("valorHistorico") != null ? rs.getDouble("valorHistorico") : null)
                            .acumuladoHistorico(rs.getObject("acumuladoHistorico") != null ? rs.getDouble("acumuladoHistorico") : null)
                            .build();

                    if (idTipo != null) {
                        if (idTipo == 1) {
                            dto.getDepreciacionesContables().add(ad);
                        } else if (idTipo == 2) {
                            dto.getDepreciacionesTributarias().add(ad);
                        }
                    }
                }
            }
            return new ArrayList<>(map.values());
        });
    }

    public void updateActivo(String idCliente, String id, String idProveedor, String proveedor,
                             String descripcion, String marca, String modelo, String seriePlaca, String idTipo,
                             String cuenta, String documento, String fechaInicio, String fechaFinContable,
                             String fechaFinTributaria, String idMoneda, Double precioUnitario, Integer cantidad,
                             Double costoInicial, Integer idEstado, String fechaBaja, String fechaCompra) {
        String sql = """
                UPDATE activos SET 
                    idProveedor = :idProveedor, proveedor = :proveedor, descripcion = :descripcion, marca = :marca, 
                    modelo = :modelo, seriePlaca = :seriePlaca, idTipo = :idTipo, cuenta = :cuenta, documento = :documento, 
                    fechaInicio = :fechaInicio, fechaFinContable = :fechaFinContable, fechaFinTributaria = :fechaFinTributaria, 
                    idMoneda = :idMoneda, precioUnitario = :precioUnitario, cantidad = :cantidad, costoInicial = :costoInicial, 
                    idEstado = :idEstado, fechaBaja = :fechaBaja, fechaCompra = :fechaCompra
                WHERE idCliente = :idCliente AND id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idProveedor", idProveedor)
                .addValue("proveedor", proveedor)
                .addValue("descripcion", descripcion)
                .addValue("marca", marca)
                .addValue("modelo", modelo)
                .addValue("seriePlaca", seriePlaca)
                .addValue("idTipo", idTipo)
                .addValue("cuenta", cuenta)
                .addValue("documento", documento)
                .addValue("fechaInicio", fechaInicio)
                .addValue("fechaFinContable", fechaFinContable)
                .addValue("fechaFinTributaria", fechaFinTributaria)
                .addValue("idMoneda", idMoneda)
                .addValue("precioUnitario", precioUnitario)
                .addValue("cantidad", cantidad)
                .addValue("costoInicial", costoInicial)
                .addValue("idEstado", idEstado)
                .addValue("fechaBaja", fechaBaja)
                .addValue("fechaCompra", fechaCompra)
                .addValue("idCliente", idCliente)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void insertActivo(String idCliente, String id, String idProveedor, String proveedor,
                             String descripcion, String marca, String modelo, String seriePlaca, String idTipo,
                             String cuenta, String documento, String fechaInicio, String fechaFinContable,
                             String fechaFinTributaria, String idMoneda, Double precioUnitario, Integer cantidad,
                             Double costoInicial, Integer idEstado, String fechaBaja, String fechaCompra) {
        String sql = """
                INSERT INTO activos (
                    idCliente, id, idProveedor, proveedor, descripcion, marca, modelo, seriePlaca, 
                    idTipo, cuenta, documento, fechaInicio, fechaFinContable, 
                    fechaFinTributaria, idMoneda, precioUnitario, cantidad, costoInicial, 
                    idEstado, fechaBaja, fechaCompra
                ) VALUES (
                    :idCliente, :id, :idProveedor, :proveedor, :descripcion, :marca, :modelo, :seriePlaca, 
                    :idTipo, :cuenta, :documento, :fechaInicio, :fechaFinContable, 
                    :fechaFinTributaria, :idMoneda, :precioUnitario, :cantidad, :costoInicial, 
                    :idEstado, :fechaBaja, :fechaCompra
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("id", id)
                .addValue("idProveedor", idProveedor)
                .addValue("proveedor", proveedor)
                .addValue("descripcion", descripcion)
                .addValue("marca", marca)
                .addValue("modelo", modelo)
                .addValue("seriePlaca", seriePlaca)
                .addValue("idTipo", idTipo)
                .addValue("cuenta", cuenta)
                .addValue("documento", documento)
                .addValue("fechaInicio", fechaInicio)
                .addValue("fechaFinContable", fechaFinContable)
                .addValue("fechaFinTributaria", fechaFinTributaria)
                .addValue("idMoneda", idMoneda)
                .addValue("precioUnitario", precioUnitario)
                .addValue("cantidad", cantidad)
                .addValue("costoInicial", costoInicial)
                .addValue("idEstado", idEstado)
                .addValue("fechaBaja", fechaBaja)
                .addValue("fechaCompra", fechaCompra);
        jdbcTemplate.update(sql, params);
    }

    public String getMinFechaCompra(String idCliente) {
        String sql = "SELECT MIN(fechaCompra) FROM activos WHERE idCliente = :idCliente";
        return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("idCliente", idCliente), String.class);
    }

    public String getMaxFechaContableTributaria(String idCliente) {
        String sql = "SELECT MAX(v) FROM (SELECT MAX(fechaFinContable) as v FROM activos WHERE idCliente = :idCliente1 UNION SELECT MAX(fechaFinTributaria) FROM activos WHERE idCliente = :idCliente2) t";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente1", idCliente)
                .addValue("idCliente2", idCliente);
        return jdbcTemplate.queryForObject(sql, params, String.class);
    }
}
