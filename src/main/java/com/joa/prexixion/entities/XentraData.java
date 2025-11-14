package com.joa.prexixion.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "xentraData")
public class XentraData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id autoincremental
    private Integer id;

    private Integer idArea;
    private Integer idSubArea;

    private String abreviatura;
    private String nombre;
    private String color;
    private String responsable;

    private String fechaInicio; // varchar(10) → puedes migrar a LocalDate si en BD cambias a DATE
    private String fechaFin;

    private String tipoRepeticion;
    private String diasSemana; // varchar(100)
    private Integer intervaloSemanas;
    private String mesesPermitidos; // varchar(100)
    private Integer diaInicioMes;
    private Integer diaFinMes;

    private String estado;

    // Relación con XentraFechas
    @OneToMany(mappedBy = "xentra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<XentraFecha> fechas;
}