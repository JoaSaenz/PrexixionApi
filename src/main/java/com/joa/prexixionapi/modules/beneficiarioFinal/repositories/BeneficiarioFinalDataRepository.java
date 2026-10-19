package com.joa.prexixionapi.modules.beneficiarioFinal.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalDataDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.repositories.BeneficiarioFinalDataRepository;

@Repository
@RequiredArgsConstructor
public class BeneficiarioFinalDataRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void upsert(BeneficiarioFinalDataDTO dto) {
        String countSql = "SELECT COUNT(*) FROM bf3800_data WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", dto.getIdCliente())
                .addValue("anio", dto.getAnio())
                .addValue("mes", dto.getMes())
                .addValue("observacion", dto.getObservacion() != null ? dto.getObservacion() : "")
                .addValue("mail", dto.getMail() != null ? dto.getMail() : 0);
        Integer count = jdbcTemplate.queryForObject(countSql, params, Integer.class);
        if (count != null && count > 0) {
            String updateSql = "UPDATE bf3800_data SET observacion = :observacion, mail = :mail WHERE idCliente = :idCliente AND anio = :anio AND mes = :mes";
            jdbcTemplate.update(updateSql, params);
        } else {
            String insertSql = "INSERT INTO bf3800_data (idCliente, anio, mes, observacion, mail) VALUES (:idCliente, :anio, :mes, :observacion, :mail)";
            jdbcTemplate.update(insertSql, params);
        }
    }
}
