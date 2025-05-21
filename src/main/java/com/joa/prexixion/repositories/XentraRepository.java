package com.joa.prexixion.repositories;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.XentraRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
                            idNombreReporte = :idNombreReporte,
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
            query.setParameter("idNombreReporte", request.getIdNombreReporte());
            query.setParameter("responsable", request.getResponsable());
            query.setParameter("fechaInicio", request.getFechaInicio());
            query.setParameter("fechaFin", request.getFechaFin());
            query.setParameter("tipoRepeticion", request.getTipoRepeticion());
            query.setParameter("diasSemana", diasSemana);
            query.setParameter("intervaloSemanas", request.getIntervaloSemanas());
            query.setParameter("diaInicioMes", request.getDiaInicioMes());
            query.setParameter("diaFinMes", request.getDiaFinMes());
            query.setParameter("id", request.getId());

            query.executeUpdate();
            return request.getId();
        } else {
            // Inserción con OUTPUT para obtener el ID generado
            String insertSql = """
                        INSERT INTO xentraData (
                            idNombreReporte, responsable, fechaInicio, fechaFin,
                            tipoRepeticion, diasSemana, intervaloSemanas, diaInicioMes, diaFinMes)
                        OUTPUT INSERTED.id
                        VALUES (
                            :idNombreReporte, :responsable, :fechaInicio, :fechaFin,
                            :tipoRepeticion, :diasSemana, :intervaloSemanas, :diaInicioMes, :diaFinMes)
                    """;

            Query insertQuery = em.createNativeQuery(insertSql);
            insertQuery.setParameter("idNombreReporte", request.getIdNombreReporte());
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

            return idGenerado;
        }
    }

    public void insertarFechas(List<LocalDate> fechas) {
        int batchSize = 50;
        int i = 0;

        for (LocalDate fecha : fechas) {
            String sql = "INSERT INTO xentraFechas values (:fecha)";
            Query query = em.createNativeQuery(sql);
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
}
