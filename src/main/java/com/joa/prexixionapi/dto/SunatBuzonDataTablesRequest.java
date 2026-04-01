package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class SunatBuzonDataTablesRequest {
    private int draw = 1;
    private int start = 0;
    private int length = 10;
    private String search;
    
    private String fecha;
    private String tieneNotificacionString;
    private String grupoEconomicoString;
    private String estadosString;
    private String gruposString;
    
    private String sortKey;
    private String sortDir;
}
