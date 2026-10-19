package com.joa.prexixionapi.modules.calendar.controllers;

import com.joa.prexixionapi.modules.calendar.dto.CalendarEventDTO;
import com.joa.prexixionapi.modules.calendar.services.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/pdt")
    public ResponseEntity<List<CalendarEventDTO>> getCronogramaPDT() { return ResponseEntity.ok(calendarService.getCronogramaPDT()); }

    @GetMapping("/sire")
    public ResponseEntity<List<CalendarEventDTO>> getCronogramaSire() { return ResponseEntity.ok(calendarService.getCronogramaSire()); }

    @GetMapping("/ple")
    public ResponseEntity<List<CalendarEventDTO>> getCronogramaPLE() { return ResponseEntity.ok(calendarService.getCronogramaPLE()); }

    @GetMapping("/afp")
    public ResponseEntity<List<CalendarEventDTO>> getCronogramaAFP() { return ResponseEntity.ok(calendarService.getCronogramaAFP()); }

    @GetMapping("/detracciones")
    public ResponseEntity<List<CalendarEventDTO>> getCronogramaDetracciones() { return ResponseEntity.ok(calendarService.getCronogramaDetracciones()); }

    @GetMapping("/anual")
    public ResponseEntity<List<CalendarEventDTO>> getCronogramaAnual() { return ResponseEntity.ok(calendarService.getCronogramaAnual()); }

    @GetMapping("/feriados")
    public ResponseEntity<List<CalendarEventDTO>> getFeriados() { return ResponseEntity.ok(calendarService.getFeriados()); }

    @GetMapping("/misa")
    public ResponseEntity<List<CalendarEventDTO>> getMisa() { return ResponseEntity.ok(calendarService.getMisa()); }

    @GetMapping("/dias-festivos")
    public ResponseEntity<List<CalendarEventDTO>> getDiasFestivos() { return ResponseEntity.ok(calendarService.getDiasFestivos()); }

    @GetMapping("/tramites-sunat")
    public ResponseEntity<List<CalendarEventDTO>> getTramitesSunat() { return ResponseEntity.ok(calendarService.getTramitesSunat()); }

    @GetMapping("/cumpleanos")
    public ResponseEntity<List<CalendarEventDTO>> getCumpleanos(@RequestParam String dni) { return ResponseEntity.ok(calendarService.getCumpleanos(dni)); }

    @GetMapping("/obligaciones")
    public ResponseEntity<List<CalendarEventDTO>> getObligaciones(@RequestParam String dni) { return ResponseEntity.ok(calendarService.getObligaciones(dni)); }

    @GetMapping("/fiscalizaciones-pay")
    public ResponseEntity<List<CalendarEventDTO>> getFiscalizacionesPay(@RequestParam String dni) { return ResponseEntity.ok(calendarService.getFiscalizacionesPay(dni)); }

    @GetMapping("/reuniones")
    public ResponseEntity<List<CalendarEventDTO>> getReuniones() { return ResponseEntity.ok(calendarService.getReuniones()); }

    @GetMapping("/fiscalizaciones")
    public ResponseEntity<List<CalendarEventDTO>> getFiscalizaciones(@RequestParam String dni) { return ResponseEntity.ok(calendarService.getFiscalizaciones(dni)); }
}
