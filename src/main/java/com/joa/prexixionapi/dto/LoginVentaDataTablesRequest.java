package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class LoginVentaDataTablesRequest {
    private String search;
    private String periodoI;
    private String periodoF;
    private String estadosString;
    private String categoriaStoreString;
    private String grupoEconomicoString;
    private String gruposString;
    private String responsableString;
    private String registroString;
    private String revisionSunatString;
    private String validacionString;
    private String confirmacionString;
    private String fVencimientoMin;
    private String fVencimientoMax;
    private String sortKey;
    private String sortDir;
}
