package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.TipoCambioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TipoCambioRepository {

    private final JdbcTemplate jdbcTemplate;

    public TipoCambioDTO findByFecha(String fecha) {
        String sql = "SELECT PERIODO, T_VENTA, T_COMPRA FROM prexixion.dbo.tcambio WHERE FECHA = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> TipoCambioDTO.builder()
                    .periodo(rs.getString("PERIODO"))
                    .tVenta(rs.getDouble("T_VENTA"))
                    .tCompra(rs.getDouble("T_COMPRA"))
                    .build(), fecha);
        } catch (Exception e) {
            return null;
        }
    }
}
