package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private String dni;
    private String nombre;
    private String apellido;
    private String fecha;
    private String empresa;
    private String puesto;
    private String area;
    private String tipo; // Fijo, Practicante, etc.
    private String mi; // Mañana Ingreso
    private String ms; // Mañana Salida
    private String ti; // Tarde Ingreso
    private String ts; // Tarde Salida
    private String tarde; // Marcaciones en horario no definido
    private int minutosTardanza;
    private int minutosTrabajados;
    private String estado; // Activo, Baja, etc.
    private Integer idEstado;
    private Integer idTipo;
}
