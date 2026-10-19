package com.joa.prexixionapi.modules.depreciacion.repositories;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.modules.depreciacion.repositories.DepreciacionRepository;
import com.joa.prexixionapi.modules.depreciacion.dto.DepreciacionResumenDTO;

@Repository
@RequiredArgsConstructor
public class DepreciacionRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<DepreciacionResumenDTO> findResumenByAnio(String anio) {
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> DepreciacionResumenDTO.builder()
                .ruc(rs.getString("ruc"))
                .razonSocial(rs.getString("razonSocial"))
                .y(rs.getString("y"))
                .idEstado(rs.getInt("idEstado"))
                .estado(rs.getString("estado"))
                .anioInicio(rs.getString("anioInicio"))
                .anioFin(rs.getString("anioFin"))
                .activoSaldoInicial(rs.getDouble("activoSaldoInicial"))
                .activoCompras(rs.getDouble("activoCompras"))
                .activoRetiros(rs.getDouble("activoRetiros"))
                .activoSaldoFinal(rs.getDouble("activoSaldoFinal"))
                .inicial(rs.getDouble("inicial"))
                .total(rs.getDouble("total"))
                .retiros(rs.getDouble("retiros"))
                .saldoFinal(rs.getDouble("saldoFinal"))
                .activoFijo(rs.getDouble("activoFijo"))
                .build(), anio, anio);
    }
}
