package com.joa.prexixion.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gclass {
    private int id;
    private String idString;
    private int idTipo;
    private String descripcion;
    private String abreviatura;
    private int marcado;
    
    public Gclass(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Gclass(int id, String abreviatura, String descripcion) {
        this.id = id;
        this.abreviatura = abreviatura;
        this.descripcion = descripcion;
    }

    
}
