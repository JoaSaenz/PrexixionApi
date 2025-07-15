package com.joa.prexixion.entities;

import lombok.Data;

//@Data
public class Cliente {
    private String ruc;
    private String y;
    private String razonSocial;
    private String nombreCorto;
    private String codigoCliente;

    private Gclass estado;
    private Gclass contribuyente;
    private Gclass servicio;
    private Gclass grupoEconomico;
    private int taxReview;
    private String fEntregaTaxReview;

    private String altaCom;
    private String periodoI621;
    private String periodoF621;
    private String periodoIActualizacion;
    private String periodoFActualizacion;
    private String fInscripcion;
    private String fRetiro;

    private String solU;
    private String solC;
    private String upsU;
    private String upsC;
    private String soldierU;
    private String soldierC;
    private String signerU;
    private String signerC;

    private String ccbCuenta;
    private String ccbUsuario;
    private String ccbClave;

    //Variables para los Niveles(F - X3)
    private SignerNivel signerNivel;

    private int administracion;

    private String periodoInicioCom;

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Gclass getEstado() {
        return estado;
    }

    public void setEstado(Gclass estado) {
        this.estado = estado;
    }

    public Gclass getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Gclass contribuyente) {
        this.contribuyente = contribuyente;
    }

    public Gclass getServicio() {
        return servicio;
    }

    public void setServicio(Gclass servicio) {
        this.servicio = servicio;
    }

    public Gclass getGrupoEconomico() {
        return grupoEconomico;
    }

    public void setGrupoEconomico(Gclass grupoEconomico) {
        this.grupoEconomico = grupoEconomico;
    }

    public int getTaxReview() {
        return taxReview;
    }

    public void setTaxReview(int taxReview) {
        this.taxReview = taxReview;
    }

    public String getfEntregaTaxReview() {
        return fEntregaTaxReview;
    }

    public void setfEntregaTaxReview(String fEntregaTaxReview) {
        this.fEntregaTaxReview = fEntregaTaxReview;
    }

    public String getAltaCom() {
        return altaCom;
    }

    public void setAltaCom(String altaCom) {
        this.altaCom = altaCom;
    }

    public String getPeriodoI621() {
        return periodoI621;
    }

    public void setPeriodoI621(String periodoI621) {
        this.periodoI621 = periodoI621;
    }

    public String getPeriodoF621() {
        return periodoF621;
    }

    public void setPeriodoF621(String periodoF621) {
        this.periodoF621 = periodoF621;
    }

    public String getPeriodoIActualizacion() {
        return periodoIActualizacion;
    }

    public void setPeriodoIActualizacion(String periodoIActualizacion) {
        this.periodoIActualizacion = periodoIActualizacion;
    }

    public String getPeriodoFActualizacion() {
        return periodoFActualizacion;
    }

    public void setPeriodoFActualizacion(String periodoFActualizacion) {
        this.periodoFActualizacion = periodoFActualizacion;
    }

    public String getfInscripcion() {
        return fInscripcion;
    }

    public void setfInscripcion(String fInscripcion) {
        this.fInscripcion = fInscripcion;
    }

    public String getfRetiro() {
        return fRetiro;
    }

    public void setfRetiro(String fRetiro) {
        this.fRetiro = fRetiro;
    }

    public String getSolU() {
        return solU;
    }

    public void setSolU(String solU) {
        this.solU = solU;
    }

    public String getSolC() {
        return solC;
    }

    public void setSolC(String solC) {
        this.solC = solC;
    }

    public String getUpsU() {
        return upsU;
    }

    public void setUpsU(String upsU) {
        this.upsU = upsU;
    }

    public String getUpsC() {
        return upsC;
    }

    public void setUpsC(String upsC) {
        this.upsC = upsC;
    }

    public String getSoldierU() {
        return soldierU;
    }

    public void setSoldierU(String soldierU) {
        this.soldierU = soldierU;
    }

    public String getSoldierC() {
        return soldierC;
    }

    public void setSoldierC(String soldierC) {
        this.soldierC = soldierC;
    }

    public String getSignerU() {
        return signerU;
    }

    public void setSignerU(String signerU) {
        this.signerU = signerU;
    }

    public String getSignerC() {
        return signerC;
    }

    public void setSignerC(String signerC) {
        this.signerC = signerC;
    }

    public String getCcbCuenta() {
        return ccbCuenta;
    }

    public void setCcbCuenta(String ccbCuenta) {
        this.ccbCuenta = ccbCuenta;
    }

    public String getCcbUsuario() {
        return ccbUsuario;
    }

    public void setCcbUsuario(String ccbUsuario) {
        this.ccbUsuario = ccbUsuario;
    }

    public String getCcbClave() {
        return ccbClave;
    }

    public void setCcbClave(String ccbClave) {
        this.ccbClave = ccbClave;
    }

    public SignerNivel getSignerNivel() {
        return signerNivel;
    }

    public void setSignerNivel(SignerNivel signerNivel) {
        this.signerNivel = signerNivel;
    }

    public int getAdministracion() {
        return administracion;
    }

    public void setAdministracion(int administracion) {
        this.administracion = administracion;
    }

    public String getPeriodoInicioCom() {
        return periodoInicioCom;
    }

    public void setPeriodoInicioCom(String periodoInicioCom) {
        this.periodoInicioCom = periodoInicioCom;
    }

    
}
