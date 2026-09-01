package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class CasoSunatAuditorDTO {
    private Object id;
    private Integer idCaso;
    private String nombresApellidos;
    private String fechaInicio;
    private String fechaFin;
}
