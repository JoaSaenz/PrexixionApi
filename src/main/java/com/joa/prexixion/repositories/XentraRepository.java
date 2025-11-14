package com.joa.prexixion.repositories;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.dto.XentraDataDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

@Repository
public class XentraRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public int insertarXentraDB(XentraDataDTO request) {

        // Inserción con OUTPUT para obtener el ID generado
        String insertSql = """
                    INSERT INTO xentraData (
                        idArea, idSubArea, abreviatura, nombre, responsable, fechaInicio, fechaFin,
                        tipoRepeticion, diasSemana, intervaloSemanas, mesesPermitidos, diaInicioMes, diaFinMes, estado)
                    OUTPUT INSERTED.id
                    VALUES (
                        :idArea, :idSubArea, :abreviatura, :nombre, :responsable, :fechaInicio, :fechaFin,
                        :tipoRepeticion, :diasSemana, :intervaloSemanas, :mesesPermitidos, :diaInicioMes, :diaFinMes, :estado)
                """;

        Query insertQuery = em.createNativeQuery(insertSql);
        insertQuery.setParameter("idArea", request.getIdArea());
        insertQuery.setParameter("idSubArea", request.getIdSubArea());
        insertQuery.setParameter("abreviatura", request.getAbreviatura());
        insertQuery.setParameter("nombre", request.getNombre());
        insertQuery.setParameter("responsable", request.getResponsable());
        insertQuery.setParameter("fechaInicio", request.getFechaInicio());
        insertQuery.setParameter("fechaFin", request.getFechaFin());
        insertQuery.setParameter("tipoRepeticion", request.getTipoRepeticion());
        insertQuery.setParameter("diasSemana", request.getDiasSemanaString());
        insertQuery.setParameter("intervaloSemanas", request.getIntervaloSemanas());
        insertQuery.setParameter("mesesPermitidos", request.getMesesPermitidosString());
        insertQuery.setParameter("diaInicioMes", request.getDiaInicioMes());
        insertQuery.setParameter("diaFinMes", request.getDiaFinMes());
        insertQuery.setParameter("estado", request.getEstado());

        Object result = insertQuery.getSingleResult();
        int idGenerado = ((Number) result).intValue();

        insertarFechas(idGenerado, request.getFechas());

