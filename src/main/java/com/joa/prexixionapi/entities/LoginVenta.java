package com.joa.prexixionapi.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.Column;

@Entity
@Table(name = "loginVentas")
public class LoginVenta {

    @jakarta.persistence.Transient
    private String prioridad;

    @jakarta.persistence.Transient
    private String ple;

    @EmbeddedId
    private LoginVentaId id;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "registro")
    private Integer registro;

    @Column(name = "registroUsuario")
    private String registroUsuario;

    @Column(name = "registroFecha")
    private String registroFecha;

    @Column(name = "registroHora")
    private String registroHora;

    @Column(name = "revisionSunat")
    private Integer revisionSunat;

    @Column(name = "revisionSunatUsuario")
    private String revisionSunatUsuario;

    @Column(name = "revisionSunatFecha")
    private String revisionSunatFecha;

    @Column(name = "validacion")
    private Integer validacion;

    @Column(name = "validacionUsuario")
    private String validacionUsuario;

    @Column(name = "validacionFecha")
    private String validacionFecha;

    @Column(name = "validacionHora")
    private String validacionHora;

    @Column(name = "confirmacion")
    private Integer confirmacion;

    @Column(name = "confirmacionUsuario")
    private String confirmacionUsuario;

    @Column(name = "confirmacionFecha")
    private String confirmacionFecha;

    @Column(name = "confirmacionHora")
    private String confirmacionHora;

    @Column(name = "seeVentas")
    private Integer seeVentas;

    @Column(name = "extVentas")
    private Integer extVentas;

    @Column(name = "observacion")
    private String observacion;

    @Version
    @Column(name = "version")
    private Integer version;

    @jakarta.persistence.Transient
    private String fVencimiento;
    
    @jakarta.persistence.Transient
    private String diaVencimiento;
    
    @jakarta.persistence.Transient
    private String mesVencimiento;
    
    @jakarta.persistence.Transient
    private Integer difference;

    @jakarta.persistence.Transient
    private String descResponsable;

    @jakarta.persistence.Transient
    private String movimiento;

    @jakarta.persistence.Transient
    private Integer comprasFilas;

    @jakarta.persistence.Transient
    private String modalidad;

    @jakarta.persistence.Transient
    private String anio;

    @jakarta.persistence.Transient
    private String mes;

    

    public LoginVenta() {
}

    public LoginVentaId getId() {
        return id;
    
}

    public void setId(LoginVentaId id) {
        this.id = id;
    
}

    public String getResponsable() {
        return responsable;
    
}

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    
}

    public Integer getRegistro() {
        return registro;
    
}

    public void setRegistro(Integer registro) {
        this.registro = registro;
    
}

    public String getRegistroUsuario() {
        return registroUsuario;
    
}

    public void setRegistroUsuario(String registroUsuario) {
        this.registroUsuario = registroUsuario;
    
}

    public String getRegistroFecha() {
        return registroFecha;
    
}

    public void setRegistroFecha(String registroFecha) {
        this.registroFecha = registroFecha;
    
}

    public String getRegistroHora() {
        return registroHora;
    
}

    public void setRegistroHora(String registroHora) {
        this.registroHora = registroHora;
    
}

    public Integer getRevisionSunat() {
        return revisionSunat;
    
}

    public void setRevisionSunat(Integer revisionSunat) {
        this.revisionSunat = revisionSunat;
    
}

    public String getRevisionSunatUsuario() {
        return revisionSunatUsuario;
    
}

    public void setRevisionSunatUsuario(String revisionSunatUsuario) {
        this.revisionSunatUsuario = revisionSunatUsuario;
    
}

    public String getRevisionSunatFecha() {
        return revisionSunatFecha;
    
}

    public void setRevisionSunatFecha(String revisionSunatFecha) {
        this.revisionSunatFecha = revisionSunatFecha;
    
}

    public Integer getValidacion() {
        return validacion;
    
}

    public void setValidacion(Integer validacion) {
        this.validacion = validacion;
    
}

    public String getValidacionUsuario() {
        return validacionUsuario;
    
}

    public void setValidacionUsuario(String validacionUsuario) {
        this.validacionUsuario = validacionUsuario;
    
}

    public String getValidacionFecha() {
        return validacionFecha;
    
}

    public void setValidacionFecha(String validacionFecha) {
        this.validacionFecha = validacionFecha;
    
}

    public String getValidacionHora() {
        return validacionHora;
    
}

    public void setValidacionHora(String validacionHora) {
        this.validacionHora = validacionHora;
    
}

    public Integer getConfirmacion() {
        return confirmacion;
    
}

    public void setConfirmacion(Integer confirmacion) {
        this.confirmacion = confirmacion;
    
}

    public String getConfirmacionUsuario() {
        return confirmacionUsuario;
    
}

    public void setConfirmacionUsuario(String confirmacionUsuario) {
        this.confirmacionUsuario = confirmacionUsuario;
    
}

    public String getConfirmacionFecha() {
        return confirmacionFecha;
    
}

    public void setConfirmacionFecha(String confirmacionFecha) {
        this.confirmacionFecha = confirmacionFecha;
    
}

    public String getConfirmacionHora() {
        return confirmacionHora;
    
}

    public void setConfirmacionHora(String confirmacionHora) {
        this.confirmacionHora = confirmacionHora;
    
}

    public Integer getSeeVentas() {
        return seeVentas;
    
}

    public void setSeeVentas(Integer seeVentas) {
        this.seeVentas = seeVentas;
    
}

    public Integer getExtVentas() {
        return extVentas;
    
}

    public void setExtVentas(Integer extVentas) {
        this.extVentas = extVentas;
    
}

    public String getObservacion() {
        return observacion;
    
}

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    
}

    public Integer getVersion() {
        return version;
    
}

    public void setVersion(Integer version) {
        this.version = version;
    
}

    public String getfVencimiento() {
        return fVencimiento;
    
}

    public void setfVencimiento(String fVencimiento) {
        this.fVencimiento = fVencimiento;
    
}

    public String getDiaVencimiento() {
        return diaVencimiento;
    
}

    public void setDiaVencimiento(String diaVencimiento) {
        this.diaVencimiento = diaVencimiento;
    
}

    public String getMesVencimiento() {
        return mesVencimiento;
    
}

    public void setMesVencimiento(String mesVencimiento) {
        this.mesVencimiento = mesVencimiento;
    
}

    public Integer getDifference() {
        return difference;
    
}

    public void setDifference(Integer difference) {
        this.difference = difference;
    
}

    public String getDescResponsable() {
        return descResponsable;
    
}

    public void setDescResponsable(String descResponsable) {
        this.descResponsable = descResponsable;
    
}

    public String getMovimiento() {
        return movimiento;
    
}

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    
}

    public Integer getComprasFilas() {
        return comprasFilas;
    
}

    public void setComprasFilas(Integer comprasFilas) {
        this.comprasFilas = comprasFilas;
    
}

    public String getModalidad() {
        return modalidad;
    
}

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    
}

    public String getAnio() {
        if (id != null && anio == null) return id.getAnio();
        return anio;
    
}

    public void setAnio(String anio) {
        this.anio = anio;
    
}

    public String getMes() {
        if (id != null && mes == null) return id.getMes();
        return mes;
    
}

    public void setMes(String mes) {
        this.mes = mes;
    
}

    


    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getPle() {
        return ple;
    }

    public void setPle(String ple) {
        this.ple = ple;
    }
}
