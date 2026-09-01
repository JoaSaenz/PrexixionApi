package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class CasoSunatDocumentoEventoDTO {
    private Object id;
    private Integer idCaso;
    private Object idDocumento;
    private Integer idEmisor;
    private String descEmisor;
    private Integer idEvento;
    private String descEvento;
    private Object idDocumentoCarta;
    private String nroDocumentoCarta;
    private String fecha;
    private String observacion;
}
