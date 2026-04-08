package com.joa.prexixionapi.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepreciacionRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Object[]> findResumenByAnioRaw(String anio) {
        String sql = """
                SELECT 
                    c.ruc as ruc, 
                    c.razonSocial as razonSocial, 
                    c.y as y, 
                    c.idEstado as idEstado, 
                    cE.descripcion as estado, 
                    dp.dpAnioInicio as anioInicio, 
                    dp.dpAnioFin as anioFin, 
                    SUM(ISNULL(ad.activoSaldoInicial, 0)) AS activoSaldoInicial, 
                    SUM(ISNULL(ad.activoCompras, 0)) AS activoCompras, 
                    SUM(ISNULL(ad.activoRetiros, 0)) AS activoRetiros, 
                    SUM(ISNULL(ad.activoSaldoFinal, 0)) AS activoSaldoFinal,
                    SUM(ISNULL(ad.inicial, 0)) AS inicial,
                    SUM(ISNULL(ad.total, 0)) AS total,
                    SUM(ISNULL(ad.retiros, 0)) AS retiros,
                    SUM(ISNULL(ad.saldoFinal, 0)) AS saldoFinal,
                    SUM(ISNULL(ad.activoFijo, 0)) AS activoFijo
                FROM cliente c
                INNER JOIN clientsEstados cE ON c.idEstado = cE.id 
                INNER JOIN clienteDepreciaciones dp ON c.ruc = dp.idCliente 
                LEFT JOIN activosDepreciaciones ad ON c.ruc = ad.idCliente AND ad.idTipo = 1
                WHERE (dp.dpAnioInicio <= ?) AND (dp.dpAnioFin >= ? OR dp.dpAnioFin = '' OR dp.dpAnioFin IS NULL)
                GROUP BY c.ruc, c.razonSocial, c.y, c.idEstado, cE.descripcion, dp.dpAnioInicio, dp.dpAnioFin
                ORDER BY c.y 
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        }, anio, anio);
    }
}
