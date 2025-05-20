package com.joa.prexixion.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.XentraRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class XentraRepository {

    @PersistenceContext
    private EntityManager em;

    public int insertarDataGeneral(XentraRequest request) {
        int rpta = 0;

        String diasSemana = request.getDiasSemana().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = """
                insert into xentraData (idNombreReporte, responsable, fechaInicio, fechaFin,
                tipoRepeticion, diasSemana, intervaloSemanas, diaInicioMes, diaFinMes)
                values (:idNombreReporte,
                :responsable,
                :fechaInicio,
                :fechaFin,
                :tipoRepeticion,
                :diasSemana,
                :intervaloSemanas,
                :diaInicioMes,
                :diaFinMes)
                """;

        Query query = em.createNativeQuery(sql);
        query.setParameter("idNombreReporte", request.getIdNombreReporte());
        query.setParameter("responsable", request.getResponsable());
        query.setParameter("fechaInicio", request.getFechaInicio());
        query.setParameter("fechaFin", request.getFechaFin());
        query.setParameter("tipoRepeticion", request.getTipoRepeticion());
        query.setParameter("diasSemana", diasSemana);
        query.setParameter("intervaloSemanas", request.getIntervaloSemanas());
        query.setParameter("diaInicioMes", request.getDiaInicioMes());
        query.setParameter("diaFinMes", request.getDiaFinMes());

        query.executeUpdate();

        return rpta;
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
