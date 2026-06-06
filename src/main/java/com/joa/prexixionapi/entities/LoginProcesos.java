package com.joa.prexixionapi.entities;

import java.util.List;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

@Entity
@Table(name = "loginProcesos")
public class LoginProcesos {

    @EmbeddedId
    private LoginProcesosId id;

    @Column(name = "movimiento")
    private String movimiento;

    @Column(name = "preLiquidacion")
    private Integer preLiquidacion;

    @Column(name = "preLiquidacionFecha")
    private String preLiquidacionFecha;

    @Column(name = "preLiquidacionUsuario")
    private String preLiquidacionUsuario;

    @Column(name = "preLiquidacionHora")
    private String preLiquidacionHora;

    @Column(name = "confirmacion")
    private Integer confirmacion;

    @Column(name = "confirmacionFecha")
    private String confirmacionFecha;

    @Column(name = "confirmacionUsuario")
    private String confirmacionUsuario;

    @Column(name = "confirmacionHora")
    private String confirmacionHora;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "idPropuestaVentas")
    private Integer idPropuestaVentas;

    @Column(name = "idPropuestaCompras")
    private Integer idPropuestaCompras;

    @Column(name = "dniResponsable2RTB")
    private String dniResponsable2RTB;

    @Version
    @Column(name = "version")
    private Integer version;

    // --- Transient fields for compatibility and reports ---
    @Transient
    private String ruc;

    @Transient
    private String anio;

    @Transient
    private String mes;

    @Transient
    private String prioridad;

    @Transient
    private String ple;

    @Transient
    private String y;

    @Transient
    private Integer idEstado;

    @Transient
    private String estado;

    @Transient
    private String razonSocial;

    @Transient
    private String nombreCortoSigner;

    @Transient
    private String correos;

    @Transient
    private String periodoInicioCom;

    @Transient
    private String periodoConcatenado;

    @Transient
    private String areaEncargada;

    @Transient
    private Integer idTipoServicio;

    @Transient
    private String tipoServicio;

    @Transient
    private String tipoServicioAbr;

    @Transient
    private String fVencimiento;

    @Transient
    private String diaVencimiento;

    @Transient
    private String mesVencimiento;

    @Transient
    private Integer difference;

    @Transient
    public Integer orden;

    @Transient
    private Integer ventasFilas;

    @Transient
    private Integer comprasFilas;

    @Transient
    private Double ventasTotales;

    @Transient
    private Double comprasTotales;

    @Transient
    private Double igvPorPagar;

    @Transient
    private Double rentaPorPagar;

    @Transient
    private Integer confirmacionVentas;

    @Transient
    private String confirmacionUsuarioVentas;

    @Transient
    private String confirmacionFechaVentas;

    @Transient
    private Integer confirmacionCompras;

    @Transient
    private String confirmacionUsuarioCompras;

    @Transient
    private String confirmacionFechaCompras;

    @Transient
    private List<ServicioRegistro> registros;

    @Transient
    private Integer sireCV;

    @Transient
    private String rucSire;

    @Transient
    private Integer pleCV;

    @Transient
    private String responsable2RTB;

    @Transient
    private Gclass propuestaVentas;

    @Transient
    private Gclass propuestaCompras;

    @Transient
    private SignerNivel signerNivel;

    @Transient
    private Gclass grupoEconomico;

    @Transient
    private Integer gestionRegimenTributario;

    @Transient
    private String abrGestionRegimenTributario;

    @Transient
    private Integer recepcion;

    @Transient
    private String recepcionFecha;

    @Transient
    private String recepcionUsuario;

    @Transient
    private String recepcionHora;

    @Transient
    private Gclass tipoDocumento;

    @Transient
    private Integer archivo;

    @Transient
    private String archivoFecha;

    @Transient
    private String archivoUsuario;

    @Transient
    private String archivoHora;

    @Transient
    private Integer registroVentas;

    @Transient
    private String registroUsuarioVentas;

    @Transient
    private String registroFechaVentas;

    @Transient
    private Integer validacionVentas;

    @Transient
    private String validacionUsuarioVentas;

    @Transient
    private String validacionFechaVentas;

    @Transient
    private Integer registroGeneralCompras;

    @Transient
    private String registroGeneralUsuarioCompras;

    @Transient
    private String registroGeneralFechaCompras;

    @Transient
    private Integer validacionCompras;

    @Transient
    private String validacionUsuarioCompras;

    @Transient
    private String validacionFechaCompras;

    @Transient
    private String solU;

    @Transient
    private String solC;

    @Transient
    private String soldierU;

    @Transient
    private String soldierC;

    @Transient
    private String inventario;

    // --- Constructor ---
    public LoginProcesos() {
    }

    // --- Getters & Setters ---
    public LoginProcesosId getId() {
        return id;
    }

    public void setId(LoginProcesosId id) {
        this.id = id;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public Integer getPreLiquidacion() {
        return preLiquidacion;
    }

    public void setPreLiquidacion(Integer preLiquidacion) {
        this.preLiquidacion = preLiquidacion;
    }

    public String getPreLiquidacionFecha() {
        return preLiquidacionFecha;
    }

    public void setPreLiquidacionFecha(String preLiquidacionFecha) {
        this.preLiquidacionFecha = preLiquidacionFecha;
    }

    public String getPreLiquidacionUsuario() {
        return preLiquidacionUsuario;
    }

    public void setPreLiquidacionUsuario(String preLiquidacionUsuario) {
        this.preLiquidacionUsuario = preLiquidacionUsuario;
    }

    public String getPreLiquidacionHora() {
        return preLiquidacionHora;
    }

    public void setPreLiquidacionHora(String preLiquidacionHora) {
        this.preLiquidacionHora = preLiquidacionHora;
    }

    public Integer getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(Integer confirmacion) {
        this.confirmacion = confirmacion;
    }

    public String getConfirmacionFecha() {
        return confirmacionFecha;
    }

    public void setConfirmacionFecha(String confirmacionFecha) {
        this.confirmacionFecha = confirmacionFecha;
    }

    public String getConfirmacionUsuario() {
        return confirmacionUsuario;
    }

    public void setConfirmacionUsuario(String confirmacionUsuario) {
        this.confirmacionUsuario = confirmacionUsuario;
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

    public Integer getIdPropuestaVentas() {
        return idPropuestaVentas;
    }

    public void setIdPropuestaVentas(Integer idPropuestaVentas) {
        this.idPropuestaVentas = idPropuestaVentas;
    }

    public Integer getIdPropuestaCompras() {
        return idPropuestaCompras;
    }

    public void setIdPropuestaCompras(Integer idPropuestaCompras) {
        this.idPropuestaCompras = idPropuestaCompras;
    }

    public String getDniResponsable2RTB() {
        return dniResponsable2RTB;
    }

    public void setDniResponsable2RTB(String dniResponsable2RTB) {
        this.dniResponsable2RTB = dniResponsable2RTB;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRuc() {
        if (id != null && ruc == null) return id.getRuc();
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
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

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreCortoSigner() {
        return nombreCortoSigner;
    }

    public void setNombreCortoSigner(String nombreCortoSigner) {
        this.nombreCortoSigner = nombreCortoSigner;
    }

    public String getCorreos() {
        return correos;
    }

    public void setCorreos(String correos) {
        this.correos = correos;
    }

    public String getPeriodoInicioCom() {
        return periodoInicioCom;
    }

    public void setPeriodoInicioCom(String periodoInicioCom) {
        this.periodoInicioCom = periodoInicioCom;
    }

    public String getPeriodoConcatenado() {
        return periodoConcatenado;
    }

    public void setPeriodoConcatenado(String periodoConcatenado) {
        this.periodoConcatenado = periodoConcatenado;
    }

    public String getAreaEncargada() {
        return areaEncargada;
    }

    public void setAreaEncargada(String areaEncargada) {
        this.areaEncargada = areaEncargada;
    }

    public Integer getIdTipoServicio() {
        return idTipoServicio;
    }

    public void setIdTipoServicio(Integer idTipoServicio) {
        this.idTipoServicio = idTipoServicio;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getTipoServicioAbr() {
        return tipoServicioAbr;
    }

    public void setTipoServicioAbr(String tipoServicioAbr) {
        this.tipoServicioAbr = tipoServicioAbr;
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getVentasFilas() {
        return ventasFilas;
    }

    public void setVentasFilas(Integer ventasFilas) {
        this.ventasFilas = ventasFilas;
    }

    public Integer getComprasFilas() {
        return comprasFilas;
    }

    public void setComprasFilas(Integer comprasFilas) {
        this.comprasFilas = comprasFilas;
    }

    public Double getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(Double ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public Double getComprasTotales() {
        return comprasTotales;
    }

    public void setComprasTotales(Double comprasTotales) {
        this.comprasTotales = comprasTotales;
    }

    public Double getIgvPorPagar() {
        return igvPorPagar;
    }

    public void setIgvPorPagar(Double igvPorPagar) {
        this.igvPorPagar = igvPorPagar;
    }

    public Double getRentaPorPagar() {
        return rentaPorPagar;
    }

    public void setRentaPorPagar(Double rentaPorPagar) {
        this.rentaPorPagar = rentaPorPagar;
    }

    public Integer getConfirmacionVentas() {
        return confirmacionVentas;
    }

    public void setConfirmacionVentas(Integer confirmacionVentas) {
        this.confirmacionVentas = confirmacionVentas;
    }

    public String getConfirmacionUsuarioVentas() {
        return confirmacionUsuarioVentas;
    }

    public void setConfirmacionUsuarioVentas(String confirmacionUsuarioVentas) {
        this.confirmacionUsuarioVentas = confirmacionUsuarioVentas;
    }

    public String getConfirmacionFechaVentas() {
        return confirmacionFechaVentas;
    }

    public void setConfirmacionFechaVentas(String confirmacionFechaVentas) {
        this.confirmacionFechaVentas = confirmacionFechaVentas;
    }

    public Integer getConfirmacionCompras() {
        return confirmacionCompras;
    }

    public void setConfirmacionCompras(Integer confirmacionCompras) {
        this.confirmacionCompras = confirmacionCompras;
    }

    public String getConfirmacionUsuarioCompras() {
        return confirmacionUsuarioCompras;
    }

    public void setConfirmacionUsuarioCompras(String confirmacionUsuarioCompras) {
        this.confirmacionUsuarioCompras = confirmacionUsuarioCompras;
    }

    public String getConfirmacionFechaCompras() {
        return confirmacionFechaCompras;
    }

    public void setConfirmacionFechaCompras(String confirmacionFechaCompras) {
        this.confirmacionFechaCompras = confirmacionFechaCompras;
    }

    public List<ServicioRegistro> getRegistros() {
        return registros;
    }

    public void setRegistros(List<ServicioRegistro> registros) {
        this.registros = registros;
    }

    public Integer getSireCV() {
        return sireCV;
    }

    public void setSireCV(Integer sireCV) {
        this.sireCV = sireCV;
    }

    public String getRucSire() {
        return rucSire;
    }

    public void setRucSire(String rucSire) {
        this.rucSire = rucSire;
    }

    public Integer getPleCV() {
        return pleCV;
    }

    public void setPleCV(Integer pleCV) {
        this.pleCV = pleCV;
    }

    public String getResponsable2RTB() {
        return responsable2RTB;
    }

    public void setResponsable2RTB(String responsable2RTB) {
        this.responsable2RTB = responsable2RTB;
    }

    public Gclass getPropuestaVentas() {
        return propuestaVentas;
    }

    public void setPropuestaVentas(Gclass propuestaVentas) {
        this.propuestaVentas = propuestaVentas;
    }

    public Gclass getPropuestaCompras() {
        return propuestaCompras;
    }

    public void setPropuestaCompras(Gclass propuestaCompras) {
        this.propuestaCompras = propuestaCompras;
    }

    public SignerNivel getSignerNivel() {
        return signerNivel;
    }

    public void setSignerNivel(SignerNivel signerNivel) {
        this.signerNivel = signerNivel;
    }

    public Gclass getGrupoEconomico() {
        return grupoEconomico;
    }

    public void setGrupoEconomico(Gclass grupoEconomico) {
        this.grupoEconomico = grupoEconomico;
    }

    public Integer getGestionRegimenTributario() {
        return gestionRegimenTributario;
    }

    public void setGestionRegimenTributario(Integer gestionRegimenTributario) {
        this.gestionRegimenTributario = gestionRegimenTributario;
    }

    public String getAbrGestionRegimenTributario() {
        return abrGestionRegimenTributario;
    }

    public void setAbrGestionRegimenTributario(String abrGestionRegimenTributario) {
        this.abrGestionRegimenTributario = abrGestionRegimenTributario;
    }

    public Integer getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(Integer recepcion) {
        this.recepcion = recepcion;
    }

    public String getRecepcionFecha() {
        return recepcionFecha;
    }

    public void setRecepcionFecha(String recepcionFecha) {
        this.recepcionFecha = recepcionFecha;
    }

    public String getRecepcionUsuario() {
        return recepcionUsuario;
    }

    public void setRecepcionUsuario(String recepcionUsuario) {
        this.recepcionUsuario = recepcionUsuario;
    }

    public String getRecepcionHora() {
        return recepcionHora;
    }

    public void setRecepcionHora(String recepcionHora) {
        this.recepcionHora = recepcionHora;
    }

    public Gclass getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Gclass tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Integer getArchivo() {
        return archivo;
    }

    public void setArchivo(Integer archivo) {
        this.archivo = archivo;
    }

    public String getArchivoFecha() {
        return archivoFecha;
    }

    public void setArchivoFecha(String archivoFecha) {
        this.archivoFecha = archivoFecha;
    }

    public String getArchivoUsuario() {
        return archivoUsuario;
    }

    public void setArchivoUsuario(String archivoUsuario) {
        this.archivoUsuario = archivoUsuario;
    }

    public String getArchivoHora() {
        return archivoHora;
    }

    public void setArchivoHora(String archivoHora) {
        this.archivoHora = archivoHora;
    }

    public Integer getRegistroVentas() {
        return registroVentas;
    }

    public void setRegistroVentas(Integer registroVentas) {
        this.registroVentas = registroVentas;
    }

    public String getRegistroUsuarioVentas() {
        return registroUsuarioVentas;
    }

    public void setRegistroUsuarioVentas(String registroUsuarioVentas) {
        this.registroUsuarioVentas = registroUsuarioVentas;
    }

    public String getRegistroFechaVentas() {
        return registroFechaVentas;
    }

    public void setRegistroFechaVentas(String registroFechaVentas) {
        this.registroFechaVentas = registroFechaVentas;
    }

    public Integer getValidacionVentas() {
        return validacionVentas;
    }

    public void setValidacionVentas(Integer validacionVentas) {
        this.validacionVentas = validacionVentas;
    }

    public String getValidacionUsuarioVentas() {
        return validacionUsuarioVentas;
    }

    public void setValidacionUsuarioVentas(String validacionUsuarioVentas) {
        this.validacionUsuarioVentas = validacionUsuarioVentas;
    }

    public String getValidacionFechaVentas() {
        return validacionFechaVentas;
    }

    public void setValidacionFechaVentas(String validacionFechaVentas) {
        this.validacionFechaVentas = validacionFechaVentas;
    }

    public Integer getRegistroGeneralCompras() {
        return registroGeneralCompras;
    }

    public void setRegistroGeneralCompras(Integer registroGeneralCompras) {
        this.registroGeneralCompras = registroGeneralCompras;
    }

    public String getRegistroGeneralUsuarioCompras() {
        return registroGeneralUsuarioCompras;
    }

    public void setRegistroGeneralUsuarioCompras(String registroGeneralUsuarioCompras) {
        this.registroGeneralUsuarioCompras = registroGeneralUsuarioCompras;
    }

    public String getRegistroGeneralFechaCompras() {
        return registroGeneralFechaCompras;
    }

    public void setRegistroGeneralFechaCompras(String registroGeneralFechaCompras) {
        this.registroGeneralFechaCompras = registroGeneralFechaCompras;
    }

    public Integer getValidacionCompras() {
        return validacionCompras;
    }

    public void setValidacionCompras(Integer validacionCompras) {
        this.validacionCompras = validacionCompras;
    }

    public String getValidacionUsuarioCompras() {
        return validacionUsuarioCompras;
    }

    public void setValidacionUsuarioCompras(String validacionUsuarioCompras) {
        this.validacionUsuarioCompras = validacionUsuarioCompras;
    }

    public String getValidacionFechaCompras() {
        return validacionFechaCompras;
    }

    public void setValidacionFechaCompras(String validacionFechaCompras) {
        this.validacionFechaCompras = validacionFechaCompras;
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

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    public int getAvance() {
        int cantSiNa = 0;
        if (this.confirmacionVentas != null && this.confirmacionVentas == 1) {
            cantSiNa++;
        }
        if (this.confirmacionCompras != null && this.confirmacionCompras == 1) {
            cantSiNa++;
        }
        if (this.preLiquidacion != null && (this.preLiquidacion == 1 || this.preLiquidacion == 2)) {
            cantSiNa++;
        }
        if (this.confirmacion != null && (this.confirmacion == 1 || this.confirmacion == 2)) {
            cantSiNa++;
        }
        if (this.sireCV != null && (this.sireCV == 1 || this.sireCV == 2)) {
            cantSiNa++;
        }
        if (this.registros != null && !this.registros.isEmpty()) {
            ServicioRegistro reg = this.registros.get(0);
            if (reg != null && reg.getNroOrden() != null && !reg.getNroOrden().trim().equals("")) {
                cantSiNa++;
            }
        }
        return (int) Math.round((cantSiNa * 100.0) / 6.0);
    }

    public String getAvanceColorClass() {
        int pct = this.getAvance();
        if (pct >= 80) {
            return "bg-success";
        } else if (pct >= 50) {
            return "bg-warning";
        } else {
            return "bg-danger";
        }
    }
}
