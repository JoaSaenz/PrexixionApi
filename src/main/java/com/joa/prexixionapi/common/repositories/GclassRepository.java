package com.joa.prexixionapi.common.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.common.dto.GclassDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GclassRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<GclassDTO> list(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName + " ORDER BY descripcion";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
    }

    public List<GclassDTO> listString(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName + " ORDER BY descripcion";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GclassDTO g = new GclassDTO();
            g.setIdString(rs.getString("id"));
            g.setDescripcion(rs.getString("descripcion"));
            return g;
        });
    }

    public List<GclassDTO> listOrderById(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
    }

    public List<GclassDTO> listAbbrPlusEmptyOrderById(String tableName) {
        String sql = "SELECT id, abreviatura, descripcion FROM " + tableName;
        List<GclassDTO> list = new ArrayList<>();
        GclassDTO empty = new GclassDTO();
        empty.setId(0);
        empty.setAbreviatura("-");
        list.add(empty);

        list.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("abreviatura"), rs.getString("descripcion"))));
        return list;
    }

    public List<GclassDTO> listPlusEmpty(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName + " ORDER BY descripcion";
        List<GclassDTO> list = new ArrayList<>();
        GclassDTO empty = new GclassDTO();
        empty.setId(0);
        empty.setDescripcion("-");
        list.add(empty);

        list.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion"))));
        return list;
    }

    public List<GclassDTO> listPlusEmptyOrderById(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName;
        List<GclassDTO> list = new ArrayList<>();
        GclassDTO empty = new GclassDTO();
        empty.setId(0);
        empty.setDescripcion("-");
        list.add(empty);

        list.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion"))));
        return list;
    }

    public List<GclassDTO> listLoginOptions(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName
                + " ORDER BY CASE WHEN id = 0 THEN 2 "
                + "               WHEN id = 1 THEN 1 "
                + "               WHEN id = 2 THEN 3 "
                + "          END ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
    }

    public List<GclassDTO> listLoginOptionsSpecials(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName
                + " ORDER BY CASE WHEN id = 0 THEN 3 "
                + "               WHEN id = 1 THEN 2 "
                + "               WHEN id = 2 THEN 4 "
                + "               WHEN id = 3 THEN 1 "
                + "          END ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
    }

    public List<GclassDTO> listAbbr(String tableName) {
        String sql = "SELECT id, abreviatura, descripcion FROM " + tableName;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("abreviatura"), rs.getString("descripcion")));
    }

    public List<GclassDTO> listCodigoDescripcion(String tableName) {
        String sql = "SELECT id, codigo as abreviatura, descripcion FROM " + tableName;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("abreviatura"), rs.getString("descripcion")));
    }

    @Transactional
    public int insertUpdate(String tableName, GclassDTO obj) {
        String sqlCheck = "SELECT count(*) FROM " + tableName + " WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", obj.getId())
                .addValue("abbr", obj.getAbreviatura())
                .addValue("desc", obj.getDescripcion());

        Integer count = jdbcTemplate.queryForObject(sqlCheck, params, Integer.class);
        if (count != null && count > 0) {
            String sqlUpdate = "UPDATE " + tableName + " SET abreviatura = :abbr, descripcion = :desc WHERE id = :id";
            jdbcTemplate.update(sqlUpdate, params);
            return 2; // Updated
        } else {
            String sqlInsert = "INSERT INTO " + tableName + " (abreviatura, descripcion) VALUES (:abbr, :desc)";
            jdbcTemplate.update(sqlInsert, params);
            return 1; // Inserted
        }
    }

    @Transactional
    public int insertUpdate2(String tableName, GclassDTO obj) {
        String sqlCheck = "SELECT count(*) FROM " + tableName + " WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", obj.getId())
                .addValue("desc", obj.getDescripcion());

        Integer count = jdbcTemplate.queryForObject(sqlCheck, params, Integer.class);
        if (count != null && count > 0) {
            String sqlUpdate = "UPDATE " + tableName + " SET descripcion = :desc WHERE id = :id";
            jdbcTemplate.update(sqlUpdate, params);
            return 2; // Updated
        } else {
            String sqlInsert = "INSERT INTO " + tableName + " (descripcion) VALUES (:desc)";
            jdbcTemplate.update(sqlInsert, params);
            return 1; // Inserted
        }
    }

    @Transactional
    public int delete(String tableName, int id) {
        String sqlDelete = "DELETE FROM " + tableName + " WHERE id = :id";
        jdbcTemplate.update(sqlDelete, new MapSqlParameterSource("id", id));
        return 3; // Deleted
    }

    public boolean existe(String tableName, int id) {
        String sql = "SELECT count(*) FROM " + tableName + " WHERE id = :id";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), Integer.class);
        return count != null && count > 0;
    }

    public GclassDTO getOne(String tableName, int id) {
        String sql = "SELECT id, descripcion FROM " + tableName + " WHERE id = :id";
        List<GclassDTO> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id),
                (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
        return list.isEmpty() ? new GclassDTO() : list.get(0);
    }

    public GclassDTO getOne2(String tableName, int id) {
        String sql = "SELECT id, abreviatura, descripcion FROM " + tableName + " WHERE id = :id";
        List<GclassDTO> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id),
                (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("abreviatura"), rs.getString("descripcion")));
        return list.isEmpty() ? new GclassDTO() : list.get(0);
    }

    public List<GclassDTO> listCredenciales(int id) {
        String sql = "EXEC credencialesEquipo :id";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), (rs, rowNum) -> {
            GclassDTO gclass = new GclassDTO();
            gclass.setId(rs.getInt(1));
            gclass.setAbreviatura(rs.getString(2)); // username
            gclass.setDescripcion(rs.getString(3)); // clave
            return gclass;
        });
    }

    public GclassDTO vlanIp(int ip) {
        String sql = "SELECT v.descripcion AS VLAN FROM EquiposIp i "
                + "INNER JOIN EquiposVlan v ON i.idVlan = v.id "
                + "WHERE i.id = :ip";
        List<GclassDTO> list = jdbcTemplate.query(sql, new MapSqlParameterSource("ip", ip), (rs, rowNum) -> {
            GclassDTO g = new GclassDTO();
            g.setDescripcion(rs.getString("VLAN"));
            return g;
        });
        return list.isEmpty() ? new GclassDTO() : list.get(0);
    }

    public List<GclassDTO> listFromTwoTables(String tabla1, String tabla2, int id) {
        String sql = "SELECT id, descripcion FROM " + tabla1 + " WHERE id IN (SELECT b FROM " + tabla2
                + " WHERE a = :id) ORDER BY descripcion";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", id),
                (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
    }

    public List<GclassDTO> listFromThreeTables(String tabla1, String tabla2, int id1, int id2) {
        String sql = "SELECT id, descripcion FROM " + tabla1 + " WHERE id IN (SELECT z FROM " + tabla2
                + " WHERE x = :id1 AND y = :id2) ORDER BY descripcion";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id1", id1)
                .addValue("id2", id2);
        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> new GclassDTO(rs.getInt("id"), rs.getString("descripcion")));
    }
}
