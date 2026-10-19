package com.joa.prexixionapi.modules.beneficiarioFinal.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.List;
import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalRegistroRepository;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalRegistroDTO;

@Repository
@RequiredArgsConstructor
public class BeneficiarioFinalRegistroRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<BeneficiarioFinalRegistroDTO> rowMapper = (rs, rowNum) -> {
        return BeneficiarioFinalRegistroDTO.builder()
                .id(rs.getInt("id"))
                .nroRegistro(rs.getInt("nro_registro"))
                .tipo(new GclassDTO(rs.getInt("idTipo"), ""))
                .fecha(rs.getString("fecha"))
                .nroOrden(rs.getString("nroOrden"))
                .nroRectificacion(rs.getString("nroRectificacion"))
                .build();
    };

    public List<BeneficiarioFinalRegistroDTO> findByIdClienteAndAnioAndMesOrderByNroRegistroAsc(String idCliente, String anio, String mes) {
        String sql = "SELECT * FROM bf3800_registros WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes ORDER BY nro_registro ASC";
        MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("idCliente", idCliente)
        .addValue("anio", anio)
        .addValue("mes", mes);
        return jdbcTemplate.query(sql, params, rowMapper);
    }

    public void save(BeneficiarioFinalRegistroDTO dto, String idCliente, String anio, String mes) {
        if (dto.getId() != null && dto.getId() > 0) {
            String sql = "UPDATE bf3800_registros SET nro_registro = :nroRegistro, idTipo = :idTipo, fecha = :fecha, nroOrden = :nroOrden, nroRectificacion = :nroRectificacion WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", dto.getId())
            .addValue("nroRegistro", dto.getNroRegistro())
            .addValue("idTipo", dto.getTipo() != null ? dto.getTipo().getId() : 0)
            .addValue("fecha", dto.getFecha())
            .addValue("nroOrden", dto.getNroOrden())
            .addValue("nroRectificacion", dto.getNroRectificacion());
        jdbcTemplate.update(sql, params);
        } else {
            String sql = "INSERT INTO bf3800_registros (idCliente, anio, mes, nro_registro, idTipo, fecha, nroOrden, nroRectificacion) VALUES (:idCliente, :anio, :mes, :nroRegistro, :idTipo, :fecha, :nroOrden, :nroRectificacion)";
            jdbcTemplate.update(sql, Map.of(
                "idCliente", idCliente,
                "anio", anio,
                "mes", mes,
                "nroRegistro", dto.getNroRegistro(),
                "idTipo", dto.getTipo() != null ? dto.getTipo().getId() : 0,
                "fecha", dto.getFecha() != null ? dto.getFecha() : "",
                "nroOrden", dto.getNroOrden() != null ? dto.getNroOrden() : "",
                "nroRectificacion", dto.getNroRectificacion() != null ? dto.getNroRectificacion() : ""
            ));
        }
    }
}
