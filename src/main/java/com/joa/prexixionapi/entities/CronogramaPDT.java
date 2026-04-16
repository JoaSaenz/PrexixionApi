package com.joa.prexixionapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

class CronogramaId implements Serializable {
    private String anio;
    private String mes;
}

@Entity
@Table(name = "CRONOGRAMAPDT")
@IdClass(CronogramaId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronogramaPDT {
    @Id
    private String anio;
    @Id
    private String mes;

    private String fecha0;
    private String fecha1;
    private String fecha2;
    private String fecha3;
    private String fecha4;
    private String fecha5;
    private String fecha6;
    private String fecha7;
    private String fecha8;
    private String fecha9;
    private String fechab;
}
