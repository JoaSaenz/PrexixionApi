package com.joa.prexixionapi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GclassDTO {
    private int id;
    private String idString;
    private int idTipo;
    private String descripcion;
    private String abreviatura;
    private int marcado;
    
    public GclassDTO(int id) {
        this.id = id;
    }

    public GclassDTO(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public GclassDTO(int id, String abreviatura, String descripcion) {
        this.id = id;
        this.abreviatura = abreviatura;
        this.descripcion = descripcion;
    }
}
