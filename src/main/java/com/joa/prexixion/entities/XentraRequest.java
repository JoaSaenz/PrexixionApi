package com.joa.prexixion.entities;

import java.util.List;

import lombok.Data;

@Data
public class XentraRequest {
    private int id;
    private int idNombreReporte;
    private String responsable;
    private String fechaInicio;
    private String fechaFin;
    private String tipoRepeticion;
    private List<Integer> diasSemana; // ej. 1 (Lunes), 4 (Jueves)
    private int intervaloSemanas;
    private int diaInicioMes;
    private int diaFinMes;
}
