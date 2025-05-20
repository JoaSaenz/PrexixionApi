package com.joa.prexixion.entities;

public class ConfiguracionMensual {
    private int diaInicio;
    private int diaFin;

    public ConfiguracionMensual(int diaInicio, int diaFin) {
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
