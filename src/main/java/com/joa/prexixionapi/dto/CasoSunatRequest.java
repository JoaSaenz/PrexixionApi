package com.joa.prexixionapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CasoSunatRequest {
    private String idEmpresa;
    private Integer idTipoCaso;
    private Integer idModalidad;
    private Integer idTributo;
    private Integer idTipoPeriodo;
    private String periodoTexto;
    private BigDecimal avance;
}
