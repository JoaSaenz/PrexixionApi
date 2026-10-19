package com.joa.prexixionapi.modules.xentra.repositories;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.modules.xentra.dto.XentraDataDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class XentraRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public int insertarXentraDB(XentraDataDTO request) {
        String insertSql = """
                INSERT INTO xentraData (
                    idArea, idSubArea, abreviatura, nombre, responsable, fechaInicio, fechaFin,
                    tipoRepeticion, diasSemana, intervaloSemanas, mesesPermitidos, diaInicioMes, diaFinMes, estado)
                OUTPUT INSERTED.id
                VALUES (
                    :idArea, :idSubArea, :abreviatura, :nombre, :responsable, :fechaInicio, :fechaFin,
                    :tipoRepeticion, :diasSemana, :intervaloSemanas, :mesesPermitidos, :diaInicioMes, :diaFinMes, :estado)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idArea", request.getIdArea())
                .addValue("idSubArea", request.getIdSubArea())
                .addValue("abreviatura", request.getAbreviatura())
                .addValue("nombre", request.getNombre())
                .addValue("responsable", request.getResponsable())
                .addValue("fechaInicio", request.getFechaInicio())
                .addValue("fechaFin", request.getFechaFin())
                .addValue("tipoRepeticion", request.getTipoRepeticion())
                .addValue("diasSemana", request.getDiasSemanaString())
                .addValue("intervaloSemanas", request.getIntervaloSemanas())
                .addValue("mesesPermitidos", request.getMesesPermitidosString())
                .addValue("diaInicioMes", request.getDiaInicioMes())
                .addValue("diaFinMes", request.getDiaFinMes())
                .addValue("estado", request.getEstado());

        Integer idGenerado = jdbcTemplate.queryForObject(insertSql, params, Integer.class);
        if (idGenerado == null) {
            idGenerado = 0;
        }

        insertarFechas(idGenerado, request.getFechas());
        return idGenerado;
    }

    @Transactional
    public int edit(XentraDataDTO xentra, String fechaFinFront) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 1. Obtener fechas
        LocalDate fechaFinActual = LocalDate.parse(xentra.getFechaFin(), formatter);
        LocalDate fechaFinNueva = LocalDate.parse(fechaFinFront, formatter);

        // 2. Ejecutar UPDATE
        String updateSql = """
                UPDATE xentraData SET
                    fechaFin = :fechaFin
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fechaFin", fechaFinFront)
                .addValue("id", xentra.getId());
        jdbcTemplate.update(updateSql, params);

        // 3. Comparar fechaFin ACTUAL vs NUEVA
        if (fechaFinNueva.isAfter(fechaFinActual)) {
            // Extensión → Insertar nuevas fechas
            List<String> fechasExistentesStr = jdbcTemplate.queryForList(
                    "SELECT fecha FROM xentraFechas WHERE idXentra = :idXentra",
                    new MapSqlParameterSource("idXentra", xentra.getId()),
                    String.class);

            List<LocalDate> fechasExistentes = fechasExistentesStr.stream()
                    .map(f -> LocalDate.parse(f, formatter))
                    .toList();

            List<LocalDate> nuevasFechas = xentra.getFechas().stream()
                    .filter(f -> !fechasExistentes.contains(f))
                    .toList();

            if (!nuevasFechas.isEmpty()) {
                insertarFechas(xentra.getId(), nuevasFechas);
            }

        } else if (fechaFinNueva.isBefore(fechaFinActual)) {
            // Acortamiento → eliminar fechas sobrantes y desactivar xentraData
            eliminarFechasPosteriores(xentra.getId(), fechaFinNueva);

            if (fechaFinNueva.isBefore(LocalDate.now())) {
                desactivarXentraData(xentra.getId());
            }
        }

        return xentra.getId();
    }

    public void insertarFechas(int idXentra, List<LocalDate> fechas) {
        if (fechas == null || fechas.isEmpty())
            return;

        String sql = "INSERT INTO xentraFechas (idXentra, fecha, idEstado, estadoLogico) VALUES (:idXentra, :fecha, :idEstado, :estadoLogico)";
        MapSqlParameterSource[] batch = fechas.stream()
                .map(fecha -> new MapSqlParameterSource()
                        .addValue("idXentra", idXentra)
                        .addValue("fecha", fecha)
                        .addValue("idEstado", 1)
                        .addValue("estadoLogico", "PENDIENTE"))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batch);
    }

    // Elimina físicamente las fechas sobrantes
    public void eliminarFechasPosteriores(int idXentra, LocalDate nuevaFechaFin) {
        String sql = """
                DELETE FROM xentraFechas
                WHERE idXentra = :idXentra
                AND fecha > :nuevaFechaFin
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idXentra", idXentra)
                .addValue("nuevaFechaFin", nuevaFechaFin);
        jdbcTemplate.update(sql, params);
    }

    // Marca el registro principal como INACTIVO
    public void desactivarXentraData(int idXentra) {
        String sql = "UPDATE xentraData SET estado = 'INACTIVO' WHERE id = :idXentra";
        jdbcTemplate.update(sql, new MapSqlParameterSource("idXentra", idXentra));
    }

    public List<XentraDataDTO> list(int idPuesto, int idArea) {
        StringBuilder sql = new StringBuilder("""
                SELECT x.id, x.idArea, a.descripcion AS descArea, x.idSubArea, ps.descripcion as descSubArea,
                CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte,  x.responsable,
                CONCAT (
                        SUBSTRING (p.nombres, 1,
                        CASE
                        WHEN CHARINDEX(' ', p.nombres)-1 < 0
                        THEN LEN (p.nombres)
                        ELSE CHARINDEX(' ', p.nombres)-1
                        END) ,
                        ' ',
                        SUBSTRING (p.apellidos, 1,
                        CASE
                        WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                        THEN LEN (p.apellidos)
                        ELSE CHARINDEX(' ', p.apellidos)-1
                        END)
                		)
                AS responsableNombreApellido,
                x.fechaInicio, x.fechaFin, x.tipoRepeticion,
                x.diasSemana,
                CASE WHEN tipoRepeticion = 'DIARIA' THEN '0'
                    WHEN tipoRepeticion = 'SEMANAL' THEN '0'
                    WHEN tipoRepeticion = 'MENSUAL' THEN x.mesesPermitidos
                END AS mesesPermitidos,
                x.diaInicioMes, x.diaFinMes, x.estado
                FROM xentraData x
                LEFT JOIN areas a ON x.idArea = a.id
                LEFT JOIN personalSubAreas ps ON x.idSubArea = ps.id
                LEFT JOIN personal p ON x.responsable = p.dni
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (idPuesto == 3) {
            if (idArea == 2) {
                sql.append(" WHERE p.idArea in (2,4) ");
            } else {
                sql.append(" WHERE p.idArea = :idArea ");
                params.addValue("idArea", idArea);
            }
        }

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            XentraDataDTO obj = new XentraDataDTO();
            obj.setId(rs.getInt("id"));
            obj.setIdArea(rs.getInt("idArea"));
            obj.setDescArea(rs.getString("descArea"));
            obj.setIdSubArea(rs.getInt("idSubArea"));
            obj.setDescSubArea(rs.getString("descSubArea"));
            obj.setNombreReporte(rs.getString("nombreReporte"));
            obj.setResponsable(rs.getString("responsable"));
            obj.setResponsableNombreApellido(rs.getString("responsableNombreApellido"));
            obj.setFechaInicio(rs.getString("fechaInicio"));
            obj.setFechaFin(rs.getString("fechaFin"));
            obj.setTipoRepeticion(rs.getString("tipoRepeticion"));
            obj.setDiasSemanaString(rs.getString("diasSemana"));
            obj.setMesesPermitidosString(rs.getString("mesesPermitidos"));
            obj.setDiaInicioMes(rs.getInt("diaInicioMes"));
            obj.setDiaFinMes(rs.getInt("diaFinMes"));
            obj.setEstado(rs.getString("estado"));
            return obj;
        });
    }

    public XentraDataDTO getOne(int id) {
        String sql = """
                SELECT x.id, x.idArea, a.descripcion AS descArea, x.idSubArea, ps.descripcion as descSubArea,
                x.abreviatura, x.nombre, x.responsable,
                CONCAT (
                        SUBSTRING (p.nombres, 1,
                        CASE
                        WHEN CHARINDEX(' ', p.nombres)-1 < 0
                        THEN LEN (p.nombres)
                        ELSE CHARINDEX(' ', p.nombres)-1
                        END) ,
                        ' ',
                        SUBSTRING (p.apellidos, 1,
                        CASE
                        WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                        THEN LEN (p.apellidos)
                        ELSE CHARINDEX(' ', p.apellidos)-1
                        END)
                		)
                AS responsableNombreApellido,
                x.fechaInicio, x.fechaFin, x.tipoRepeticion,
                x.diasSemana, x.intervaloSemanas, x.mesesPermitidos, x.diaInicioMes, x.diaFinMes, x.estado
                FROM xentraData x
                LEFT JOIN areas a ON x.idArea = a.id
                LEFT JOIN personalSubAreas ps ON x.idSubArea = ps.id
                LEFT JOIN personal p ON x.responsable = p.dni
                WHERE x.id = :id
                """;

        List<XentraDataDTO> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), (rs, rowNum) -> {
            XentraDataDTO obj = new XentraDataDTO();
            obj.setId(rs.getInt("id"));
            obj.setIdArea(rs.getInt("idArea"));
            obj.setDescArea(rs.getString("descArea"));
            obj.setIdSubArea(rs.getInt("idSubArea"));
            obj.setDescSubArea(rs.getString("descSubArea"));
            obj.setAbreviatura(rs.getString("abreviatura"));
            obj.setNombre(rs.getString("nombre"));
            obj.setResponsable(rs.getString("responsable"));
            obj.setResponsableNombreApellido(rs.getString("responsableNombreApellido"));
            obj.setFechaInicio(rs.getString("fechaInicio"));
            obj.setFechaFin(rs.getString("fechaFin"));
            obj.setTipoRepeticion(rs.getString("tipoRepeticion"));
            obj.setDiasSemanaString(rs.getString("diasSemana"));
            obj.setIntervaloSemanas(rs.getInt("intervaloSemanas"));
            obj.setMesesPermitidosString(rs.getString("mesesPermitidos"));
            obj.setDiaInicioMes(rs.getInt("diaInicioMes"));
            obj.setDiaFinMes(rs.getInt("diaFinMes"));
            obj.setEstado(rs.getString("estado"));
            return obj;
        });

        return list.isEmpty() ? null : list.get(0);
    }

    public List<XentraDataDTO> getFechas(int idPuesto, int idArea, int idSubArea, String dni) {
        StringBuilder sql = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (idPuesto == 2) {
            sql.append(
                    """
                            SELECT x.idArea, x.idSubArea,
                            CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte, x.responsable,
                            CONCAT (
                                    SUBSTRING (p.nombres, 1,
                                    CASE
                                    WHEN CHARINDEX(' ', p.nombres)-1 < 0
                                    THEN LEN (p.nombres)
                                    ELSE CHARINDEX(' ', p.nombres)-1
                                    END) ,
                                    ' ',
                                    SUBSTRING (p.apellidos, 1,
                                    CASE
                                    WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                                    THEN LEN (p.apellidos)
                                    ELSE CHARINDEX(' ', p.apellidos)-1
                                    END)
                            		)
                            AS responsableNombreApellido, xf.idEstado, xf.fecha, xfe.id as idEstadoLogico, xf.estadoLogico, p.color
                            FROM xentraFechas xf
                            LEFT JOIN xentraData x ON xf.idXentra = x.id
                            LEFT JOIN personal p ON x.responsable = p.dni
                            LEFT JOIN xentraFechasEstadosLogicos xfe ON xf.estadoLogico = xfe.descripcion
                            """);
        } else if (idPuesto == 3) {
            if (idArea == 2) {
                sql.append(
                        """
                                SELECT x.idArea, x.idSubArea,
                                CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte, x.responsable,
                                CONCAT (
                                        SUBSTRING (p.nombres, 1,
                                        CASE
                                        WHEN CHARINDEX(' ', p.nombres)-1 < 0
                                        THEN LEN (p.nombres)
                                        ELSE CHARINDEX(' ', p.nombres)-1
                                        END) ,
                                        ' ',
                                        SUBSTRING (p.apellidos, 1,
                                        CASE
                                        WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                                        THEN LEN (p.apellidos)
                                        ELSE CHARINDEX(' ', p.apellidos)-1
                                        END)
                                		)
                                AS responsableNombreApellido, xf.idEstado, xf.fecha, xfe.id as idEstadoLogico, xf.estadoLogico, p.color
                                FROM xentraFechas xf
                                LEFT JOIN xentraData x ON xf.idXentra = x.id
                                LEFT JOIN personal p ON x.responsable = p.dni
                                LEFT JOIN xentraFechasEstadosLogicos xfe ON xf.estadoLogico = xfe.descripcion
                                WHERE p.idArea in (2,4)
                                """);
            } else {
                sql.append(
                        """
                                SELECT x.idArea, x.idSubArea,
                                CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte, x.responsable,
                                CONCAT (
                                        SUBSTRING (p.nombres, 1,
                                        CASE
                                        WHEN CHARINDEX(' ', p.nombres)-1 < 0
                                        THEN LEN (p.nombres)
                                        ELSE CHARINDEX(' ', p.nombres)-1
                                        END) ,
                                        ' ',
                                        SUBSTRING (p.apellidos, 1,
                                        CASE
                                        WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                                        THEN LEN (p.apellidos)
                                        ELSE CHARINDEX(' ', p.apellidos)-1
                                        END)
                                		)
                                AS responsableNombreApellido, xf.idEstado, xf.fecha, xfe.id as idEstadoLogico, xf.estadoLogico, p.color
                                FROM xentraFechas xf
                                LEFT JOIN xentraData x ON xf.idXentra = x.id
                                LEFT JOIN personal p ON x.responsable = p.dni
                                LEFT JOIN xentraFechasEstadosLogicos xfe ON xf.estadoLogico = xfe.descripcion
                                WHERE p.idArea = :idArea
                                """);
                params.addValue("idArea", idArea);
            }
        } else if (idPuesto == 4) {
            sql.append(
                    """
                            SELECT x.idArea, x.idSubArea,
                            CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte, x.responsable,
                            CONCAT (
                                    SUBSTRING (p.nombres, 1,
                                    CASE
                                    WHEN CHARINDEX(' ', p.nombres)-1 < 0
                                    THEN LEN (p.nombres)
                                    ELSE CHARINDEX(' ', p.nombres)-1
                                    END) ,
                                    ' ',
                                    SUBSTRING (p.apellidos, 1,
                                    CASE
                                    WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                                    THEN LEN (p.apellidos)
                                    ELSE CHARINDEX(' ', p.apellidos)-1
                                    END)
                            		)
                            AS responsableNombreApellido, xf.idEstado, xf.fecha, xfe.id as idEstadoLogico, xf.estadoLogico, p.color
                            FROM xentraFechas xf
                            LEFT JOIN xentraData x ON xf.idXentra = x.id
                            LEFT JOIN personal p ON x.responsable = p.dni
                            LEFT JOIN xentraFechasEstadosLogicos xfe ON xf.estadoLogico = xfe.descripcion
                            WHERE p.idSubArea = :idSubArea
                            """);
            params.addValue("idSubArea", idSubArea);
        } else {
            sql.append(
                    """
                            SELECT x.idArea, x.idSubArea,
                            CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte, x.responsable,
                            CONCAT (
                                    SUBSTRING (p.nombres, 1,
                                    CASE
                                    WHEN CHARINDEX(' ', p.nombres)-1 < 0
                                    THEN LEN (p.nombres)
                                    ELSE CHARINDEX(' ', p.nombres)-1
                                    END) ,
                                    ' ',
                                    SUBSTRING (p.apellidos, 1,
                                    CASE
                                    WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                                    THEN LEN (p.apellidos)
                                    ELSE CHARINDEX(' ', p.apellidos)-1
                                    END)
                            		)
                            AS responsableNombreApellido, xf.idEstado, xf.fecha, xfe.id as idEstadoLogico, xf.estadoLogico, p.color
                            FROM xentraFechas xf
                            LEFT JOIN xentraData x ON xf.idXentra = x.id
                            LEFT JOIN personal p ON x.responsable = p.dni
                            LEFT JOIN xentraFechasEstadosLogicos xfe ON xf.estadoLogico = xfe.descripcion
                            WHERE x.responsable = :dni
                            """);
            params.addValue("dni", dni);
        }

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            XentraDataDTO obj = new XentraDataDTO();
            obj.setIdArea(rs.getInt("idArea"));
            obj.setIdSubArea(rs.getInt("idSubArea"));
            obj.setNombreReporte(rs.getString("nombreReporte"));
            obj.setResponsableNombreApellido(rs.getString("responsableNombreApellido"));
            obj.setFecha(rs.getString("fecha"));
            obj.setIdEstadoLogico(rs.getInt("idEstadoLogico"));
            obj.setEstadoLogico(rs.getString("estadoLogico"));
            obj.setColor(rs.getString("color"));
            return obj;
        });
    }

    public Set<LocalDate> obtenerFeriados() {
        String sql = "SELECT fecha FROM cronogramaFeriados";
        List<LocalDate> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Date d = rs.getDate("fecha");
            return d != null ? d.toLocalDate() : null;
        });
        return new HashSet<>(list.stream().filter(java.util.Objects::nonNull).toList());
    }

    @Transactional
    public int delete(int id) {
        // Elimina primero las fechas relacionadas (clave foránea)
        String deleteFechasSql = "DELETE FROM xentraFechas WHERE idXentra = :id";
        jdbcTemplate.update(deleteFechasSql, new MapSqlParameterSource("id", id));

        // Luego elimina el registro principal
        String deleteSql = "DELETE FROM xentraData WHERE id = :id";
        int rpta = jdbcTemplate.update(deleteSql, new MapSqlParameterSource("id", id));
        return (rpta == 1) ? 3 : 0;
    }
}
