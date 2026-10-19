package com.joa.prexixionapi.modules.pdt621.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdt621RegistroDTO {
    private String nroRectificacion;
    private String fecha;
    private String nroOrden;
    private Integer tipo;
}
