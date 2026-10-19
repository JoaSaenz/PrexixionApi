package com.joa.prexixionapi.auth.repositories;

import com.joa.prexixionapi.auth.dto.UserDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<UserDTO> findById(String dni) {
        String sql = "SELECT dni, clave, habilitado FROM users WHERE dni = :dni";
        MapSqlParameterSource params = new MapSqlParameterSource("dni", dni);

        List<UserDTO> users = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            return UserDTO.builder()
                    .dni(rs.getString("dni"))
                    .clave(rs.getString("clave"))
                    .habilitado(rs.getInt("habilitado"))
                    .build();
        });

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }
}
