package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class TradeClienteDataTablesRequest {
    private String search;
    private String periodoI;
    private String periodoF;
    private String estadosString;
    private String respuestasString;
    private String serviciosString;
    private String sortKey;
    private String sortDir;
}
