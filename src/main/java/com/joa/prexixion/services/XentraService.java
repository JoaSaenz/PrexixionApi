package com.joa.prexixion.services;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.joa.prexixion.dto.XentraDataDTO;
import com.joa.prexixion.entities.ConfiguracionMensual;
import com.joa.prexixion.entities.XentraData;
import com.joa.prexixion.enums.TipoRepeticion;
import com.joa.prexixion.mappers.XentraMapper;
import com.joa.prexixion.repositories.XentraDataRepository;
import com.joa.prexixion.repositories.XentraRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class XentraService {

    private final XentraRepository xentraRepository;
    private final XentraDataRepository xentraDataRepository;
    private final XentraMapper mapper; // inyectado por Spring

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<XentraDataDTO> list(int idPuesto, int idArea) {
        List<XentraDataDTO> list = xentraRepository.list(idPuesto, idArea);

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
        Map<Integer, String> mesEquivalencias = Map.ofEntries(
                Map.entry(0, "-"),
                Map.entry(1, "ENE"),
                Map.entry(2, "FEB"),
                Map.entry(3, "MAR"),
                Map.entry(4, "ABR"),
                Map.entry(5, "MAY"),
                Map.entry(6, "JUN"),
                Map.entry(7, "JUL"),
                Map.entry(8, "AGO"),
                Map.entry(9, "SEP"),
                Map.entry(10, "OCT"),
                Map.entry(11, "NOV"),
                Map.entry(12, "DIC"));

        list.forEach(e -> {
            e.setDiasSemanaString(Arrays.stream(e.getDiasSemanaString().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .map(diaEquivalencias::get)
                    .collect(Collectors.joining(",")));
        });
        list.forEach(e -> {
            e.setMesesPermitidosString(Arrays.stream(e.getMesesPermitidosString().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .map(mesEquivalencias::get)
                    .collect(Collectors.joining(",")));
        });

        return list;
    }

    public int editarXentra(int id, String fechaFinFront) {
        int rpta = 0;
        XentraDataDTO xentra = xentraRepository.getOne(id);

        // Adaptar xentra DB
        LocalDate inicio = LocalDate.parse(xentra.getFechaInicio(), formatter);
        LocalDate fin = LocalDate.parse(fechaFinFront, formatter);
        TipoRepeticion tipo = TipoRepeticion.valueOf(xentra.getTipoRepeticion());

        Set<DayOfWeek> diasSemana = Arrays.stream(xentra.getDiasSemanaString().split(","))
                .map(String::trim) // eliminar espacios
                .map(Integer::parseInt) // convertir a int
                .filter(e -> e != 0) // descartar ceros
                .map(DayOfWeek::of) // convertir al enum DayOfWeek
                .collect(Collectors.toSet());

        int intervaloSemanas = 1;

        Set<Month> mesesPermitidos = Arrays.stream(xentra.getMesesPermitidosString().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .filter(e -> e != 0)
                .map(Month::of)
                .collect(Collectors.toSet());

        ConfiguracionMensual configMensual = new ConfiguracionMensual(xentra.getDiaInicioMes(), xentra.getDiaFinMes());

        Set<LocalDate> feriados = xentraRepository.obtenerFeriados();

        // Generamos las fechas y las asignamos al objeto request
        List<LocalDate> fechas = generarFechas(inicio, tipo, intervaloSemanas, fin, diasSemana, mesesPermitidos,
                configMensual,
                feriados);
        xentra.setFechas(fechas);

        rpta = xentraRepository.editarXentraDB(xentra, fechaFinFront);

        return rpta;
    }

    public int insertarXentra(XentraDataDTO xentraDTO) {
        // Adaptar xentra
        LocalDate inicio = LocalDate.parse(xentraDTO.getFechaInicio(), formatter);
        LocalDate fin = LocalDate.parse(xentraDTO.getFechaFin(), formatter);
        TipoRepeticion tipo = TipoRepeticion.valueOf(xentraDTO.getTipoRepeticion());

        Set<DayOfWeek> diasSemana = new HashSet<DayOfWeek>();
        if (xentraDTO.getDiasSemana() != null) {
            xentraDTO.getDiasSemana().forEach(e -> {
                if (e != 0) {
                    diasSemana.add(DayOfWeek.of(e));
                }
            });
        }

        String diasSemanaString = xentraDTO.getDiasSemana().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        xentraDTO.setDiasSemanaString(diasSemanaString);

        int intervaloSemanas = 1;

        Set<Month> mesesPermitidos = new HashSet<Month>();
        if (xentraDTO.getMesesPermitidos() != null) {
            xentraDTO.getMesesPermitidos().forEach(e -> {
                if (e != 0) {
                    mesesPermitidos.add(Month.of(e));
                }
            });
        }

        String mesesPermitidosString = xentraDTO.getMesesPermitidos().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        xentraDTO.setMesesPermitidosString(mesesPermitidosString);

        ConfiguracionMensual configMensual = new ConfiguracionMensual(xentraDTO.getDiaInicioMes(),
                xentraDTO.getDiaFinMes());

        Set<LocalDate> feriados = xentraRepository.obtenerFeriados();

        // Generamos las fechas y las asignamos al objeto request
        List<LocalDate> fechas = generarFechas(inicio, tipo, intervaloSemanas, fin, diasSemana, mesesPermitidos,
                configMensual,
                feriados);
        xentraDTO.setFechas(fechas);

        // convertir a Entity
        XentraData xentraData = mapper.toEntity(xentraDTO);

        // 🔥 Asignar el padre a cada fecha (OBLIGATORIO)
        if (xentraData.getFechas() != null) {
            xentraData.getFechas().forEach(f -> {
                f.setXentra(xentraData);
                f.setIdEstado(1);
                f.setEstadoLogico("PENDIENTE");
            });
        }

        XentraData guardado = xentraDataRepository.save(xentraData);

        return guardado.getId();
    }

    public static List<LocalDate> generarFechas(
            LocalDate inicio,
            TipoRepeticion tipo,
            int intervaloSemanas,
            LocalDate hasta,
            Set<DayOfWeek> diasSemana,
            Set<Month> mesesPermitidos,
            ConfiguracionMensual configMensual,
            Set<LocalDate> feriados) {

        List<LocalDate> fechas = new ArrayList<>();

        // Set para evitar duplicados (por retrocesos en días consecutivos)
        Set<LocalDate> fechasAsignadas = new HashSet<>();

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
                                && !fechasAsignadas.contains(posible)) {
                            fechas.add(posible);
                            fechasAsignadas.add(posible);
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
                LocalDate mes = inicio.withDayOfMonth(1);
                while (!mes.isAfter(hasta)) {
                    if (mesesPermitidos != null && !mesesPermitidos.contains(mes.getMonth())) {
                        mes = mes.plusMonths(1);
                        continue;
                    }

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
                            // día inexistente, ignorar
                        }
                    }
                    mes = mes.plusMonths(1);
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

    public XentraDataDTO getOne(int id) {
        return xentraRepository.getOne(id);
    }

    public List<XentraDataDTO> getListXentraFechas(int idPuesto, int idArea, int idSubArea, String dni) {
        return xentraRepository.getListXentraFechas(idPuesto, idArea, idSubArea, dni);
    }

    public int delete(int id) {
        return xentraRepository.delete(id);
    }
}
