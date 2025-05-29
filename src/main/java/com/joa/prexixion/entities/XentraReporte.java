package com.joa.prexixion.entities;

import lombok.Data;

@Data
public class XentraReporte {
    private int id;
    private int idXentra;
    private String fecha;
    private int idEstado;
    private String fechaEstado;
    private String horaEstado;
    private String estadoLogico;

    //Variabls para List principal
    private String nombreReporte;
    private String responsable;
    private String responsableNombreApellido;
    private String descEstado;
}
