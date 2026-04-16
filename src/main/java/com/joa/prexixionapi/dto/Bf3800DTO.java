package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bf3800DTO {
    private String idEstado;
    private String estado;
    private String idCliente;
    private String y;
    private String razonSocial;
    private String regimenTributario;
    private String anio;
    private String mes;
    private String direccion;

    private String fVencimiento;
    private String mesVencimiento;
    private String diaVencimiento;
    private Integer difference;

    private String ple;
    private String prico;
    private String observacion;
    private Integer mail;

    private String fInicio;
    private String fFin;
    private Boolean existeConstancia;

    private Integer idTipoServicio;
    private String tipoServicio;
    private String tipoServicioAbr;

    @Builder.Default
    private List<Bf3800RegistroDTO> registros = new ArrayList<>();
    @Builder.Default
    private List<BeneficiarioDTO> beneficiarios = new ArrayList<>();
}
