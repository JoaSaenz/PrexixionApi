package com.joa.prexixionapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CasoSunatListDTO {
    private Integer id;
    private String idEmpresa;
    private String razonSocial;
    private Integer idTipoCaso;
    private String descTipoCaso;
    private Integer idModalidad;
    private String descModalidad;
    private Integer idTributo;
    private String descTributo;
    private Integer idMotivo;
    private String descMotivo;
    private Integer idTipoPeriodo;
    private String descTipoPeriodo;
    private String anioPeriodoInicio;
    private String mesPeriodoInicio;
    private String anioPeriodoFin;
    private String mesPeriodoFin;
    private String periodoTexto;
    private Integer coordinacionTax;
    private Integer coordinacionFir;
    private BigDecimal avance;

    // Campos del último documento registrado
    private Integer ultIdTipoDocumento;
    private String descTipoDocumento;
    private String fechaPresentacion;
    private String hora;
    private BigDecimal importeObservado;
    private Integer idEstado;
    private String descEstado;
}
