package com.joa.prexixion.repositories;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.XentraRequest;

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
    public int insertarDataGeneral(XentraRequest request) {

        String diasSemana = request.getDiasSemana().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        if (request.getId() > 0) {
            // UPDATE si el ID ya existe
            String updateSql = """
                        update xentraData set
                            idArea = :idArea,
                            idSubArea = :idSubArea,
                            nombre = :nombre,
                            proceso = :proceso,
                            color = :color,
                            responsable = :responsable,
                            fechaInicio = :fechaInicio,
                            fechaFin = :fechaFin,
                            tipoRepeticion = :tipoRepeticion,
                            diasSemana = :diasSemana,
                            intervaloSemanas = :intervaloSemanas,
                            diaInicioMes = :diaInicioMes,
                            diaFinMes = :diaFinMes
                        where id = :id
                    """;

            Query query = em.createNativeQuery(updateSql);
            query.setParameter("idArea", request.getIdArea());
            query.setParameter("idSubArea", request.getIdSubArea());
            query.setParameter("nombre", request.getNombre());
            query.setParameter("proceso", request.getProceso());
            query.setParameter("color", request.getColor());
            query.setParameter("responsable", request.getResponsable());
            query.setParameter("fechaInicio", request.getFechaInicio());
            query.setParameter("fechaFin", request.getFechaFin());
            query.setParameter("tipoRepeticion", request.getTipoRepeticion());
            query.setParameter("diasSemana", diasSemana);
            query.setParameter("intervaloSemanas", 1);
            query.setParameter("diaInicioMes", request.getDiaInicioMes());
            query.setParameter("diaFinMes", request.getDiaFinMes());
            query.setParameter("id", request.getId());

            query.executeUpdate();
            return request.getId();
        } else {
            // Inserción con OUTPUT para obtener el ID generado
            String insertSql = """
                        INSERT INTO xentraData (
                            idArea, idSubArea, nombre, proceso, color, responsable, fechaInicio, fechaFin,
                            tipoRepeticion, diasSemana, intervaloSemanas, diaInicioMes, diaFinMes)
                        OUTPUT INSERTED.id
                        VALUES (
                            :idArea, :idSubArea, :nombre, :proceso, :color, :responsable, :fechaInicio, :fechaFin,
                            :tipoRepeticion, :diasSemana, :intervaloSemanas, :diaInicioMes, :diaFinMes)
                    """;

            Query insertQuery = em.createNativeQuery(insertSql);
            insertQuery.setParameter("idArea", request.getIdArea());
            insertQuery.setParameter("idSubArea", request.getIdSubArea());
            insertQuery.setParameter("nombre", request.getNombre());
            insertQuery.setParameter("proceso", request.getProceso());
            insertQuery.setParameter("color", request.getColor());
            insertQuery.setParameter("responsable", request.getResponsable());
            insertQuery.setParameter("fechaInicio", request.getFechaInicio());
            insertQuery.setParameter("fechaFin", request.getFechaFin());
            insertQuery.setParameter("tipoRepeticion", request.getTipoRepeticion());
            insertQuery.setParameter("diasSemana", diasSemana);
            insertQuery.setParameter("intervaloSemanas", request.getIntervaloSemanas());
            insertQuery.setParameter("diaInicioMes", request.getDiaInicioMes());
            insertQuery.setParameter("diaFinMes", request.getDiaFinMes());

            Object result = insertQuery.getSingleResult();
            int idGenerado = ((Number) result).intValue();

            insertarFechas(idGenerado, request.getFechas());

            return idGenerado;
        }
    }

    public void insertarFechas(int idXentra, List<LocalDate> fechas) {
        int batchSize = 50;
        int i = 0;

        for (LocalDate fecha : fechas) {
            String sql = "INSERT INTO xentraFechas (idXentra, fecha) VALUES (:idXentra, :fecha)";
            Query query = em.createNativeQuery(sql);
            query.setParameter("idXentra", idXentra);
            query.setParameter("fecha", fecha);
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

    public List<XentraRequest> list() {
        List<XentraRequest> list = new ArrayList<>();

        String sql = """
                SELECT x.id, x.idArea, a.descripcion AS descArea, x.idSubArea, ps.descripcion as descSubArea,
                x.nombre, x.proceso, x.responsable,
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
                x.diasSemana, x.diaInicioMes, x.diaFinMes
                FROM xentraData x
                LEFT JOIN areas a ON x.idArea = a.id
                LEFT JOIN personalSubAreas ps ON x.idSubArea = ps.id
                LEFT JOIN personal p ON x.responsable = p.dni
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            XentraRequest obj = new XentraRequest();
            obj.setId(tuple.get("id", Integer.class));
            obj.setIdArea(tuple.get("idArea", Integer.class));
            obj.setDescArea(tuple.get("descArea", String.class));
            obj.setIdSubArea(tuple.get("idSubArea", Integer.class));
            obj.setDescSubArea(tuple.get("descSubArea", String.class));
            obj.setNombre(tuple.get("nombre", String.class));
            obj.setProceso(tuple.get("proceso", String.class));
            obj.setResponsable(tuple.get("responsable", String.class));
            obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
            obj.setFechaInicio(tuple.get("fechaInicio", String.class));
            obj.setFechaFin(tuple.get("fechaFin", String.class));
            obj.setTipoRepeticion(tuple.get("tipoRepeticion", String.class));
            obj.setDiasSemanaString(tuple.get("diasSemana", String.class));
            obj.setDiaInicioMes(tuple.get("diaInicioMes", Integer.class));
            obj.setDiaFinMes(tuple.get("diaFinMes", Integer.class));
            list.add(obj);
        }

        return list;
    }

    public XentraRequest getOne(int id) {
        String sql = """
                SELECT x.id, x.idArea, a.descripcion AS descArea, x.idSubArea, ps.descripcion as descSubArea,
                x.nombre, x.proceso, x.color, x.responsable,
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
                x.diasSemana, x.intervaloSemanas, x.diaInicioMes, x.diaFinMes
                FROM xentraData x
                LEFT JOIN areas a ON x.idArea = a.id
                LEFT JOIN personalSubAreas ps ON x.idSubArea = ps.id
                LEFT JOIN personal p ON x.responsable = p.dni
                WHERE x.id = :id
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("id", id);
        Tuple tuple = (Tuple) query.getSingleResult();

        XentraRequest obj = new XentraRequest();
        obj.setId(tuple.get("id", Integer.class));
        obj.setIdArea(tuple.get("idArea", Integer.class));
        obj.setDescArea(tuple.get("descArea", String.class));
        obj.setIdSubArea(tuple.get("idSubArea", Integer.class));
        obj.setDescSubArea(tuple.get("descSubArea", String.class));
        obj.setNombre(tuple.get("nombre", String.class));
        obj.setProceso(tuple.get("proceso", String.class));
        obj.setColor(tuple.get("color", String.class));
        obj.setResponsable(tuple.get("responsable", String.class));
        obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
        obj.setFechaInicio(tuple.get("fechaInicio", String.class));
        obj.setFechaFin(tuple.get("fechaFin", String.class));
        obj.setTipoRepeticion(tuple.get("tipoRepeticion", String.class));
        obj.setDiasSemanaString(tuple.get("diasSemana", String.class));
        obj.setIntervaloSemanas(tuple.get("intervaloSemanas", Integer.class));
        obj.setDiaInicioMes(tuple.get("diaInicioMes", Integer.class));
        obj.setDiaFinMes(tuple.get("diaFinMes", Integer.class));

        return obj;
    }

    public List<XentraRequest> getListXentraFechas() {
        List<XentraRequest> list = new ArrayList<>();

        String sql = """
                SELECT x.nombre, x.responsable,
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
                AS responsableNombreApellido, 0 as idEstado, xf.fecha, x.color
                FROM xentraFechas xf
                LEFT JOIN xentraData x ON xf.idXentra = x.id
                LEFT JOIN personal p ON x.responsable = p.dni
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            XentraRequest obj = new XentraRequest();
            obj.setNombre(tuple.get("nombre", String.class));
            obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
            obj.setIdEstado(tuple.get("idEstado", Integer.class));
            obj.setFecha(tuple.get("fecha", String.class));
            obj.setColor(tuple.get("color", String.class));
            list.add(obj);
        }

        return list;
    }
}
