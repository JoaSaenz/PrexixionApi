package com.joa.prexixionapi.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LoginProcesosId implements Serializable {

    @Column(name = "ruc")
    private String ruc;

    @Column(name = "anio", length = 4)
    private String anio;

    @Column(name = "mes", length = 2)
    private String mes;

    public LoginProcesosId() {}

    public LoginProcesosId(String ruc, String anio, String mes) {
        this.ruc = ruc;
        this.anio = anio;
        this.mes = mes;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginProcesosId that = (LoginProcesosId) o;
        return Objects.equals(ruc, that.ruc) &&
               Objects.equals(anio, that.anio) &&
               Objects.equals(mes, that.mes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruc, anio, mes);
    }
}
