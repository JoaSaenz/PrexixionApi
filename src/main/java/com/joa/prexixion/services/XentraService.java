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

    public List<XentraRequest> list(int idPuesto, int idArea) {
        List<XentraRequest> list = xentraRepository.list(idPuesto, idArea);

        // Mapa de equivalencias
        Map<Integer, String> diaEquivalencias = Map.of(
                0, "-",
                1, "LU",
                2, "MA",
                3, "MI",
                4, "JU",
                5, "VI",
                6, "SA",
                7, "DO");

        list.forEach(e -> {
            e.setDiasSemanaString(Arrays.stream(e.getDiasSemanaString().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .map(diaEquivalencias::get)
                    .collect(Collectors.joining(",")));
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
            if (e != 0) {
                diasSemana.add(DayOfWeek.of(e));
            }
        });

        int intervaloSemanas = 1;
        ConfiguracionMensual configMensual = new ConfiguracionMensual(request.getDiaInicioMes(),
                request.getDiaFinMes());

        Set<LocalDate> feriados = xentraRepository.obtenerFeriados();

        // Generamos las fechas y las asignamos al objeto request
        List<LocalDate> fechas = generarFechasMasivas(inicio, tipo, intervaloSemanas, fin, diasSemana, configMensual,
                feriados);
        request.setFechas(fechas);

        int id = xentraRepository.insertarDataGeneral(request);

        return id;
    }

    public static List<LocalDate> generarFechasMasivas(
            LocalDate inicio,
            TipoRepeticion tipo,
            int intervaloSemanas,
            LocalDate hasta,
            Set<DayOfWeek> diasSemana,
            ConfiguracionMensual configMensual,
            Set<LocalDate> feriados) {

        List<LocalDate> fechas = new ArrayList<>();

        switch (tipo) {
            case SEMANAL:
                // Encontrar el lunes de la semana del inicio (puedes cambiarlo si tu semana
                // comienza en otro día)
                LocalDate semanaCursor = inicio.with(DayOfWeek.MONDAY);

                while (!semanaCursor.isAfter(hasta)) {
                    for (DayOfWeek d : diasSemana) {
                        LocalDate posible = semanaCursor.with(d);

                        // Retroceder si es domingo o feriado, sin límite de semana
                        while (posible.getDayOfWeek() == DayOfWeek.SUNDAY || feriados.contains(posible)) {
                            posible = posible.minusDays(1);
                        }

                        if (!posible.isBefore(inicio) && !posible.isAfter(hasta)
                                && posible.getDayOfWeek() != DayOfWeek.SUNDAY
                                && !feriados.contains(posible)) {
                            fechas.add(posible);
                        }
                    }
                    semanaCursor = semanaCursor.plusWeeks(intervaloSemanas);
                }
                break;

            case DIARIA:
                LocalDate actual = inicio;
                while (!actual.isAfter(hasta)) {
                    if (actual.getDayOfWeek() != DayOfWeek.SUNDAY && !feriados.contains(actual)) { // OMITIR DOMINGO
                        fechas.add(actual);
                    }
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

                        int diaInicio = configMensual.getDiaInicio();
                        int diaFin = configMensual.getDiaFin();

                        for (int d = diaInicio; d <= diaFin; d++) {
                            try {
                                LocalDate fecha = LocalDate.of(y, m, d);
                                if (!fecha.isBefore(inicio) && !fecha.isAfter(hasta)) {

                                    // Si es un único día y cae en domingo o feriado, retroceder
                                    if (diaInicio == diaFin
                                            && (fecha.getDayOfWeek() == DayOfWeek.SUNDAY || feriados.contains(fecha))) {
                                        do {
                                            fecha = fecha.minusDays(1);
                                        } while ((fecha.getDayOfWeek() == DayOfWeek.SUNDAY || feriados.contains(fecha))
                                                && !fecha.isBefore(inicio));
                                    }

                                    // Para rangos, simplemente omitir domingos y feriados
                                    if (fecha.getDayOfWeek() != DayOfWeek.SUNDAY && !feriados.contains(fecha)) {
                                        fechas.add(fecha);
                                    }
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
                        LocalDate posible = d;

                        if (posible.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            posible = posible.minusDays(1); // mover al sábado
                        }

                        if (posible.getDayOfWeek() != DayOfWeek.SUNDAY) {
                            fechas.add(posible);
                        }
                        d = d.plusMonths(1);
                    }
                }
                break;
            }

            case TRIMESTRAL:
                actual = inicio;
                while (!actual.isAfter(hasta)) {
                    if (actual.getDayOfWeek() != DayOfWeek.SUNDAY) {
                        fechas.add(actual);
                    } else {
                        fechas.add(actual.minusDays(1)); // mover al sábado
                    }
                    actual = actual.plusMonths(3);
                }
                break;

            case ANUAL:
                actual = inicio;
                while (!actual.isAfter(hasta)) {
                    LocalDate posible = actual;

                    // Retroceder si cae en domingo o feriado, hasta encontrar día hábil
                    while ((posible.getDayOfWeek() == DayOfWeek.SUNDAY || feriados.contains(posible))
                            && !posible.isBefore(inicio)) {
                        posible = posible.minusDays(1);
                    }

                    // Agregar solo si no es domingo ni feriado (por seguridad extra)
                    if (posible.getDayOfWeek() != DayOfWeek.SUNDAY && !feriados.contains(posible)) {
                        fechas.add(posible);
                    }

                    // Avanzar un año desde la fecha original (no desde `posible`)
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

    public List<XentraRequest> getListXentraFechas(int idPuesto, int idArea, int idSubArea, String dni) {
        return xentraRepository.getListXentraFechas(idPuesto, idArea, idSubArea, dni);
    }

    public int delete(int id) {
        return xentraRepository.delete(id);
    }
}
