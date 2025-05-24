package com.joa.prexixion.entities;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class XentraRequest {
    private int id;
    private int idArea;
    private int idSubArea;
    private String nombre;
    private String proceso;
    private String color;
    private String responsable;
    private String fechaInicio;
    private String fechaFin;
    private String tipoRepeticion;
    private List<Integer> diasSemana; // ej. 1 (Lunes), 4 (Jueves)
    private String diasSemanaString;
    private int intervaloSemanas;
    private int diaInicioMes;
    private int diaFinMes;

    // Variables para List principal
    private String descArea;
    private String descSubArea;
    private String responsableNombreApellido;

    //Variables para Calendario XentraFechas (luego crear su propia clase)
    private int idEstado;
    private String fecha;

    // Lista de fechas generadas
    private List<LocalDate> fechas;

}
