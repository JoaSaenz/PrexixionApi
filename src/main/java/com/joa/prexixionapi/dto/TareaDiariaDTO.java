package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TareaDiariaDTO {
    private Long id;
    private String empresa;
    private String proceso;
    private String detalle;
    private Double porcentajeAvance;
    private String estado;
    private Integer idEstado;
    private String responsable;
    private String fechaInicio;
    private String fechaFin;
}
