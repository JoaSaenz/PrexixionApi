package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CasoSunatRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<CasoSunatListDTO> listForDataTable(CasoSunatRequest request) {
        String sql = """
                SELECT c.id, c.idEmpresa, COALESCE(cl.razonSocial, 'EMPRESA REGISTRADA') AS razonSocial,
                       c.idTipoCaso, tc.descripcion AS descTipoCaso,
                       c.idModalidad, m.descripcion AS descModalidad,
                       c.idTributo, tr.descripcion AS descTributo,
                       c.idMotivo, mo.descripcion AS descMotivo,
                       c.idTipoPeriodo, tp.descripcion AS descTipoPeriodo,
                       c.anioPeriodoInicio, c.mesPeriodoInicio,
                       c.anioPeriodoFin, c.mesPeriodoFin,
                       c.periodoTexto, c.coordinacionTax, c.coordinacionFir, c.avance,
                       ld.idTipoDocumento AS ultIdTipoDocumento,
                       ltd.descripcion AS descTipoDocumento,
                       ld.fechaPresentacion AS fechaPresentacion,
                       ld.hora AS hora,
                       ld.importeObservado AS importeObservado,
                       ld.idEstado AS idEstado,
                       led.descripcion AS descEstado
                FROM casoSunat c
                LEFT JOIN cliente cl ON c.idEmpresa = cl.ruc
                LEFT JOIN casoSunatTipoCaso tc ON c.idTipoCaso = tc.id
                LEFT JOIN casoSunatModalidad m ON c.idModalidad = m.id
                LEFT JOIN casoSunatTributo tr ON c.idTributo = tr.id
                LEFT JOIN casoSunatMotivo mo ON c.idMotivo = mo.id
                LEFT JOIN casoSunatTipoPeriodo tp ON c.idTipoPeriodo = tp.id
                LEFT JOIN casoSunatDocumento ld ON ld.id = (
                    SELECT MAX(d2.id) FROM casoSunatDocumento d2 WHERE d2.idCaso = c.id
                )
                LEFT JOIN casoSunatTipoDocumento ltd ON ld.idTipoDocumento = ltd.id
                LEFT JOIN casoSunatEstadoDocumento led ON ld.idEstado = led.id
                WHERE 1=1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (request.getIdEmpresa() != null && !request.getIdEmpresa().trim().isEmpty()) {
            sql += " AND c.idEmpresa = :idEmpresa ";
            params.addValue("idEmpresa", request.getIdEmpresa().trim());
        }
        if (request.getTiposCasoString() != null && !request.getTiposCasoString().isEmpty()) {
            sql += " AND c.idTipoCaso IN (" + request.getTiposCasoString() + ") ";
        }
        if (request.getModalidadesString() != null && !request.getModalidadesString().isEmpty()) {
            sql += " AND c.idModalidad IN (" + request.getModalidadesString() + ") ";
        }
        if (request.getTributosString() != null && !request.getTributosString().isEmpty()) {
            sql += " AND c.idTributo IN (" + request.getTributosString() + ") ";
        }
        if (request.getTiposPeriodoString() != null && !request.getTiposPeriodoString().isEmpty()) {
            sql += " AND c.idTipoPeriodo IN (" + request.getTiposPeriodoString() + ") ";
        }
        if (request.getPeriodoTexto() != null && !request.getPeriodoTexto().trim().isEmpty()) {
            sql += " AND LOWER(c.periodoTexto) LIKE :periodoTexto ";
            params.addValue("periodoTexto", "%" + request.getPeriodoTexto().trim().toLowerCase() + "%");
        }

        sql += " ORDER BY c.id DESC ";

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            CasoSunatListDTO dto = new CasoSunatListDTO();
            dto.setId(rs.getInt("id"));
            dto.setIdEmpresa(rs.getString("idEmpresa"));
            dto.setRazonSocial(rs.getString("razonSocial"));
            dto.setIdTipoCaso(rs.getInt("idTipoCaso"));
            dto.setDescTipoCaso(rs.getString("descTipoCaso"));
            dto.setIdModalidad(rs.getInt("idModalidad"));
            dto.setDescModalidad(rs.getString("descModalidad"));
            dto.setIdTributo(rs.getInt("idTributo"));
            dto.setDescTributo(rs.getString("descTributo"));
            dto.setIdMotivo(rs.getObject("idMotivo") != null ? rs.getInt("idMotivo") : null);
            dto.setDescMotivo(rs.getString("descMotivo"));
            dto.setIdTipoPeriodo(rs.getInt("idTipoPeriodo"));
            dto.setDescTipoPeriodo(rs.getString("descTipoPeriodo"));
            dto.setAnioPeriodoInicio(rs.getString("anioPeriodoInicio"));
            dto.setMesPeriodoInicio(rs.getString("mesPeriodoInicio"));
            dto.setAnioPeriodoFin(rs.getString("anioPeriodoFin"));
            dto.setMesPeriodoFin(rs.getString("mesPeriodoFin"));
            dto.setPeriodoTexto(rs.getString("periodoTexto"));
            dto.setCoordinacionTax(rs.getObject("coordinacionTax") != null ? rs.getInt("coordinacionTax") : 0);
            dto.setCoordinacionFir(rs.getObject("coordinacionFir") != null ? rs.getInt("coordinacionFir") : 0);
            dto.setAvance(rs.getBigDecimal("avance"));
            
            // Campos del último documento
            dto.setUltIdTipoDocumento(rs.getObject("ultIdTipoDocumento") != null ? rs.getInt("ultIdTipoDocumento") : null);
            dto.setDescTipoDocumento(rs.getString("descTipoDocumento"));
            dto.setFechaPresentacion(rs.getString("fechaPresentacion"));
            dto.setHora(rs.getString("hora"));
            dto.setImporteObservado(rs.getBigDecimal("importeObservado"));
            dto.setIdEstado(rs.getObject("idEstado") != null ? rs.getInt("idEstado") : null);
            dto.setDescEstado(rs.getString("descEstado"));
            return dto;
        });
    }

    public boolean exist(Integer id) {
        if (id == null || id <= 0) return false;
        String sql = "SELECT COUNT(*) FROM casoSunat WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public CasoSunatDTO getOne(Integer id) {
        String sqlCaso = """
                SELECT c.id, c.idEmpresa, COALESCE(cl.razonSocial, 'EMPRESA REGISTRADA') AS razonSocial,
                       c.idTipoCaso, tc.descripcion AS descTipoCaso,
                       c.idModalidad, m.descripcion AS descModalidad,
                       c.idTributo, tr.descripcion AS descTributo,
                       c.idMotivo, mo.descripcion AS descMotivo,
                       c.idTipoPeriodo, tp.descripcion AS descTipoPeriodo,
                       c.anioPeriodoInicio, c.mesPeriodoInicio,
                       c.anioPeriodoFin, c.mesPeriodoFin,
                       c.periodoTexto, c.coordinacionTax, c.coordinacionFir, c.avance
                FROM casoSunat c
                LEFT JOIN cliente cl ON c.idEmpresa = cl.ruc
                LEFT JOIN casoSunatTipoCaso tc ON c.idTipoCaso = tc.id
                LEFT JOIN casoSunatModalidad m ON c.idModalidad = m.id
                LEFT JOIN casoSunatTributo tr ON c.idTributo = tr.id
                LEFT JOIN casoSunatMotivo mo ON c.idMotivo = mo.id
                LEFT JOIN casoSunatTipoPeriodo tp ON c.idTipoPeriodo = tp.id
                WHERE c.id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        List<CasoSunatDTO> list = jdbcTemplate.query(sqlCaso, params, (rs, rowNum) -> {
            CasoSunatDTO dto = new CasoSunatDTO();
            dto.setId(rs.getInt("id"));
            dto.setIdEmpresa(rs.getString("idEmpresa"));
            dto.setRazonSocial(rs.getString("razonSocial"));
            dto.setIdTipoCaso(rs.getInt("idTipoCaso"));
            dto.setDescTipoCaso(rs.getString("descTipoCaso"));
            dto.setIdModalidad(rs.getInt("idModalidad"));
            dto.setDescModalidad(rs.getString("descModalidad"));
            dto.setIdTributo(rs.getInt("idTributo"));
            dto.setDescTributo(rs.getString("descTributo"));
            dto.setIdMotivo(rs.getObject("idMotivo") != null ? rs.getInt("idMotivo") : null);
            dto.setDescMotivo(rs.getString("descMotivo"));
            dto.setIdTipoPeriodo(rs.getInt("idTipoPeriodo"));
            dto.setDescTipoPeriodo(rs.getString("descTipoPeriodo"));
            dto.setAnioPeriodoInicio(rs.getString("anioPeriodoInicio"));
            dto.setMesPeriodoInicio(rs.getString("mesPeriodoInicio"));
            dto.setAnioPeriodoFin(rs.getString("anioPeriodoFin"));
            dto.setMesPeriodoFin(rs.getString("mesPeriodoFin"));
            dto.setPeriodoTexto(rs.getString("periodoTexto"));
            dto.setCoordinacionTax(rs.getObject("coordinacionTax") != null ? rs.getInt("coordinacionTax") : 0);
            dto.setCoordinacionFir(rs.getObject("coordinacionFir") != null ? rs.getInt("coordinacionFir") : 0);
            dto.setAvance(rs.getBigDecimal("avance"));
            return dto;
        });

        if (list.isEmpty()) return null;

        CasoSunatDTO caso = list.get(0);
        caso.setAuditores(getAuditoresByCaso(id));
        
        List<CasoSunatDocumentoDTO> docs = getDocumentosByCaso(id);
        Map<Integer, List<CasoSunatDocumentoEventoDTO>> eventosMap = getEventosByCasoMap(id);
        for (CasoSunatDocumentoDTO doc : docs) {
            doc.setEventos(eventosMap.getOrDefault(doc.getId(), new ArrayList<>()));
        }
        caso.setDocumentos(docs);
        caso.setRelaciones(getRelacionesByCaso(id));

        return caso;
    }

    public List<CasoSunatAuditorDTO> getAuditoresByCaso(Integer idCaso) {
        String sql = "SELECT id, idCaso, nombresApellidos, fechaInicio, fechaFin FROM casoSunatAuditor WHERE idCaso = :idCaso ORDER BY id ASC";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("idCaso", idCaso), (rs, rowNum) -> {
            CasoSunatAuditorDTO dto = new CasoSunatAuditorDTO();
            dto.setId(rs.getInt("id"));
            dto.setIdCaso(rs.getInt("idCaso"));
            dto.setNombresApellidos(rs.getString("nombresApellidos"));
            dto.setFechaInicio(rs.getString("fechaInicio"));
            dto.setFechaFin(rs.getString("fechaFin"));
            return dto;
        });
    }

    public List<CasoSunatDocumentoDTO> getDocumentosByCaso(Integer idCaso) {
        String sql = """
                SELECT d.id, d.idCaso, d.idTipoDocumento, td.descripcion AS descTipoDocumento,
                       d.nroDocumento, d.fechaRecepcion, d.fechaEnvio, d.fechaPresentacion, d.hora, d.fechaResultado,
                       d.idEstado, ed.descripcion AS descEstado, d.importeObservado, d.rectificatoria, d.importeRectificado
                FROM casoSunatDocumento d
                LEFT JOIN casoSunatTipoDocumento td ON d.idTipoDocumento = td.id
                LEFT JOIN casoSunatEstadoDocumento ed ON d.idEstado = ed.id
                WHERE d.idCaso = :idCaso
                ORDER BY d.id ASC
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("idCaso", idCaso), (rs, rowNum) -> {
            CasoSunatDocumentoDTO dto = new CasoSunatDocumentoDTO();
            dto.setId(rs.getInt("id"));
            dto.setIdCaso(rs.getInt("idCaso"));
            dto.setIdTipoDocumento(rs.getInt("idTipoDocumento"));
            dto.setDescTipoDocumento(rs.getString("descTipoDocumento"));
            dto.setNroDocumento(rs.getString("nroDocumento"));
            dto.setFechaRecepcion(rs.getString("fechaRecepcion"));
            dto.setFechaEnvio(rs.getString("fechaEnvio"));
            dto.setFechaPresentacion(rs.getString("fechaPresentacion"));
            dto.setHora(rs.getString("hora"));
            dto.setFechaResultado(rs.getString("fechaResultado"));
            dto.setIdEstado(rs.getInt("idEstado"));
            dto.setDescEstado(rs.getString("descEstado"));
            dto.setImporteObservado(rs.getBigDecimal("importeObservado"));
            dto.setRectificatoria(rs.getObject("rectificatoria") != null ? rs.getInt("rectificatoria") : 0);
            dto.setImporteRectificado(rs.getBigDecimal("importeRectificado"));
            return dto;
        });
    }

    public Map<Integer, List<CasoSunatDocumentoEventoDTO>> getEventosByCasoMap(Integer idCaso) {
        String sql = """
                SELECT e.id, e.idCaso, e.idDocumento, e.idEmisor, em.descripcion AS descEmisor,
                       e.idTipoEvento, ev.descripcion AS descEvento, e.idDocumentoCarta, cd.nroDocumento AS nroDocumentoCarta,
                       e.fecha, e.observacion
                FROM casoSunatDocumentoEvento e
                LEFT JOIN casoSunatEmisor em ON e.idEmisor = em.id
                LEFT JOIN casoSunatEvento ev ON e.idTipoEvento = ev.id
                LEFT JOIN casoSunatDocumento cd ON e.idDocumentoCarta = cd.id
                WHERE e.idCaso = :idCaso
                ORDER BY e.id ASC
                """;

        Map<Integer, List<CasoSunatDocumentoEventoDTO>> map = new HashMap<>();
        jdbcTemplate.query(sql, new MapSqlParameterSource("idCaso", idCaso), (rs) -> {
            Integer idDoc = rs.getInt("idDocumento");
            CasoSunatDocumentoEventoDTO dto = new CasoSunatDocumentoEventoDTO();
            dto.setId(rs.getInt("id"));
            dto.setIdCaso(rs.getInt("idCaso"));
            dto.setIdDocumento(idDoc);
            dto.setIdEmisor(rs.getInt("idEmisor"));
            dto.setDescEmisor(rs.getString("descEmisor"));
            dto.setIdEvento(rs.getInt("idTipoEvento"));
            dto.setDescEvento(rs.getString("descEvento"));
            dto.setIdDocumentoCarta(rs.getObject("idDocumentoCarta") != null ? rs.getInt("idDocumentoCarta") : null);
            dto.setNroDocumentoCarta(rs.getString("nroDocumentoCarta"));
            dto.setFecha(rs.getString("fecha"));
            dto.setObservacion(rs.getString("observacion"));

            map.computeIfAbsent(idDoc, k -> new ArrayList<>()).add(dto);
        });
        return map;
    }

    public List<CasoSunatDocumentoRelacionDTO> getRelacionesByCaso(Integer idCaso) {
        String sql = """
                SELECT r.id, r.idCaso, r.idDocumentoOrigen,
                       CONCAT(tdo.descripcion, ' ', do.nroDocumento) AS descDocumentoOrigen,
                       r.idTipoRelacion, tr.descripcion AS descTipoRelacion,
                       r.idDocumentoDestino,
                       CONCAT(tdd.descripcion, ' ', dd.nroDocumento) AS descDocumentoDestino
                FROM casoSunatDocumentoRelacion r
                LEFT JOIN casoSunatDocumento do ON r.idDocumentoOrigen = do.id
                LEFT JOIN casoSunatTipoDocumento tdo ON do.idTipoDocumento = tdo.id
                LEFT JOIN casoSunatTipoRelacion tr ON r.idTipoRelacion = tr.id
                LEFT JOIN casoSunatDocumento dd ON r.idDocumentoDestino = dd.id
                LEFT JOIN casoSunatTipoDocumento tdd ON dd.idTipoDocumento = tdd.id
                WHERE r.idCaso = :idCaso
                ORDER BY r.id ASC
                """;

        return jdbcTemplate.query(sql, new MapSqlParameterSource("idCaso", idCaso), (rs, rowNum) -> {
            CasoSunatDocumentoRelacionDTO dto = new CasoSunatDocumentoRelacionDTO();
            dto.setId(rs.getInt("id"));
            dto.setIdCaso(rs.getInt("idCaso"));
            dto.setIdDocumentoOrigen(rs.getInt("idDocumentoOrigen"));
            dto.setDescDocumentoOrigen(rs.getString("descDocumentoOrigen"));
            dto.setIdTipoRelacion(rs.getInt("idTipoRelacion"));
            dto.setDescTipoRelacion(rs.getString("descTipoRelacion"));
            dto.setIdDocumentoDestino(rs.getInt("idDocumentoDestino"));
            dto.setDescDocumentoDestino(rs.getString("descDocumentoDestino"));
            return dto;
        });
    }

    public int insertCaso(CasoSunatDTO dto) {
        String sql = """
                INSERT INTO casoSunat (idEmpresa, idTipoCaso, idModalidad, idTributo, idMotivo, idTipoPeriodo,
                                       anioPeriodoInicio, mesPeriodoInicio, anioPeriodoFin, mesPeriodoFin,
                                       periodoTexto, coordinacionTax, coordinacionFir, avance)
                VALUES (:idEmpresa, :idTipoCaso, :idModalidad, :idTributo, :idMotivo, :idTipoPeriodo,
                        :anioPeriodoInicio, :mesPeriodoInicio, :anioPeriodoFin, :mesPeriodoFin,
                        :periodoTexto, :coordinacionTax, :coordinacionFir, :avance)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idEmpresa", dto.getIdEmpresa())
                .addValue("idTipoCaso", dto.getIdTipoCaso())
                .addValue("idModalidad", dto.getIdModalidad())
                .addValue("idTributo", dto.getIdTributo())
                .addValue("idMotivo", dto.getIdMotivo())
                .addValue("idTipoPeriodo", dto.getIdTipoPeriodo())
                .addValue("anioPeriodoInicio", dto.getAnioPeriodoInicio())
                .addValue("mesPeriodoInicio", dto.getMesPeriodoInicio())
                .addValue("anioPeriodoFin", dto.getAnioPeriodoFin())
                .addValue("mesPeriodoFin", dto.getMesPeriodoFin())
                .addValue("periodoTexto", dto.getPeriodoTexto())
                .addValue("coordinacionTax", dto.getCoordinacionTax() != null ? dto.getCoordinacionTax() : 0)
                .addValue("coordinacionFir", dto.getCoordinacionFir() != null ? dto.getCoordinacionFir() : 0)
                .addValue("avance", dto.getAvance() != null ? dto.getAvance() : 0);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public void updateCaso(CasoSunatDTO dto) {
        String sql = """
                UPDATE casoSunat
                SET idEmpresa = :idEmpresa,
                    idTipoCaso = :idTipoCaso,
                    idModalidad = :idModalidad,
                    idTributo = :idTributo,
                    idMotivo = :idMotivo,
                    idTipoPeriodo = :idTipoPeriodo,
                    anioPeriodoInicio = :anioPeriodoInicio,
                    mesPeriodoInicio = :mesPeriodoInicio,
                    anioPeriodoFin = :anioPeriodoFin,
                    mesPeriodoFin = :mesPeriodoFin,
                    periodoTexto = :periodoTexto,
                    coordinacionTax = :coordinacionTax,
                    coordinacionFir = :coordinacionFir,
                    avance = :avance
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", dto.getId())
                .addValue("idEmpresa", dto.getIdEmpresa())
                .addValue("idTipoCaso", dto.getIdTipoCaso())
                .addValue("idModalidad", dto.getIdModalidad())
                .addValue("idTributo", dto.getIdTributo())
                .addValue("idMotivo", dto.getIdMotivo())
                .addValue("idTipoPeriodo", dto.getIdTipoPeriodo())
                .addValue("anioPeriodoInicio", dto.getAnioPeriodoInicio())
                .addValue("mesPeriodoInicio", dto.getMesPeriodoInicio())
                .addValue("anioPeriodoFin", dto.getAnioPeriodoFin())
                .addValue("mesPeriodoFin", dto.getMesPeriodoFin())
                .addValue("periodoTexto", dto.getPeriodoTexto())
                .addValue("coordinacionTax", dto.getCoordinacionTax() != null ? dto.getCoordinacionTax() : 0)
                .addValue("coordinacionFir", dto.getCoordinacionFir() != null ? dto.getCoordinacionFir() : 0)
                .addValue("avance", dto.getAvance() != null ? dto.getAvance() : 0);

        jdbcTemplate.update(sql, params);
    }

    public void insertAuditor(CasoSunatAuditorDTO dto) {
        String sql = """
                INSERT INTO casoSunatAuditor (idCaso, nombresApellidos, fechaInicio, fechaFin)
                VALUES (:idCaso, :nombresApellidos, :fechaInicio, :fechaFin)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCaso", dto.getIdCaso())
                .addValue("nombresApellidos", dto.getNombresApellidos())
                .addValue("fechaInicio", dto.getFechaInicio())
                .addValue("fechaFin", dto.getFechaFin());
        jdbcTemplate.update(sql, params);
    }

    public int insertDocumento(CasoSunatDocumentoDTO dto) {
        String sql = """
                INSERT INTO casoSunatDocumento (idCaso, idTipoDocumento, nroDocumento, fechaRecepcion, fechaEnvio,
                                                fechaPresentacion, hora, fechaResultado, idEstado, importeObservado,
                                                rectificatoria, importeRectificado)
                VALUES (:idCaso, :idTipoDocumento, :nroDocumento, :fechaRecepcion, :fechaEnvio,
                        :fechaPresentacion, :hora, :fechaResultado, :idEstado, :importeObservado,
                        :rectificatoria, :importeRectificado)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCaso", dto.getIdCaso())
                .addValue("idTipoDocumento", dto.getIdTipoDocumento())
                .addValue("nroDocumento", dto.getNroDocumento())
                .addValue("fechaRecepcion", dto.getFechaRecepcion())
                .addValue("fechaEnvio", dto.getFechaEnvio())
                .addValue("fechaPresentacion", dto.getFechaPresentacion())
                .addValue("hora", dto.getHora())
                .addValue("fechaResultado", dto.getFechaResultado())
                .addValue("idEstado", dto.getIdEstado())
                .addValue("importeObservado", dto.getImporteObservado())
                .addValue("rectificatoria", dto.getRectificatoria() != null ? dto.getRectificatoria() : 0)
                .addValue("importeRectificado", dto.getImporteRectificado());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public void insertEvento(CasoSunatDocumentoEventoDTO dto) {
        String sql = """
                INSERT INTO casoSunatDocumentoEvento (idCaso, idDocumento, idEmisor, idTipoEvento, idDocumentoCarta, fecha, observacion)
                VALUES (:idCaso, :idDocumento, :idEmisor, :idTipoEvento, :idDocumentoCarta, :fecha, :observacion)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCaso", dto.getIdCaso())
                .addValue("idDocumento", dto.getIdDocumento())
                .addValue("idEmisor", dto.getIdEmisor())
                .addValue("idTipoEvento", dto.getIdEvento())
                .addValue("idDocumentoCarta", dto.getIdDocumentoCarta())
                .addValue("fecha", dto.getFecha())
                .addValue("observacion", dto.getObservacion());

        jdbcTemplate.update(sql, params);
    }

    public void insertRelacion(CasoSunatDocumentoRelacionDTO dto) {
        String sql = """
                INSERT INTO casoSunatDocumentoRelacion (idCaso, idDocumentoOrigen, idDocumentoDestino, idTipoRelacion)
                VALUES (:idCaso, :idDocumentoOrigen, :idDocumentoDestino, :idTipoRelacion)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCaso", dto.getIdCaso())
                .addValue("idDocumentoOrigen", dto.getIdDocumentoOrigen())
                .addValue("idDocumentoDestino", dto.getIdDocumentoDestino())
                .addValue("idTipoRelacion", dto.getIdTipoRelacion());

        jdbcTemplate.update(sql, params);
    }

    public void deleteHijosByCaso(Integer idCaso) {
        MapSqlParameterSource params = new MapSqlParameterSource("idCaso", idCaso);
        jdbcTemplate.update("DELETE FROM casoSunatDocumentoRelacion WHERE idCaso = :idCaso", params);
        jdbcTemplate.update("DELETE FROM casoSunatDocumentoEvento WHERE idCaso = :idCaso", params);
        jdbcTemplate.update("DELETE FROM casoSunatDocumento WHERE idCaso = :idCaso", params);
        jdbcTemplate.update("DELETE FROM casoSunatAuditor WHERE idCaso = :idCaso", params);
    }

    public int delete(Integer idCaso) {
        deleteHijosByCaso(idCaso);
        String sql = "DELETE FROM casoSunat WHERE id = :idCaso";
        return jdbcTemplate.update(sql, new MapSqlParameterSource("idCaso", idCaso));
    }

    // =========================================================================
    // MÉTODOS PARA ACTUALIZACIÓN DIFERENCIAL (UPSERT - RETENER IDs REALES)
    // =========================================================================

    public List<Integer> getAuditorIdsByCaso(Integer idCaso) {
        String sql = "SELECT id FROM casoSunatAuditor WHERE idCaso = :idCaso";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("idCaso", idCaso), Integer.class);
    }

    public List<Integer> getDocumentoIdsByCaso(Integer idCaso) {
        String sql = "SELECT id FROM casoSunatDocumento WHERE idCaso = :idCaso";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("idCaso", idCaso), Integer.class);
    }

    public List<Integer> getEventoIdsByCaso(Integer idCaso) {
        String sql = "SELECT id FROM casoSunatDocumentoEvento WHERE idCaso = :idCaso";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("idCaso", idCaso), Integer.class);
    }

    public List<Integer> getRelacionIdsByCaso(Integer idCaso) {
        String sql = "SELECT id FROM casoSunatDocumentoRelacion WHERE idCaso = :idCaso";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("idCaso", idCaso), Integer.class);
    }

    public void updateAuditor(CasoSunatAuditorDTO dto, Integer realId) {
        String sql = """
                UPDATE casoSunatAuditor
                SET nombresApellidos = :nombresApellidos, fechaInicio = :fechaInicio, fechaFin = :fechaFin
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", realId)
                .addValue("nombresApellidos", dto.getNombresApellidos())
                .addValue("fechaInicio", dto.getFechaInicio())
                .addValue("fechaFin", dto.getFechaFin());
        jdbcTemplate.update(sql, params);
    }

    public void updateDocumento(CasoSunatDocumentoDTO dto, Integer realId) {
        String sql = """
                UPDATE casoSunatDocumento
                SET idTipoDocumento = :idTipoDocumento, nroDocumento = :nroDocumento,
                    fechaRecepcion = :fechaRecepcion, fechaEnvio = :fechaEnvio,
                    fechaPresentacion = :fechaPresentacion, hora = :hora,
                    fechaResultado = :fechaResultado, idEstado = :idEstado,
                    importeObservado = :importeObservado, rectificatoria = :rectificatoria,
                    importeRectificado = :importeRectificado
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", realId)
                .addValue("idTipoDocumento", dto.getIdTipoDocumento())
                .addValue("nroDocumento", dto.getNroDocumento())
                .addValue("fechaRecepcion", dto.getFechaRecepcion())
                .addValue("fechaEnvio", dto.getFechaEnvio())
                .addValue("fechaPresentacion", dto.getFechaPresentacion())
                .addValue("hora", dto.getHora())
                .addValue("fechaResultado", dto.getFechaResultado())
                .addValue("idEstado", dto.getIdEstado())
                .addValue("importeObservado", dto.getImporteObservado())
                .addValue("rectificatoria", dto.getRectificatoria() != null ? dto.getRectificatoria() : 0)
                .addValue("importeRectificado", dto.getImporteRectificado());
        jdbcTemplate.update(sql, params);
    }

    public void updateEvento(CasoSunatDocumentoEventoDTO dto, Integer realId) {
        String sql = """
                UPDATE casoSunatDocumentoEvento
                SET idDocumento = :idDocumento, idEmisor = :idEmisor, idTipoEvento = :idTipoEvento,
                    idDocumentoCarta = :idDocumentoCarta, fecha = :fecha, observacion = :observacion
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", realId)
                .addValue("idDocumento", dto.getIdDocumento())
                .addValue("idEmisor", dto.getIdEmisor())
                .addValue("idTipoEvento", dto.getIdEvento())
                .addValue("idDocumentoCarta", dto.getIdDocumentoCarta())
                .addValue("fecha", dto.getFecha())
                .addValue("observacion", dto.getObservacion());
        jdbcTemplate.update(sql, params);
    }

    public void updateRelacion(CasoSunatDocumentoRelacionDTO dto, Integer realId) {
        String sql = """
                UPDATE casoSunatDocumentoRelacion
                SET idDocumentoOrigen = :idDocumentoOrigen, idDocumentoDestino = :idDocumentoDestino,
                    idTipoRelacion = :idTipoRelacion
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", realId)
                .addValue("idDocumentoOrigen", dto.getIdDocumentoOrigen())
                .addValue("idDocumentoDestino", dto.getIdDocumentoDestino())
                .addValue("idTipoRelacion", dto.getIdTipoRelacion());
        jdbcTemplate.update(sql, params);
    }

    public void deleteAuditoresByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        jdbcTemplate.update("DELETE FROM casoSunatAuditor WHERE id IN (:ids)", params);
    }

    public void deleteDocumentosByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        jdbcTemplate.update("DELETE FROM casoSunatDocumento WHERE id IN (:ids)", params);
    }

    public void deleteEventosByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        jdbcTemplate.update("DELETE FROM casoSunatDocumentoEvento WHERE id IN (:ids)", params);
    }

    public void deleteRelacionesByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        jdbcTemplate.update("DELETE FROM casoSunatDocumentoRelacion WHERE id IN (:ids)", params);
    }
}
