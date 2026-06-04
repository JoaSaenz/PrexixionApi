package com.joa.prexixionapi.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.Column;

@Entity
@Table(name = "loginCompras")
public class LoginCompra {

    @EmbeddedId
    private LoginCompraId id;

    @Column(name = "sire")
    private Integer sire;

    @Column(name = "sireUsuario")
    private String sireUsuario;

    @Column(name = "sireFecha")
    private String sireFecha;

    @Column(name = "validacionSunat")
    private Integer validacionSunat;

    @Column(name = "validacionSunatUsuario")
    private String validacionSunatUsuario;

    @Column(name = "validacionSunatFecha")
    private String validacionSunatFecha;

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

    @Column(name = "observacion")
    private String observacion;

    @Version
    @Column(name = "version")
    private Integer version;

    // Transient fields for business logic and legacy compatibility
    @jakarta.persistence.Transient
    private String prioridad;

    @jakarta.persistence.Transient
    private String ple;

    @jakarta.persistence.Transient
    private String inventario;

    @jakarta.persistence.Transient
    private String rucSire;

    @jakarta.persistence.Transient
    private String movimiento;

    @jakarta.persistence.Transient
    private String fVencimientoGear;

    @jakarta.persistence.Transient
    private String fVencimiento;

    @jakarta.persistence.Transient
    private Integer difference;

    @jakarta.persistence.Transient
    private String diaVencimiento;

    @jakarta.persistence.Transient
    private String mesVencimiento;

    @jakarta.persistence.Transient
    private Integer modalidad;

    @jakarta.persistence.Transient
    private Integer comprasFilas;

    @jakarta.persistence.Transient
    private String anio;

    @jakarta.persistence.Transient
    private String mes;

    @jakarta.persistence.Transient
    private String validacionUsuarioNombreCorto;

    public LoginCompra() {}

    public LoginCompraId getId() {
        return id;
    }

    public void setId(LoginCompraId id) {
        this.id = id;
    }

    public Integer getSire() {
        return sire;
    }

    public void setSire(Integer sire) {
        this.sire = sire;
    }

    public String getSireUsuario() {
        return sireUsuario;
    }

    public void setSireUsuario(String sireUsuario) {
        this.sireUsuario = sireUsuario;
    }

    public String getSireFecha() {
        return sireFecha;
    }

    public void setSireFecha(String sireFecha) {
        this.sireFecha = sireFecha;
    }

    public Integer getValidacionSunat() {
        return validacionSunat;
    }

    public void setValidacionSunat(Integer validacionSunat) {
        this.validacionSunat = validacionSunat;
    }

    public String getValidacionSunatUsuario() {
        return validacionSunatUsuario;
    }

    public void setValidacionSunatUsuario(String validacionSunatUsuario) {
        this.validacionSunatUsuario = validacionSunatUsuario;
    }

    public String getValidacionSunatFecha() {
        return validacionSunatFecha;
    }

    public void setValidacionSunatFecha(String validacionSunatFecha) {
        this.validacionSunatFecha = validacionSunatFecha;
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

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    public String getRucSire() {
        return rucSire;
    }

    public void setRucSire(String rucSire) {
        this.rucSire = rucSire;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public String getfVencimientoGear() {
        return fVencimientoGear;
    }

    public void setfVencimientoGear(String fVencimientoGear) {
        this.fVencimientoGear = fVencimientoGear;
    }

    public String getfVencimiento() {
        return fVencimiento;
    }

    public void setfVencimiento(String fVencimiento) {
        this.fVencimiento = fVencimiento;
    }

    public Integer getDifference() {
        return difference;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
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

    public Integer getModalidad() {
        return modalidad;
    }

    public void setModalidad(Integer modalidad) {
        this.modalidad = modalidad;
    }

    public Integer getComprasFilas() {
        return comprasFilas;
    }

    public void setComprasFilas(Integer comprasFilas) {
        this.comprasFilas = comprasFilas;
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

    public String getValidacionUsuarioNombreCorto() {
        return validacionUsuarioNombreCorto;
    }

    public void setValidacionUsuarioNombreCorto(String validacionUsuarioNombreCorto) {
        this.validacionUsuarioNombreCorto = validacionUsuarioNombreCorto;
    }
}
