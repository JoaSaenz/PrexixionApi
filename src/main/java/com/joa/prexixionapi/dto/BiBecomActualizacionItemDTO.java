package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiBecomActualizacionItemDTO {
    private Integer idGrupoEconomico;
    private String grupoEmpresarial;
    private String ruc;
    private String razonSocial;
    private Integer idEstadoCliente;
    private String estadoCliente;
    private Integer idEstado;
    private String estado;
    private String periodoInicio;
    private String periodoFinal;
    private String fechaInicio;
    private String fechaTermino;
    private String observacion;
}
