package com.joa.prexixionapi.modules.reunion.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.modules.reunion.dto.ReunionDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionListDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionRequest;
import com.joa.prexixionapi.common.utils.QueryUtils;
import com.joa.prexixionapi.modules.reunion.dto.ReunionExcelDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionParticipanteExternoDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionParticipanteInternoDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionTemaDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionAreaDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionAcuerdoDTO;
import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDTO;

@Repository
public class ReunionRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    // ==========================================
    // 2. READ METHODS (LIST & AGGREGATE)
    // ==========================================
    public List<ReunionListDTO> list(ReunionRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT r.id, r.idCliente, r.razonSocial, r.tipo, r.fecha, r.horaI, r.horaF, r.idEstado, re.descripcion as estado, r.otros
                FROM reuniones r
                INNER JOIN reunionesEstados re ON r.idEstado = re.id
                WHERE 1=1
                """);
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        QueryUtils.appendIntFilter(sql, params, "r.idEstado", req.getEstadoFiltro(), "estados");
        
        sql.append(" ORDER BY r.fecha DESC, r.horaI DESC ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> ReunionListDTO.builder()
                .id(rs.getInt("id"))
                .clienteRuc(rs.getString("idCliente"))
                .clienteRazonSocial(rs.getString("razonSocial"))
                .tipo(rs.getString("tipo"))
                .fecha(rs.getString("fecha"))
                .horaI(rs.getString("horaI"))
                .horaF(rs.getString("horaF"))
                .estadoId(rs.getInt("idEstado"))
                .estadoDescripcion(rs.getString("estado"))
                .build());
    }

    public List<ReunionExcelDTO> listExcel(ReunionRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT r.id, r.idCliente, r.razonSocial, r.tipo, r.fecha, r.horaI, r.horaF, r.idEstado, re.descripcion as estado, r.otros,
                       (SELECT STRING_AGG(tema, ', ') FROM reunionesTemas rt WHERE rt.idReunion = r.id) as temas,
                       (SELECT STRING_AGG(a.descripcion, ', ') FROM reunionesAreas ra JOIN areas a ON ra.idArea = a.id WHERE ra.idReunion = r.id) as areas,
                       (SELECT STRING_AGG(nombres, ', ') FROM reunionesParticipantesExternos rpe WHERE rpe.idReunion = r.id) as participantesExternos,
                       (SELECT STRING_AGG(p.apellidos + ' ' + p.nombres, ', ') FROM reunionesParticipantesInternos rpi JOIN personal p ON rpi.dni = p.dni WHERE rpi.idReunion = r.id) as participantesInternos,
                       (SELECT STRING_AGG(acuerdo, ', ') FROM reunionesAcuerdos rac WHERE rac.idReunion = r.id) as acuerdos
                FROM reuniones r
                INNER JOIN reunionesEstados re ON r.idEstado = re.id
                WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        QueryUtils.appendIntFilter(sql, params, "r.idEstado", req.getEstadoFiltro(), "estados");

        sql.append(" ORDER BY r.fecha DESC, r.horaI DESC ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> ReunionExcelDTO.builder()
                .id(rs.getInt("id"))
                .clienteRuc(rs.getString("idCliente"))
                .clienteRazonSocial(rs.getString("razonSocial"))
                .tipo(rs.getString("tipo"))
                .fecha(rs.getString("fecha"))
                .horaI(rs.getString("horaI"))
                .horaF(rs.getString("horaF"))
                .estadoId(rs.getInt("idEstado"))
                .estadoDescripcion(rs.getString("estado"))
                .otros(rs.getString("otros"))
                .temas(rs.getString("temas") != null ? rs.getString("temas") : "")
                .areas(rs.getString("areas") != null ? rs.getString("areas") : "")
                .participantesExternos(
                        rs.getString("participantesExternos") != null ? rs.getString("participantesExternos") : "")
                .participantesInternos(
                        rs.getString("participantesInternos") != null ? rs.getString("participantesInternos") : "")
                .acuerdos(rs.getString("acuerdos") != null ? rs.getString("acuerdos") : "")
                .build());
    }

    public List<IndicadorDTO> getSummaryEstados(ReunionRequest req) {
        StringBuilder sql = new StringBuilder("""
                SELECT re.descripcion AS texto, COUNT(r.id) AS cantidad
                FROM reuniones r
                INNER JOIN reunionesEstados re ON r.idEstado = re.id
                WHERE 1=1
                """);
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        QueryUtils.appendIntFilter(sql, params, "r.idEstado", req.getEstadoFiltro(), "estados");
        
        sql.append(" GROUP BY re.descripcion");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> 
            new IndicadorDTO(rs.getString("texto"), rs.getInt("cantidad"))
        );
    }

    // ==========================================
    // 3. READ METHODS (SINGLE & BATCH FETCHERS)
    // ==========================================
    public ReunionDTO getOne(int idReunion) {
        String sql = """
                SELECT r.id, r.idCliente, r.razonSocial, r.tipo, r.fecha, r.horaI, r.horaF, r.idEstado, re.descripcion as estado, r.otros
                FROM reuniones r
                INNER JOIN reunionesEstados re ON r.idEstado = re.id
                WHERE r.id = :id
                """;

        List<ReunionDTO> result = jdbcTemplate.query(sql, new MapSqlParameterSource("id", idReunion), (rs, rowNum) -> {
            ClienteDTO clie = new ClienteDTO();
            clie.setRuc(rs.getString("idCliente"));
            clie.setRazonSocial(rs.getString("razonSocial"));

            return ReunionDTO.builder()
                    .id(rs.getInt("id"))
                    .cliente(clie)
                    .tipo(rs.getString("tipo"))
                    .fecha(rs.getString("fecha"))
                    .horaI(rs.getString("horaI"))
                    .horaF(rs.getString("horaF"))
                    .estado(new GclassDTO(rs.getInt("idEstado"), rs.getString("estado")))
                    .otros(rs.getString("otros"))
                    .build();
        });

        if (result.isEmpty())
            return null;

        ReunionDTO r = result.get(0);
        List<Integer> ids = List.of(idReunion);

        r.setTemas(fetchTemas(ids));
        r.setParticipantesExternos(fetchParticipantesExternos(ids));
        r.setParticipantesInternos(fetchParticipantesInternos(ids));

        List<GclassDTO> areas = new ArrayList<>();
        for (ReunionAreaDTO a : fetchAreas(ids)) {
            areas.add(new GclassDTO(a.getId(), a.getDescripcion()));
        }
        r.setAreas(areas);

        List<GclassDTO> acuerdos = new ArrayList<>();
        for (ReunionAcuerdoDTO a : fetchAcuerdos(ids)) {
            acuerdos.add(new GclassDTO(a.getId(), a.getAcuerdo()));
        }
        r.setAcuerdos(acuerdos);

        return r;
    }

    public List<ReunionTemaDTO> fetchTemas(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT idReunion, idTema, tema, acuerdoTema FROM reunionesTemas WHERE idReunion IN (:ids)";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids), (rs, rowNum) -> ReunionTemaDTO.builder()
                .idReunion(rs.getInt("idReunion"))
                .id(rs.getInt("idTema"))
                .tema(rs.getString("tema"))
                .acuerdoTema(rs.getString("acuerdoTema"))
                .build());
    }

    public Map<Integer, List<String>> fetchTemasStrings(List<Integer> ids) {
        if (ids.isEmpty())
            return new HashMap<>();
        String sql = "SELECT idReunion, tema FROM reunionesTemas WHERE idReunion IN (:ids)";

        Map<Integer, List<String>> map = new HashMap<>();
        jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids), rs -> {
            map.computeIfAbsent(rs.getInt("idReunion"), k -> new ArrayList<>()).add(rs.getString("tema"));
        });
        return map;
    }

    public List<ReunionAreaDTO> fetchAreas(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT ra.idReunion, a.id, a.descripcion " +
                "FROM reunionesAreas ra " +
                "INNER JOIN areas a ON ra.idArea = a.id " +
                "WHERE ra.idReunion IN (:ids)";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids), (rs, rowNum) -> ReunionAreaDTO.builder()
                .idReunion(rs.getInt("idReunion"))
                .id(rs.getInt("id"))
                .descripcion(rs.getString("descripcion"))
                .build());
    }

    public Map<Integer, List<String>> fetchAreasStrings(List<Integer> ids) {
        if (ids.isEmpty())
            return new HashMap<>();
        String sql = "SELECT ra.idReunion, a.descripcion FROM reunionesAreas ra INNER JOIN areas a ON ra.idArea = a.id WHERE ra.idReunion IN (:ids)";

        Map<Integer, List<String>> map = new HashMap<>();
        jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids), rs -> {
            map.computeIfAbsent(rs.getInt("idReunion"), k -> new ArrayList<>()).add(rs.getString("descripcion"));
        });
        return map;
    }

    public List<ReunionParticipanteExternoDTO> fetchParticipantesExternos(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT idReunion, nombres, cargo FROM reunionesParticipantesExternos WHERE idReunion IN (:ids)";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids),
                (rs, rowNum) -> ReunionParticipanteExternoDTO.builder()
                        .idReunion(rs.getInt("idReunion"))
                        .nombres(rs.getString("nombres"))
                        .cargo(rs.getString("cargo"))
                        .build());
    }

    public List<ReunionParticipanteInternoDTO> fetchParticipantesInternos(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT rpi.idReunion, p.dni, p.apellidos, p.nombres, pu.descripcion as puesto " +
                "FROM reunionesParticipantesInternos rpi " +
                "INNER JOIN personal p ON rpi.dni = p.dni " +
                "LEFT JOIN personalPuestos pu ON p.idPuesto = pu.id " +
                "WHERE rpi.idReunion IN (:ids)";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids),
                (rs, rowNum) -> ReunionParticipanteInternoDTO.builder()
                        .idReunion(rs.getInt("idReunion"))
                        .dni(rs.getString("dni"))
                        .apellidos(rs.getString("apellidos"))
                        .nombres(rs.getString("nombres"))
                        .puesto(rs.getString("puesto"))
                        .build());
    }

    public List<ReunionAcuerdoDTO> fetchAcuerdos(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT idReunion, idAcuerdo, acuerdo FROM reunionesAcuerdos WHERE idReunion IN (:ids)";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids),
                (rs, rowNum) -> ReunionAcuerdoDTO.builder()
                        .idReunion(rs.getInt("idReunion"))
                        .id(rs.getInt("idAcuerdo"))
                        .acuerdo(rs.getString("acuerdo"))
                        .build());
    }

    // ==========================================
    // 4. WRITE METHODS (CUD)
    // ==========================================
    @Transactional
    public int insert(ReunionDTO r) {
        String query = "INSERT INTO reuniones(idCliente, razonSocial, tipo, fecha, horaI, horaF, idEstado, otros) " +
                "OUTPUT INSERTED.id " +
                "VALUES (:idCliente, :razonSocial, :tipo, :fecha, :horaI, :horaF, :idEstado, :otros)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idCliente", r.getCliente() != null ? r.getCliente().getRuc() : null);
        params.addValue("razonSocial", r.getCliente() != null ? r.getCliente().getRazonSocial() : null);
        params.addValue("tipo", r.getTipo());
        params.addValue("fecha", r.getFecha());
        params.addValue("horaI", r.getHoraI());
        params.addValue("horaF", r.getHoraF());
        params.addValue("idEstado", r.getEstado() != null ? r.getEstado().getId() : null);
        params.addValue("otros", r.getOtros());

        Integer generatedId = jdbcTemplate.queryForObject(query, params, Integer.class);

        r.setId(generatedId);
        insertSubEntities(r);
        return generatedId;
    }

    @Transactional
    public void update(ReunionDTO r) {
        String query = "UPDATE reuniones SET idCliente = :idCliente, razonSocial = :razonSocial, tipo = :tipo, " +
                "fecha = :fecha, horaI = :horaI, horaF = :horaF, idEstado = :idEstado, otros = :otros " +
                "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idCliente", r.getCliente() != null ? r.getCliente().getRuc() : null);
        params.addValue("razonSocial", r.getCliente() != null ? r.getCliente().getRazonSocial() : null);
        params.addValue("tipo", r.getTipo());
        params.addValue("fecha", r.getFecha());
        params.addValue("horaI", r.getHoraI());
        params.addValue("horaF", r.getHoraF());
        params.addValue("idEstado", r.getEstado() != null ? r.getEstado().getId() : null);
        params.addValue("otros", r.getOtros());
        params.addValue("id", r.getId());

        jdbcTemplate.update(query, params);

        // Delete sub-entities
        int idReunion = r.getId();
        MapSqlParameterSource deleteParams = new MapSqlParameterSource("id", idReunion);
        jdbcTemplate.update("DELETE FROM reunionesTemas WHERE idReunion = :id", deleteParams);
        jdbcTemplate.update("DELETE FROM reunionesParticipantesExternos WHERE idReunion = :id", deleteParams);
        jdbcTemplate.update("DELETE FROM reunionesParticipantesInternos WHERE idReunion = :id", deleteParams);
        jdbcTemplate.update("DELETE FROM reunionesAreas WHERE idReunion = :id", deleteParams);
        jdbcTemplate.update("DELETE FROM reunionesAcuerdos WHERE idReunion = :id", deleteParams);

        insertSubEntities(r);
    }

    @Transactional
    public void delete(int idReunion) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", idReunion);
        jdbcTemplate.update("DELETE FROM reunionesTemas WHERE idReunion = :id", params);
        jdbcTemplate.update("DELETE FROM reunionesParticipantesExternos WHERE idReunion = :id", params);
        jdbcTemplate.update("DELETE FROM reunionesParticipantesInternos WHERE idReunion = :id", params);
        jdbcTemplate.update("DELETE FROM reunionesAreas WHERE idReunion = :id", params);
        jdbcTemplate.update("DELETE FROM reunionesAcuerdos WHERE idReunion = :id", params);
        jdbcTemplate.update("DELETE FROM reuniones WHERE id = :id", params);
    }

    // ==========================================
    // 5. PRIVATE HELPERS
    // ==========================================

    private void insertSubEntities(ReunionDTO r) {
        int generatedId = r.getId();

        if (r.getTemas() != null) {
            int i = 1;
            String sql = "INSERT INTO reunionesTemas (idReunion, idTema, tema, acuerdoTema) VALUES (:idR, :idT, :t, :a)";
            for (ReunionTemaDTO tema : r.getTemas()) {
                MapSqlParameterSource p = new MapSqlParameterSource()
                        .addValue("idR", generatedId).addValue("idT", i++)
                        .addValue("t", tema.getTema()).addValue("a", tema.getAcuerdoTema());
                jdbcTemplate.update(sql, p);
            }
        }

        if (r.getParticipantesExternos() != null) {
            int i = 1;
            String sql = "INSERT INTO reunionesParticipantesExternos (idReunion, id, nombres, cargo) VALUES (:idR, :idP, :n, :c)";
            for (ReunionParticipanteExternoDTO p : r.getParticipantesExternos()) {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("idR", generatedId).addValue("idP", i++)
                        .addValue("n", p.getNombres()).addValue("c", p.getCargo());
                jdbcTemplate.update(sql, params);
            }
        }

        if (r.getParticipantesInternos() != null) {
            String sql = "INSERT INTO reunionesParticipantesInternos (idReunion, dni) VALUES (:idR, :dni)";
            for (ReunionParticipanteInternoDTO p : r.getParticipantesInternos()) {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("idR", generatedId).addValue("dni", p.getDni());
                jdbcTemplate.update(sql, params);
            }
        }

        if (r.getAreas() != null) {
            String sql = "INSERT INTO reunionesAreas (idReunion, idArea) VALUES (:idR, :idA)";
            for (GclassDTO a : r.getAreas()) {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("idR", generatedId).addValue("idA", a.getId());
                jdbcTemplate.update(sql, params);
            }
        }

        if (r.getAcuerdos() != null) {
            int i = 1;
            String sql = "INSERT INTO reunionesAcuerdos (idReunion, idAcuerdo, acuerdo) VALUES (:idR, :idA, :a)";
            for (GclassDTO a : r.getAcuerdos()) {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("idR", generatedId).addValue("idA", i++)
                        .addValue("a", a.getDescripcion());
                jdbcTemplate.update(sql, params);
            }
        }
    }
}
