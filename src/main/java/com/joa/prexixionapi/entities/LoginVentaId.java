package com.joa.prexixionapi.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LoginVentaId implements Serializable {

    @Column(name = "idCliente")
    private String idCliente;

    @Column(name = "anio", length = 4)
    private String anio;

    @Column(name = "mes", length = 2)
    private String mes;

    public LoginVentaId() {}

    public LoginVentaId(String idCliente, String anio, String mes) {
        this.idCliente = idCliente;
        this.anio = anio;
        this.mes = mes;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
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
        LoginVentaId that = (LoginVentaId) o;
        return Objects.equals(idCliente, that.idCliente) &&
               Objects.equals(anio, that.anio) &&
               Objects.equals(mes, that.mes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente, anio, mes);
    }
}
