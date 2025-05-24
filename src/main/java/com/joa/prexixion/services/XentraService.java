package com.joa.prexixion.services;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixion.entities.ConfiguracionMensual;
import com.joa.prexixion.entities.XentraRequest;
import com.joa.prexixion.enums.TipoRepeticion;
import com.joa.prexixion.repositories.XentraRepository;

@Service
public class XentraService {

    @Autowired
    XentraRepository xentraRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<XentraRequest> list() {
        List<XentraRequest> list = xentraRepository.list();

        // Mapa de equivalencias
        Map<Integer, String> diaEquivalencias = Map.of(
            0, "-",
            1, "LU",
            2, "MA",
            3, "MI",
            4, "JU",
            5, "VI",
            6, "SA",
            7, "DO"
        );

        list.forEach(e -> {
            System.out.println(e.getDiasSemanaString());

            e.setDiasSemanaString(Arrays.stream(e.getDiasSemanaString().split(","))
                                .map(String::trim)
                                .map(Integer::parseInt)
                                .map(diaEquivalencias::get)
                                .collect(Collectors.joining(","))); 
            
            System.out.println(e.getDiasSemanaString());
        });

       

        return list;
    }

    public int guardarFechas(XentraRequest request) {
        int rpta = 0;

        // Adaptar request
        LocalDate inicio = LocalDate.parse(request.getFechaInicio(), formatter);
        LocalDate fin = LocalDate.parse(request.getFechaFin(), formatter);
        TipoRepeticion tipo = TipoRepeticion.valueOf(request.getTipoRepeticion());
        Set<DayOfWeek> diasSemana = new HashSet<DayOfWeek>();
        request.getDiasSemana().forEach(e -> {
            diasSemana.add(DayOfWeek.of(e));
        });
        int intervaloSemanas = 1;
        ConfiguracionMensual configMensual = new ConfiguracionMensual(request.getDiaInicioMes(),
                request.getDiaFinMes());

        // Generamos las fechas
        List<LocalDate> fechas = generarFechasMasivas(inicio, tipo, intervaloSemanas, fin, diasSemana, configMensual);

        // Guardamos la data general por el repository

        System.out.println(request.toString());
        int id = xentraRepository.insertarDataGeneral(request);

        // Guardamos las fechas por el repository
        // rpta = xentraRepository.insertarFechas(fechas);

        return id;
    }

    public static List<LocalDate> generarFechasMasivas(
            LocalDate inicio,
            TipoRepeticion tipo,
            int intervaloSemanas,
            LocalDate hasta,
            Set<DayOfWeek> diasSemana,
            ConfiguracionMensual configMensual) {

        List<LocalDate> fechas = new ArrayList<>();

        switch (tipo) {
            case SEMANAL:
                // Encontrar el lunes de la semana del inicio (puedes cambiarlo si tu semana
                // comienza en otro día)
                LocalDate semanaCursor = inicio.with(DayOfWeek.MONDAY);

                while (!semanaCursor.isAfter(hasta)) {
                    for (DayOfWeek d : diasSemana) {
                        LocalDate posible = semanaCursor.with(d);
                        if (!posible.isBefore(inicio) && !posible.isAfter(hasta)) {
                            fechas.add(posible);
                        }
                    }
                    semanaCursor = semanaCursor.plusWeeks(intervaloSemanas);
                }
                break;

            case DIARIA:
                LocalDate actual = inicio;
                while (!actual.isAfter(hasta)) {
                    fechas.add(actual);
                    actual = actual.plusDays(1);
                }
                break;

            case MENSUAL: {
                if (configMensual != null) {
                    // Caso “rango de días”: p. ej. 5-15 de cada mes
                    LocalDate mes = inicio.withDayOfMonth(1);
                    while (!mes.isAfter(hasta)) {
                        int y = mes.getYear();
                        int m = mes.getMonthValue();
                        for (int d = configMensual.getDiaInicio(); d <= configMensual.getDiaFin(); d++) {
                            try {
                                LocalDate fecha = LocalDate.of(y, m, d);
                                if (!fecha.isBefore(inicio) && !fecha.isAfter(hasta)) {
                                    fechas.add(fecha);
                                }
                            } catch (DateTimeException e) {
                                /* día inexistente, ignorar */ }
                        }
                        mes = mes.plusMonths(1);
                    }
                } else {
                    // Caso clásico: mismo día del mes que ‘inicio’
                    LocalDate d = inicio;
                    while (!d.isAfter(hasta)) {
                        fechas.add(d);
                        d = d.plusMonths(1);
                    }
                }
                break;
            }

            case TRIMESTRAL:
                actual = inicio;
                while (!actual.isAfter(hasta)) {
                    fechas.add(actual);
                    actual = actual.plusMonths(3);
                }
                break;

            case ANUAL:
                actual = inicio;
                while (!actual.isAfter(hasta)) {
                    fechas.add(actual);
                    actual = actual.plusYears(1);
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de repetición no soportado.");
        }

        Collections.sort(fechas);
        return fechas;
    }

    public XentraRequest getOne(int id) {
        return xentraRepository.getOne(id);
    }

    public List<XentraRequest> getListXentraFechas() {
        return xentraRepository.getListXentraFechas();
    }
}
