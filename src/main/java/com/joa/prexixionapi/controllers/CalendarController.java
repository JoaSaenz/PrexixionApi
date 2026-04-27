package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.CalendarEventDTO;
import com.joa.prexixionapi.services.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para desarrollo
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/pdt")
    public List<CalendarEventDTO> getCronogramaPDT() { return calendarService.getCronogramaPDT(); }

    @GetMapping("/sire")
    public List<CalendarEventDTO> getCronogramaSire() { return calendarService.getCronogramaSire(); }

    @GetMapping("/ple")
    public List<CalendarEventDTO> getCronogramaPLE() { return calendarService.getCronogramaPLE(); }

    @GetMapping("/afp")
    public List<CalendarEventDTO> getCronogramaAFP() { return calendarService.getCronogramaAFP(); }

    @GetMapping("/detracciones")
    public List<CalendarEventDTO> getCronogramaDetracciones() { return calendarService.getCronogramaDetracciones(); }

    @GetMapping("/anual")
    public List<CalendarEventDTO> getCronogramaAnual() { return calendarService.getCronogramaAnual(); }

    @GetMapping("/feriados")
    public List<CalendarEventDTO> getFeriados() { return calendarService.getFeriados(); }

    @GetMapping("/misa")
    public List<CalendarEventDTO> getMisa() { return calendarService.getMisa(); }

    @GetMapping("/dias-festivos")
    public List<CalendarEventDTO> getDiasFestivos() { return calendarService.getDiasFestivos(); }

    @GetMapping("/tramites-sunat")
    public List<CalendarEventDTO> getTramitesSunat() { return calendarService.getTramitesSunat(); }

    @GetMapping("/cumpleanos")
    public List<CalendarEventDTO> getCumpleanos(@RequestParam String dni) { return calendarService.getCumpleanos(dni); }

    @GetMapping("/obligaciones")
    public List<CalendarEventDTO> getObligaciones(@RequestParam String dni) { return calendarService.getObligaciones(dni); }

    @GetMapping("/fiscalizaciones-pay")
    public List<CalendarEventDTO> getFiscalizacionesPay(@RequestParam String dni) { return calendarService.getFiscalizacionesPay(dni); }

    @GetMapping("/reuniones")
    public List<CalendarEventDTO> getReuniones() { return calendarService.getReuniones(); }

    @GetMapping("/fiscalizaciones")
    public List<CalendarEventDTO> getFiscalizaciones(@RequestParam String dni) { return calendarService.getFiscalizaciones(dni); }
}
