package com.joa.prexixion.entities;

import java.util.List;

import lombok.Data;

@Data
public class XentraRequest {
    private Integer idNombreReporte;
    private String responsable;
    private String fechaInicio;
    private String fechaFin;
    private String tipoRepeticion;
    private List<Integer> diasSemana; // ej. 1 (Lunes), 4 (Jueves)
    private Integer intervaloSemanas;
    private Integer diaInicioMes;
    private Integer diaFinMes;
}
