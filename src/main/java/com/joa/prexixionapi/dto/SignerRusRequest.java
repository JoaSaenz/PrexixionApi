package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class SignerRusRequest {
    private int draw = 1;
    private int start = 0;
    private int length = 10;
    private String search;
    private String nivelesFijosString;
    private String nivelesX3String;
    private String gruposEconomicosString;
    private String estadosString;
    private String gruposString;
    private String sortKey;
    private String sortDir;
}
