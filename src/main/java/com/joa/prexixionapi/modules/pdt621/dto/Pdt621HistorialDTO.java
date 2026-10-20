package com.joa.prexixionapi.modules.pdt621.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdt621HistorialDTO {
    private String idCliente;
    private String ruc;
    private String anio;
    private String mes;
    private Integer registrado;
    private String fechaVencimiento;
    private String fechaDeclaracion;
    private String nroOrden;
}
