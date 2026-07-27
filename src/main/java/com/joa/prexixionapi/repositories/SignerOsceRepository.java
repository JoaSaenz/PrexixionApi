package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.SignerOsceDTO;
import com.joa.prexixionapi.dto.SignerOsceListDTO;
import com.joa.prexixionapi.dto.SignerOsceRequest;
import com.joa.prexixionapi.dto.SignerOsceTipoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SignerOsceRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<SignerOsceListDTO> listForDataTable(SignerOsceRequest request) {
        String sql = """
                SELECT c.idGrupoEconomico, ge.descripcion as descGrupoEconomico,
                       c.idEstado as idEstadoCliente, ce.descripcion AS descEstadoCliente,
                       o.idCliente, c.y, c.razonSocial,
                       o.mail, o.usuario, o.clave, o.rnp, o.feAlta,
                       o.idEstado as idEstadoOsce, oe.descripcion as descEstadoOsce,
                       o.idTramite, ot.descripcion as descTramite,
                       (SELECT TOP 1 tipo FROM signerOsceTipos ti WHERE ti.tipo = 'BYS' AND ti.idCliente = o.idCliente ORDER BY ti.id desc) tipoBYS,
                       (SELECT TOP 1 tipo FROM signerOsceTipos ti WHERE ti.tipo = 'EYC' AND ti.idCliente = o.idCliente ORDER BY ti.id desc) tipoEyC,
                       (SELECT TOP 1 CASE WHEN categoria = 0 then '' WHEN categoria = 1 then 'A' WHEN categoria = 2 then 'B' WHEN categoria = 3 then 'C' WHEN categoria = 4 then 'D' END as categoria
                        FROM signerOsceTipos ti WHERE ti.tipo = 'EYC' AND ti.idCliente = o.idCliente ORDER BY ti.id desc) categoriaEyc,
                       o.observacion
                FROM signerOsce o
                LEFT JOIN cliente c ON o.idCliente = c.ruc
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id
                LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN signerOsceEstados oe ON o.idEstado = oe.id
                LEFT JOIN signerOsceTramites ot ON o.idTramite = ot.id
                WHERE 1=1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (request.getGruposEconomicosString() != null && !request.getGruposEconomicosString().isEmpty()) {
            boolean hasZero = java.util.Arrays.asList(request.getGruposEconomicosString().split(",")).contains("0");
            if (hasZero) {
                sql += " AND (c.idGrupoEconomico IN (" + request.getGruposEconomicosString()
                        + ") OR c.idGrupoEconomico IS NULL) ";
            } else {
                sql += " AND c.idGrupoEconomico IN (" + request.getGruposEconomicosString() + ") ";
            }
        }

        if (request.getEstadosString() != null && !request.getEstadosString().isEmpty()) {
            sql += " AND c.idEstado IN (" + request.getEstadosString() + ") ";
        }

        if (request.getGruposString() != null && !request.getGruposString().isEmpty()) {
            sql += " AND c.y IN (" + request.getGruposString() + ") ";
        }

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SignerOsceListDTO dto = new SignerOsceListDTO();
            dto.setIdCliente(rs.getString("idCliente"));
            dto.setY(rs.getString("y"));
            dto.setRazonSocial(rs.getString("razonSocial"));

            dto.setIdEstadoCliente(rs.getInt("idEstadoCliente"));
            dto.setDescEstadoCliente(rs.getString("descEstadoCliente"));
            dto.setIdGrupoEconomico(rs.getInt("idGrupoEconomico"));
            dto.setDescGrupoEconomico(rs.getString("descGrupoEconomico"));

            dto.setMail(rs.getInt("mail"));
            dto.setUsuario(rs.getString("usuario"));
            dto.setClave(rs.getString("clave"));
            dto.setRnp(rs.getString("rnp"));
            dto.setFeAlta(rs.getString("feAlta"));

            dto.setIdEstadoOsce(rs.getInt("idEstadoOsce"));
            dto.setDescEstadoOsce(rs.getString("descEstadoOsce"));
            dto.setIdTramite(rs.getInt("idTramite"));
            dto.setDescTramite(rs.getString("descTramite"));

            dto.setTipoByS(rs.getString("tipoBYS"));
            dto.setTipoEyC(rs.getString("tipoEyC"));
            dto.setCategoriaEyC(rs.getString("categoriaEyc"));
            dto.setObservacion(rs.getString("observacion"));

            return dto;
        });
    }

    public List<SignerOsceListDTO> listForExcel(SignerOsceRequest request) {
        String sql = """
                SELECT c.idGrupoEconomico, ge.descripcion as descGrupoEconomico,
                       c.idEstado as idEstadoCliente, ce.descripcion AS descEstadoCliente,
                       o.idCliente, c.y, c.razonSocial,
                       o.mail, o.usuario, o.clave, o.rnp, o.feAlta,
                       o.idEstado as idEstadoOsce, oe.descripcion as descEstadoOsce,
                       o.idTramite, ot.descripcion as descTramite,
                       (SELECT TOP 1 tipo FROM signerOsceTipos ti WHERE ti.tipo = 'BYS' AND ti.idCliente = o.idCliente ORDER BY ti.id desc) tipoBYS,
                       (SELECT TOP 1 tipo FROM signerOsceTipos ti WHERE ti.tipo = 'EYC' AND ti.idCliente = o.idCliente ORDER BY ti.id desc) tipoEyC,
                       (SELECT TOP 1 CASE WHEN categoria = 0 then '' WHEN categoria = 1 then 'A' WHEN categoria = 2 then 'B' WHEN categoria = 3 then 'C' WHEN categoria = 4 then 'D' END as categoria
                        FROM signerOsceTipos ti WHERE ti.tipo = 'EYC' AND ti.idCliente = o.idCliente ORDER BY ti.id desc) categoriaEyc,
                       o.observacion
                FROM signerOsce o
                LEFT JOIN cliente c ON o.idCliente = c.ruc
                LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id
                LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN signerOsceEstados oe ON o.idEstado = oe.id
                LEFT JOIN signerOsceTramites ot ON o.idTramite = ot.id
                WHERE 1=1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (request.getGruposEconomicosString() != null && !request.getGruposEconomicosString().isEmpty()) {
            boolean hasZero = java.util.Arrays.asList(request.getGruposEconomicosString().split(",")).contains("0");
            if (hasZero) {
                sql += " AND (c.idGrupoEconomico IN (" + request.getGruposEconomicosString()
                        + ") OR c.idGrupoEconomico IS NULL) ";
            } else {
                sql += " AND c.idGrupoEconomico IN (" + request.getGruposEconomicosString() + ") ";
            }
        }

        if (request.getEstadosString() != null && !request.getEstadosString().isEmpty()) {
            sql += " AND c.idEstado IN (" + request.getEstadosString() + ") ";
        }

        if (request.getGruposString() != null && !request.getGruposString().isEmpty()) {
            sql += " AND c.y IN (" + request.getGruposString() + ") ";
        }

        sql += " ORDER BY c.y ASC ";

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SignerOsceListDTO dto = new SignerOsceListDTO();
            dto.setIdCliente(rs.getString("idCliente"));
            dto.setY(rs.getString("y"));
            dto.setRazonSocial(rs.getString("razonSocial"));

            dto.setIdEstadoCliente(rs.getInt("idEstadoCliente"));
            dto.setDescEstadoCliente(rs.getString("descEstadoCliente"));
            dto.setIdGrupoEconomico(rs.getInt("idGrupoEconomico"));
            dto.setDescGrupoEconomico(rs.getString("descGrupoEconomico"));

            dto.setMail(rs.getInt("mail"));
            dto.setUsuario(rs.getString("usuario"));
            dto.setClave(rs.getString("clave"));
            dto.setRnp(rs.getString("rnp"));
            dto.setFeAlta(rs.getString("feAlta"));

            dto.setIdEstadoOsce(rs.getInt("idEstadoOsce"));
            dto.setDescEstadoOsce(rs.getString("descEstadoOsce"));
            dto.setIdTramite(rs.getInt("idTramite"));
            dto.setDescTramite(rs.getString("descTramite"));

            dto.setTipoByS(rs.getString("tipoBYS"));
            dto.setTipoEyC(rs.getString("tipoEyC"));
            dto.setCategoriaEyC(rs.getString("categoriaEyc"));
            dto.setObservacion(rs.getString("observacion"));

            return dto;
        });
    }

    public SignerOsceDTO getOne(String idCliente) {
        String sql = """
                SELECT o.idCliente, c.razonSocial,
                       o.mail, o.usuario, o.clave, o.rnp, o.feAlta,
                       o.idEstado as idEstadoOsce, oe.descripcion as descEstadoOsce,
                       o.idTramite, ot.descripcion as descTramite,
                       o.observacion
                FROM signerOsce o
                LEFT JOIN cliente c ON o.idCliente = c.ruc
                LEFT JOIN signerOsceEstados oe ON o.idEstado = oe.id
                LEFT JOIN signerOsceTramites ot ON o.idTramite = ot.id
                WHERE o.idCliente = :idCliente
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente);

        List<SignerOsceDTO> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            SignerOsceDTO dto = new SignerOsceDTO();
            dto.setIdCliente(rs.getString("idCliente"));
            dto.setRazonSocial(rs.getString("razonSocial"));

            dto.setMail(rs.getInt("mail"));
            dto.setUsuario(rs.getString("usuario"));
            dto.setClave(rs.getString("clave"));
            dto.setRnp(rs.getString("rnp"));
            dto.setFeAlta(rs.getString("feAlta"));

            dto.setIdEstadoOsce(rs.getInt("idEstadoOsce"));
            dto.setDescEstadoOsce(rs.getString("descEstadoOsce"));
            dto.setIdTramite(rs.getInt("idTramite"));
            dto.setDescTramite(rs.getString("descTramite"));
            dto.setObservacion(rs.getString("observacion"));

            return dto;
        });

        if (list.isEmpty()) {
            return null;
        }

        SignerOsceDTO dto = list.get(0);

        // Fetch Tipos
        String sqlTipos = "SELECT id, idCliente, tipo, categoria, feAlta FROM signerOsceTipos WHERE idCliente = :idCliente ORDER BY id";
        List<SignerOsceTipoDTO> tipos = jdbcTemplate.query(sqlTipos, params, (rs, rowNum) -> {
            SignerOsceTipoDTO t = new SignerOsceTipoDTO();
            t.setId(rs.getInt("id"));
            t.setIdCliente(rs.getString("idCliente"));
            t.setTipo(rs.getString("tipo"));
            t.setCategoria(rs.getInt("categoria"));
            t.setFeAlta(rs.getString("feAlta"));
            return t;
        });

        dto.setListSignerOsceTipo(tipos);

        return dto;
    }

    public boolean exist(String idCliente) {
        String sql = "SELECT COUNT(*) FROM signerOsce WHERE idCliente = :idCliente";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("idCliente", idCliente);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public void insert(SignerOsceDTO dto) {
        String sql = """
                INSERT INTO signerOsce (idCliente, mail, usuario, clave, rnp, feAlta, idEstado, idTramite, observacion)
                VALUES (:idCliente, :mail, :usuario, :clave, :rnp, :feAlta, :idEstadoOsce, :idTramite, :observacion)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", dto.getIdCliente())
                .addValue("mail", dto.getMail())
                .addValue("usuario", dto.getUsuario())
                .addValue("clave", dto.getClave())
                .addValue("rnp", dto.getRnp())
                .addValue("feAlta", dto.getFeAlta())
                .addValue("idEstadoOsce", dto.getIdEstadoOsce())
                .addValue("idTramite", dto.getIdTramite())
                .addValue("observacion", dto.getObservacion());

        jdbcTemplate.update(sql, params);
    }

    public void update(SignerOsceDTO dto) {
        String sql = """
                UPDATE signerOsce
                SET mail = :mail,
                    usuario = :usuario,
                    clave = :clave,
                    rnp = :rnp,
                    feAlta = :feAlta,
                    idEstado = :idEstadoOsce,
                    idTramite = :idTramite,
                    observacion = :observacion
                WHERE idCliente = :idCliente
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", dto.getIdCliente())
                .addValue("mail", dto.getMail())
                .addValue("usuario", dto.getUsuario())
                .addValue("clave", dto.getClave())
                .addValue("rnp", dto.getRnp())
                .addValue("feAlta", dto.getFeAlta())
                .addValue("idEstadoOsce", dto.getIdEstadoOsce())
                .addValue("idTramite", dto.getIdTramite())
                .addValue("observacion", dto.getObservacion());

        jdbcTemplate.update(sql, params);
    }

    public void deleteTipos(String idCliente) {
        String sql = "DELETE FROM signerOsceTipos WHERE idCliente = :idCliente";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("idCliente", idCliente);
        jdbcTemplate.update(sql, params);
    }

    public void deleteTiposExcept(String idCliente, List<Integer> idsToKeep) {
        if (idsToKeep.isEmpty()) {
            deleteTipos(idCliente);
            return;
        }

        String sql = "DELETE FROM signerOsceTipos WHERE idCliente = :idCliente AND id NOT IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("ids", idsToKeep);
        jdbcTemplate.update(sql, params);
    }

    public void insertTipo(SignerOsceTipoDTO tipo) {
        String sql = """
                INSERT INTO signerOsceTipos (idCliente, tipo, categoria, feAlta)
                VALUES (:idCliente, :tipo, :categoria, :feAlta)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", tipo.getIdCliente())
                .addValue("tipo", tipo.getTipo())
                .addValue("categoria", tipo.getCategoria())
                .addValue("feAlta", tipo.getFeAlta());

        jdbcTemplate.update(sql, params);
    }

    public void updateTipo(SignerOsceTipoDTO tipo) {
        String sql = """
                UPDATE signerOsceTipos
                SET tipo = :tipo,
                    categoria = :categoria,
                    feAlta = :feAlta
                WHERE id = :id AND idCliente = :idCliente
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", tipo.getId())
                .addValue("idCliente", tipo.getIdCliente())
                .addValue("tipo", tipo.getTipo())
                .addValue("categoria", tipo.getCategoria())
                .addValue("feAlta", tipo.getFeAlta());

        jdbcTemplate.update(sql, params);
    }

    public void delete(String idCliente) {
        String sql = "DELETE FROM signerOsce WHERE idCliente = :idCliente";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("idCliente", idCliente);
        jdbcTemplate.update(sql, params);
    }

}
