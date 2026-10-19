package com.joa.prexixionapi.modules.jobSunat.dto;

import lombok.Data;

@Data
public class SunatBuzonDataTablesRequest {
    private String search;
    
    private String fecha;
    private String tieneNotificacionString;
    private String tieneAdjuntoString;
    private String grupoEconomicoString;
    private String estadosString;
    private String gruposString;
    
    private String sortKey;
    private String sortDir;
}