        return idGenerado;

    }

    @Transactional
    public int editarXentraDB(XentraDataDTO xentra, String fechaFinFront) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 1. Obtener fechas
        LocalDate fechaFinActual = LocalDate.parse(xentra.getFechaFin(), formatter);
        LocalDate fechaFinNueva = LocalDate.parse(fechaFinFront, formatter);

        // 2. Ejecutar UPDATE
        String updateSql = """
                    UPDATE xentraData set
                        fechaFin = :fechaFin
                    WHERE id = :id
                """;

        Query query = em.createNativeQuery(updateSql);
        query.setParameter("fechaFin", fechaFinFront);
        query.setParameter("id", xentra.getId());
        query.executeUpdate();

        // 3. Comparar fechaFin ACTUAL vs NUEVA
        if (fechaFinNueva.isAfter(fechaFinActual)) {
            // Extensión → Insertar nuevas fechas

            // 1. Obtener las fechas que ya existen en BD
            List<String> fechasExistentesStr = em.createNativeQuery(
                    "SELECT fecha FROM xentraFechas WHERE idXentra = :idXentra")
                    .setParameter("idXentra", xentra.getId())
                    .getResultList();
            List<LocalDate> fechasExistentes = fechasExistentesStr.stream()
                    .map(f -> LocalDate.parse(f, formatter))
                    .toList();

            // 2. Filtrar solo las nuevas (las que están en request pero no en BD)
            List<LocalDate> nuevasFechas = xentra.getFechas().stream()
                    .filter(f -> !fechasExistentes.contains(f))
                    .toList();

            // 3. Insertar masivamente con el método existente
            if (!nuevasFechas.isEmpty()) {
                insertarFechas(xentra.getId(), nuevasFechas);
            }

        } else if (fechaFinNueva.isBefore(fechaFinActual)) {
            // Acortamiento → eliminar fechas sobrantes y desactivar xentraData
            eliminarFechasPosteriores(xentra.getId(), fechaFinNueva);

            // Cambiar a estado INACTIVO siempre y cuando la nueva fecha sea menor a la
            // fecha actual
            if (fechaFinNueva.isBefore(LocalDate.now())) {
                desactivarXentraData(xentra.getId());
            }
        }

        return xentra.getId();

    }

    public void insertarFechas(int idXentra, List<LocalDate> fechas) {
        int batchSize = 50;
        int i = 0;

        for (LocalDate fecha : fechas) {
            String sql = "INSERT INTO xentraFechas (idXentra, fecha, idEstado, estadoLogico) VALUES (:idXentra, :fecha, :idEstado, :estadoLogico)";
            Query query = em.createNativeQuery(sql);
            query.setParameter("idXentra", idXentra);
            query.setParameter("fecha", fecha);
            query.setParameter("idEstado", 1); // Insertar 1:Pendiente por defecto
            query.setParameter("estadoLogico", "PENDIENTE"); // Insertar: Pendiente por defecto
            query.executeUpdate();

            i++;
            if (i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }

        em.flush();
        em.clear();
    }

    // Elimina físicamente las fechas sobrantes
    public void eliminarFechasPosteriores(int idXentra, LocalDate nuevaFechaFin) {
        String sql = """
                DELETE FROM xentraFechas
                WHERE idXentra = :idXentra
                AND fecha > :nuevaFechaFin
                """;
        Query query = em.createNativeQuery(sql);
        query.setParameter("idXentra", idXentra);
        query.setParameter("nuevaFechaFin", nuevaFechaFin);
        query.executeUpdate();
    }

    // Marca el registro principal como INACTIVO
    public void desactivarXentraData(int idXentra) {
        String sql = "UPDATE xentraData SET estado = 'INACTIVO' WHERE id = :idXentra";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idXentra", idXentra);
        query.executeUpdate();
    }

    public List<XentraDataDTO> list(int idPuesto, int idArea) {
        List<XentraDataDTO> list = new ArrayList<>();

        String sql = """
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
                """;

        if (idPuesto == 3) {
            if (idArea == 2) {
                sql += """
                        WHERE p.idArea in (2,4)
                        """;
            } else {
                sql += """
                        WHERE p.idArea = :idArea
                        """;
            }
        }

        Query query = em.createNativeQuery(sql, Tuple.class);
        if (idPuesto == 3) {
            if (idArea != 2) {
                query.setParameter("idArea", idArea);
            }
        }
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            XentraDataDTO obj = new XentraDataDTO();
            obj.setId(tuple.get("id", Integer.class));
            obj.setIdArea(tuple.get("idArea", Integer.class));
            obj.setDescArea(tuple.get("descArea", String.class));
            obj.setIdSubArea(tuple.get("idSubArea", Integer.class));
            obj.setDescSubArea(tuple.get("descSubArea", String.class));
            obj.setNombreReporte(tuple.get("nombreReporte", String.class));
            obj.setResponsable(tuple.get("responsable", String.class));
            obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
            obj.setFechaInicio(tuple.get("fechaInicio", String.class));
            obj.setFechaFin(tuple.get("fechaFin", String.class));
            obj.setTipoRepeticion(tuple.get("tipoRepeticion", String.class));
            obj.setDiasSemanaString(tuple.get("diasSemana", String.class));
            obj.setMesesPermitidosString(tuple.get("mesesPermitidos", String.class));
            obj.setDiaInicioMes(tuple.get("diaInicioMes", Integer.class));
            obj.setDiaFinMes(tuple.get("diaFinMes", Integer.class));
            obj.setEstado(tuple.get("estado", String.class));
            list.add(obj);
        }

        return list;
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

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("id", id);
        Tuple tuple = (Tuple) query.getSingleResult();

        XentraDataDTO obj = new XentraDataDTO();
        obj.setId(tuple.get("id", Integer.class));
        obj.setIdArea(tuple.get("idArea", Integer.class));
        obj.setDescArea(tuple.get("descArea", String.class));
        obj.setIdSubArea(tuple.get("idSubArea", Integer.class));
        obj.setDescSubArea(tuple.get("descSubArea", String.class));
        obj.setAbreviatura(tuple.get("abreviatura", String.class));
        obj.setNombre(tuple.get("nombre", String.class));
        obj.setResponsable(tuple.get("responsable", String.class));
        obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
        obj.setFechaInicio(tuple.get("fechaInicio", String.class));
        obj.setFechaFin(tuple.get("fechaFin", String.class));
        obj.setTipoRepeticion(tuple.get("tipoRepeticion", String.class));
        obj.setDiasSemanaString(tuple.get("diasSemana", String.class));
        obj.setIntervaloSemanas(tuple.get("intervaloSemanas", Integer.class));
        obj.setMesesPermitidosString(tuple.get("mesesPermitidos", String.class));
        obj.setDiaInicioMes(tuple.get("diaInicioMes", Integer.class));
        obj.setDiaFinMes(tuple.get("diaFinMes", Integer.class));
        obj.setEstado(tuple.get("estado", String.class));

        return obj;
    }

    public List<XentraDataDTO> getListXentraFechas(int idPuesto, int idArea, int idSubArea, String dni) {
        List<XentraDataDTO> list = new ArrayList<>();
        String sql = "";

        if (idPuesto == 2) {
            sql = """
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
                    """;
        } else if (idPuesto == 3) {
            if (idArea == 2) {
                sql = """
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
                        """;
            } else {
                sql = """
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
                        """;
            }
        } else if (idPuesto == 4) {
            sql = """
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
                    """;
        } else {
            sql = """
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
                    """;
        }

        Query query = em.createNativeQuery(sql, Tuple.class);
        if (idPuesto != 2) {
            if (idPuesto == 3) {
                if (idArea != 2) {
                    query.setParameter("idArea", idArea);
                }
            } else if (idPuesto == 4) {
                query.setParameter("idSubArea", idSubArea);
            } else {
                query.setParameter("dni", dni);
            }
        }
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            XentraDataDTO obj = new XentraDataDTO();
            obj.setIdArea(tuple.get("idArea", Integer.class));
            obj.setIdSubArea(tuple.get("idSubArea", Integer.class));
            obj.setNombreReporte(tuple.get("nombreReporte", String.class));
            obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
            obj.setFecha(tuple.get("fecha", String.class));
            obj.setIdEstadoLogico(tuple.get("idEstadoLogico", Integer.class));
            obj.setEstadoLogico(tuple.get("estadoLogico", String.class));
            obj.setColor(tuple.get("color", String.class));
            list.add(obj);
        }

        return list;
    }

    public Set<LocalDate> obtenerFeriados() {
        Set<LocalDate> feriados = new HashSet<>();

        String sql = """
                SELECT fecha FROM cronogramaFeriados;
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            Date fechaSql = tuple.get("fecha", Date.class);
            if (fechaSql != null) {
                LocalDate fecha = fechaSql.toLocalDate();
                feriados.add(fecha);
            }
        }

        return feriados;
    }

    @Transactional
    public int delete(int id) {
        // Elimina primero las fechas relacionadas (clave foránea)
        String deleteFechasSql = "DELETE FROM xentraFechas WHERE idXentra = :id";
        Query deleteFechasQuery = em.createNativeQuery(deleteFechasSql);
        deleteFechasQuery.setParameter("id", id);
        deleteFechasQuery.executeUpdate();

        // Luego elimina el registro principal
        String deleteSql = "DELETE FROM xentraData WHERE id = :id";
        Query deleteQuery = em.createNativeQuery(deleteSql);
        deleteQuery.setParameter("id", id);

        int rpta = deleteQuery.executeUpdate(); // Retorna 1 si se eliminó, 0 si no se encontró
        return (rpta == 1) ? 3 : 0;
    }
}
