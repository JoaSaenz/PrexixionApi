package com.joa.prexixionapi.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ActivoRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
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
            """;

    public List<Object[]> findByIdClienteRaw(String idCliente) {
        String sql = SELECT_BASE + " WHERE b.idCliente = ? ORDER BY b.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, idCliente);
    }

    public Object[] findByClienteAndIdRaw(String idCliente, String id) {
        String sql = SELECT_BASE + " WHERE b.idCliente = ? AND b.id = ?";
        List<Object[]> rows = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, idCliente, id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public long countByClienteAndId(String idCliente, String id) {
        String sql = "SELECT COUNT(*) FROM activos WHERE idCliente = ? AND id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, idCliente, id);
        return count != null ? count : 0;
    }

    public int deleteByClienteAndId(String idCliente, String id) {
        String sql = "DELETE FROM activos WHERE idCliente = ? AND id = ?";
        return jdbcTemplate.update(sql, idCliente, id);
    }

    public int updateBloqueo(String idCliente, List<String> ids, int status) {
        if (ids == null || ids.isEmpty()) return 0;
        String sql = "UPDATE activos SET bloqueado = ? WHERE idCliente = ? AND id IN (" + 
                     String.join(",", ids.stream().map(i -> "?").toArray(String[]::new)) + ")";
        Object[] params = new Object[ids.size() + 2];
        params[0] = status;
        params[1] = idCliente;
        for (int i = 0; i < ids.size(); i++) params[i + 2] = ids.get(i);
        return jdbcTemplate.update(sql, params);
    }

    public List<Object[]> findActivosFijosRVExcel(String idCliente) {
        String sql = SELECT_BASE + " WHERE b.idCliente = ? AND b.idTipo = 'RV' ORDER BY b.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, idCliente);
    }

    public List<Object[]> findActivosFijosExcelWithDepreciations(String idCliente, String anio) {
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
                    -- JOINED DATA (Starting at index 29)
                    d.idCliente as d_idCliente, d.idBien, d.anio, d.idTipo as d_idTipo, d.activoSaldoInicial, 
                    d.activoCompras, d.activoRetiros, d.activoSaldoFinal, d.inicial, d.ene, d.feb, d.mar, 
                    d.abr, d.may, d.jun, d.jul, d.ago, d.sep, d.oct, d.nov, d.[dec], d.total, d.retiros, 
                    d.saldoFinal, d.activoFijo, 
                    (d.activoSaldoInicial + d.activoCompras + d.activoRetiros) as valorHistorico, 
                    (d.inicial + d.total - d.retiros) as acumuladoHistorico
                FROM activos b
                LEFT JOIN bienesTipos t ON b.idTipo = t.id
                LEFT JOIN monedas m ON b.idMoneda = m.abreviatura
                LEFT JOIN bienesEstados e ON b.idEstado = e.id
                LEFT JOIN prexixion.dbo.tcambio tc ON tc.fecha = b.fechaInicio
                LEFT JOIN activosDepreciaciones d ON b.idCliente = d.idCliente AND b.id = d.idBien AND d.anio = ?
                WHERE b.idCliente = ? AND d.anio = ?
                ORDER BY b.id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, anio, idCliente, anio);
    }

    public void updateActivo(String idCliente, String id, String idProveedor, String proveedor,
                             String descripcion, String marca, String modelo, String seriePlaca, String idTipo,
                             String cuenta, String documento, String fechaInicio, String fechaFinContable,
                             String fechaFinTributaria, String idMoneda, Double precioUnitario, Integer cantidad,
                             Double costoInicial, Integer idEstado, String fechaBaja, String fechaCompra) {
        String sql = """
                UPDATE activos SET 
                    idProveedor=?, proveedor=?, descripcion=?, marca=?, modelo=?, seriePlaca=?, 
                    idTipo=?, cuenta=?, documento=?, fechaInicio=?, fechaFinContable=?, 
                    fechaFinTributaria=?, idMoneda=?, precioUnitario=?, cantidad=?, costoInicial=?, 
                    idEstado=?, fechaBaja=?, fechaCompra=?
                WHERE idCliente=? AND id=?
                """;
        jdbcTemplate.update(sql, idProveedor, proveedor, descripcion, marca, modelo, seriePlaca,
                idTipo, cuenta, documento, fechaInicio, fechaFinContable,
                fechaFinTributaria, idMoneda, precioUnitario, cantidad,
                costoInicial, idEstado, fechaBaja, fechaCompra, idCliente, id);
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
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, idCliente, id, idProveedor, proveedor, descripcion, marca, modelo, seriePlaca,
                idTipo, cuenta, documento, fechaInicio, fechaFinContable,
                fechaFinTributaria, idMoneda, precioUnitario, cantidad,
                costoInicial, idEstado, fechaBaja, fechaCompra);
    }
}
