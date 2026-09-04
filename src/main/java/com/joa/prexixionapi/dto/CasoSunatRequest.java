package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class CasoSunatRequest {
    private String idEmpresa;
    private String tiposCasoString;
    private String documentosString;
    private String modalidadesString;
    private String tributosString;
    private String tiposPeriodoString;
    private String periodoTexto;
    private String estadosString;
}
