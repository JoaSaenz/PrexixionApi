package com.joa.prexixionapi.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ActivoDepreciacionRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Object[]> findByClienteAndBienAndTipoRaw(String idCliente, String idBien, int idTipo) {
        String sql = """
                SELECT 
                    ad.idCliente, ad.idBien, ad.anio, ad.idTipo, ad.activoSaldoInicial, ad.activoCompras, 
                    ad.activoRetiros, ad.activoSaldoFinal, ad.inicial, ad.ene, ad.feb, ad.mar, ad.abr, ad.may, 
                    ad.jun, ad.jul, ad.ago, ad.sep, ad.oct, ad.nov, ad.dec, ad.total, ad.retiros,
                    ad.saldoFinal, ad.activoFijo
                FROM activosDepreciaciones ad
                WHERE ad.idCliente = ? AND ad.idBien = ? AND ad.idTipo = ?
                ORDER BY ad.anio
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, idCliente, idBien, idTipo);
    }

    public List<Object[]> findAllByClienteRaw(String idCliente) {
        String sql = """
                SELECT 
                    ad.idCliente, ad.idBien, ad.anio, ad.idTipo, ad.activoSaldoInicial, ad.activoCompras, 
                    ad.activoRetiros, ad.activoSaldoFinal, ad.inicial, ad.ene, ad.feb, ad.mar, ad.abr, ad.may, 
                    ad.jun, ad.jul, ad.ago, ad.sep, ad.oct, ad.nov, ad.dec, ad.total, ad.retiros,
                    ad.saldoFinal, ad.activoFijo
                FROM activosDepreciaciones ad
                WHERE ad.idCliente = ?
                ORDER BY ad.idBien, ad.idTipo, ad.anio
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, idCliente);
    }

    public int deleteByClienteAndBien(String idCliente, String idBien) {
        String sql = "DELETE FROM activosDepreciaciones WHERE idCliente = ? AND idBien = ?";
        return jdbcTemplate.update(sql, idCliente, idBien);
    }
}
