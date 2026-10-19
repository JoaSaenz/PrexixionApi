package com.joa.prexixionapi.modules.casoSunat.dto;

import lombok.Data;

@Data
public class CasoSunatDocumentoRelacionDTO {
    private Object id;
    private Integer idCaso;
    private Object idDocumentoOrigen;
    private String descDocumentoOrigen;
    private Integer idTipoRelacion;
    private String descTipoRelacion;
    private Object idDocumentoDestino;
    private String descDocumentoDestino;
}
