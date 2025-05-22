package com.joa.prexixion.entities;

import java.util.List;

import lombok.Data;

@Data
public class XentraRequest {
    private int id;
    private int idArea;
    private int idSubArea;
    private String estiloReporte;
    private String nombre;
    private String proceso;
    private String responsable;
    private String fechaInicio;
    private String fechaFin;
    private String tipoRepeticion;
    private List<Integer> diasSemana; // ej. 1 (Lunes), 4 (Jueves)
    private String diasSemanaString;
    private int intervaloSemanas;
    private int diaInicioMes;
    private int diaFinMes;

    //Variables para List principal
    private String descArea;
    private String descSubArea;
    private String responsableNombre;
    private String responsableApellido;

}
