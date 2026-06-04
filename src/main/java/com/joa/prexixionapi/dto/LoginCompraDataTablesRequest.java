package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class LoginCompraDataTablesRequest {
    private int draw = 1;
    private int start = 0;
    private int length = 10;
    private String search;
    private String periodoI;
    private String periodoF;
    private String estadosString;
    private String categoriaStoreString;
    private String grupoEconomicoString;
    private String gruposString;
    private String ventasConfirmacionString;
    private String sireString;
    private String validacionSunatString;
    private String validacionString;
    private String confirmacionString;
    private String fVencimientoMin;
    private String fVencimientoMax;
    private String sortKey;
    private String sortDir;
}
