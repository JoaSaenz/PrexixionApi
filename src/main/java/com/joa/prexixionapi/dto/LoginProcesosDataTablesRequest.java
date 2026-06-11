package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class LoginProcesosDataTablesRequest {
    private int draw = 1;
    private int start = 0;
    private int length = 10;
    private String search;
    private String periodoI;
    private String periodoF;
    private String estadosString;
    private String categoriasString;
    private String categoriaStoreString;
    private String grupoEconomicoString;
    private String gruposString;
    private String confirmacionVentasString;
    private String confirmacionComprasString;
    private String preLiquidacionString;
    private String confirmacionString;
    private String sireCvString;
    private String pdtString;
    private String fVencimientoMin;
    private String fVencimientoMax;
    private String sortKey;
    private String sortDir;
}
