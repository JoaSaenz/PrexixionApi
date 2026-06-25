package com.joa.prexixionapi.entities;

import lombok.Data;

@Data
public class TradeCliente {
    private int codigoCliente;
    private String ruc;
    private String razonSocial;
    private int idContribuyente;
    private String contribuyenteDescripcion;
    private String idsRubro;
    private String rubrosDescripciones;
    private String idsTipoServicio;
    private String serviciosDescripciones;
    private int idEstado;
    private String estadoDescripcion;
    private String fRegistro;
    private String fTermino;
    
    private TradeClienteBudget budget;
    private String tcContactoNombre;
    private String tcContactoTelefono;
    private String tcContactoCorreo;
    
    private String coFecha;
    private String coNumero;
    private String coIdsTipoServicio;
    private double coImporte;
    private int coIdMoneda;
    private int coIdIgv;
    private String coObservacion;
    private int coIdEmpresaCom;
    private String coEmpresaComDescripcion;
    private String reFecha;
    private String reNumero;
    private int reIdTipoServicio;
    private String reTipoServicioDescripcion;
    private int reIdEstadoR;
    private String reEstadoDescripcion;
    private String reObservacion;
    
    private String fAlta;
    private int idEstadoSigner;
    private String descEstadoSigner;
    private String inicioDj;
    private String anioDj;
    private String mesDj;
    private String anioDjAnual;
    private String grupoEconomico;
    private String idEmpresaGcom;
    private String descEmpresaGcom;
    private String inicioSunat;
    private int rt1ra;
    private int rt2da;
    private int rt3ra;
    private int rt4ta;
    private int rt5ta;
    private int rtRus;
    private int rtEspecial;
    private int rtMypeTributario;
    private int rtGeneral;
    private int rtAmazonico;
    private int rtAgrario;
    private int rlMicro;
    private int rlPequenia;
    private int rlGeneral;
    private int rlAgrario;
    private int rlConstruccion;
    private int pleSireCV;
    private int pdt621;
    private int pdtAnual;
    private int pdt601;
    private String solU;
    private String solC;
    private String afpU;
    private String afpC;
    private String sisU;
    private String sisC;
    private int taxReview;
    private int actualizacion;
    private String desdeActualizacion;
    private String anioDesdeActualizacion;
    private String mesDesdeActualizacion;
    private String hastaActualizacion;
    private String anioHastaActualizacion;
    private String mesHastaActualizacion;
    private String observacionSigner;
    
    private String codigoClienteClie; 
    
    private int rlAcreditado;
    private int rlNoAcreditado;

    public String getfRegistro() {
        return fRegistro;
    }

    public void setfRegistro(String fRegistro) {
        this.fRegistro = fRegistro;
    }

    public String getfTermino() {
        return fTermino;
    }

    public void setfTermino(String fTermino) {
        this.fTermino = fTermino;
    }

    public String getfAlta() {
        return fAlta;
    }

    public void setfAlta(String fAlta) {
        this.fAlta = fAlta;
    }
}
