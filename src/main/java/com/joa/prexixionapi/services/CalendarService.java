package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.CalendarEventDTO;
import com.joa.prexixionapi.repositories.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public List<CalendarEventDTO> getCronogramaPDT() { return calendarRepository.findCronogramaPDT(); }
    public List<CalendarEventDTO> getCronogramaSire() { return calendarRepository.findCronogramaSire(); }
    public List<CalendarEventDTO> getCronogramaPLE() { return calendarRepository.findCronogramaPLE(); }
    public List<CalendarEventDTO> getCronogramaAFP() { return calendarRepository.findCronogramaAFP(); }
    public List<CalendarEventDTO> getCronogramaDetracciones() { return calendarRepository.findCronogramaDetracciones(); }
    public List<CalendarEventDTO> getCronogramaAnual() { return calendarRepository.findCronogramaAnual(); }
    public List<CalendarEventDTO> getFeriados() { return calendarRepository.findFeriados(); }
    public List<CalendarEventDTO> getMisa() { return calendarRepository.findMisa(); }
    public List<CalendarEventDTO> getDiasFestivos() { return calendarRepository.findDiasFestivos(); }
    public List<CalendarEventDTO> getTramitesSunat() { return calendarRepository.findTramitesSunat(); }
    public List<CalendarEventDTO> getCumpleanos(String dni) { return calendarRepository.findCumpleanos(dni); }
    public List<CalendarEventDTO> getObligaciones(String dni) { return calendarRepository.findObligaciones(dni); }
    public List<CalendarEventDTO> getFiscalizacionesPay(String dni) { return calendarRepository.findFiscalizacionesPay(dni); }
    public List<CalendarEventDTO> getReuniones() { return calendarRepository.findReuniones(); }
    public List<CalendarEventDTO> getFiscalizaciones(String dni) { return calendarRepository.findFiscalizaciones(dni); }
}
