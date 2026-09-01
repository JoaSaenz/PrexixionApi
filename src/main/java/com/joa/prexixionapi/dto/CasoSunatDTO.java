package com.joa.prexixionapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CasoSunatDTO {
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
    
    private List<CasoSunatAuditorDTO> auditores;
    private List<CasoSunatDocumentoDTO> documentos;
    private List<CasoSunatDocumentoRelacionDTO> relaciones;
}
