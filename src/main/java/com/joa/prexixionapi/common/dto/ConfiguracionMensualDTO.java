package com.joa.prexixionapi.common.dto;

public class ConfiguracionMensualDTO {
    private int diaInicio;
    private int diaFin;

    public ConfiguracionMensualDTO(int diaInicio, int diaFin) {
        this.diaInicio = diaInicio;
        this.diaFin = diaFin;
    }

    public int getDiaInicio() {
        return diaInicio;
    }

    public int getDiaFin() {
        return diaFin;
    }
}
