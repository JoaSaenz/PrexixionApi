package com.joa.prexixionapi.modules.depreciacion.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoDepreciacionRepository;

@Repository
@RequiredArgsConstructor
public class ActivoDepreciacionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Object[]> findByClienteAndBienAndTipoRaw(String idCliente, String idBien, int idTipo) {
        String sql = """
                SELECT 
                    ad.idCliente, ad.idBien, ad.anio, ad.idTipo, ad.activoSaldoInicial, ad.activoCompras, 
                    ad.activoRetiros, ad.activoSaldoFinal, ad.inicial, ad.ene, ad.feb, ad.mar, ad.abr, ad.may, 
                    ad.jun, ad.jul, ad.ago, ad.sep, ad.oct, ad.nov, ad.dec, ad.total, ad.retiros,
                    ad.saldoFinal, ad.activoFijo
                FROM activosDepreciaciones ad
                WHERE ad.idCliente = :idCliente AND ad.idBien = :idBien AND ad.idTipo = :idTipo
                ORDER BY ad.anio
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("idBien", idBien)
                .addValue("idTipo", idTipo);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        });
    }

    public List<Object[]> findAllByClienteRaw(String idCliente) {
        String sql = """
                SELECT 
                    ad.idCliente, ad.idBien, ad.anio, ad.idTipo, ad.activoSaldoInicial, ad.activoCompras, 
                    ad.activoRetiros, ad.activoSaldoFinal, ad.inicial, ad.ene, ad.feb, ad.mar, ad.abr, ad.may, 
                    ad.jun, ad.jul, ad.ago, ad.sep, ad.oct, ad.nov, ad.dec, ad.total, ad.retiros,
                    ad.saldoFinal, ad.activoFijo
                FROM activosDepreciaciones ad
                WHERE ad.idCliente = :idCliente
                ORDER BY ad.idBien, ad.idTipo, ad.anio
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("idCliente", idCliente), (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        });
    }

    public int deleteByClienteAndBien(String idCliente, String idBien) {
        String sql = "DELETE FROM activosDepreciaciones WHERE idCliente = :idCliente AND idBien = :idBien";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("idBien", idBien);
        return jdbcTemplate.update(sql, params);
    }

    public void insertDepreciacionesBatch(String idCliente, String idBien, int idTipo, List<com.joa.prexixionapi.modules.depreciacion.dto.ActivoDepreciacionDTO> list) {
        if (list == null || list.isEmpty())
            return;

        String sql = """
                    INSERT INTO activosDepreciaciones
                    (idCliente, idBien, anio, idTipo, activoSaldoInicial, activoCompras, activoRetiros, activoSaldoFinal,
                     inicial, ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, [dec], total, retiros, saldoFinal, activoFijo)
                    VALUES (:idCliente, :idBien, :anio, :idTipo, :activoSaldoInicial, :activoCompras, :activoRetiros, :activoSaldoFinal,
                     :inicial, :ene, :feb, :mar, :abr, :may, :jun, :jul, :ago, :sep, :oct, :nov, :dec, :total, :retiros, :saldoFinal, :activoFijo)
                """;

        java.util.List<org.springframework.jdbc.core.namedparam.MapSqlParameterSource> batchArgsList = new java.util.ArrayList<>();
        for (com.joa.prexixionapi.modules.depreciacion.dto.ActivoDepreciacionDTO ad : list) {
            org.springframework.jdbc.core.namedparam.MapSqlParameterSource params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("idBien", idBien)
                .addValue("anio", ad.getAnio())
                .addValue("idTipo", idTipo)
                .addValue("activoSaldoInicial", ad.getActivoSaldoInicial())
                .addValue("activoCompras", ad.getActivoCompras())
                .addValue("activoRetiros", ad.getActivoRetiros())
                .addValue("activoSaldoFinal", ad.getActivoSaldoFinal())
                .addValue("inicial", ad.getInicial())
                .addValue("ene", ad.getEne())
                .addValue("feb", ad.getFeb())
                .addValue("mar", ad.getMar())
                .addValue("abr", ad.getAbr())
                .addValue("may", ad.getMay())
                .addValue("jun", ad.getJun())
                .addValue("jul", ad.getJul())
                .addValue("ago", ad.getAgo())
                .addValue("sep", ad.getSep())
                .addValue("oct", ad.getOct())
                .addValue("nov", ad.getNov())
                .addValue("dec", ad.getDec())
                .addValue("total", ad.getTotal())
                .addValue("retiros", ad.getRetiros())
                .addValue("saldoFinal", ad.getSaldoFinal())
                .addValue("activoFijo", ad.getActivoFijo());
            batchArgsList.add(params);
        }
        
        jdbcTemplate.batchUpdate(sql, batchArgsList.toArray(new org.springframework.jdbc.core.namedparam.MapSqlParameterSource[0]));
    }
}
