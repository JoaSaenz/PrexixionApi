package com.joa.prexixionapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacacionProgramadaDTO {
    private Integer id;
    private String dni;
    private String colaborador;
    private Integer idArea;
    private String area;
    private Integer idSubArea;
    private String subArea;
    private String fechaInicio;
    private String fechaFin;
    private Integer dias;
    private Integer idEstado; // 1: PROGRAMADA, 2: REPROGRAMADA, 3: ANULADA
    private String estado;   // Descripción textual (ej: "PROGRAMADA", "REPROGRAMADA")
    private String observacion;

    // Propiedades mapeadas para FullCalendar
    private String title;
    private String start;
    private String end;
    private String color;
    private String textColor;
    private Boolean allDay;
}
