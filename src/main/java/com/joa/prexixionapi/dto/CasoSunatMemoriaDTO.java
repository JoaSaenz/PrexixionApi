package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasoSunatMemoriaDTO {
    private Integer idCaso;
    private String idEmpresa;
    private String razonSocial;
    private Integer idDocumento;
    private Integer idTipoDocumento;
    private String descTipoDocumento;
    private String nroDocumentoReq;
    private Integer idEvento;
    private Integer idEmisor;
    private String descEmisor;
    private Integer idTipoEvento;
    private String descEvento;
    private Integer idDocumentoCarta;
    private String nroDocumentoCarta;
    private String fecha;
    private String observacion;
}
