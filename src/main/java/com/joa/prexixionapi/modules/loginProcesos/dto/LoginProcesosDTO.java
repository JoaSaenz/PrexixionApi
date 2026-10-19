package com.joa.prexixionapi.modules.loginProcesos.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.dto.ServicioRegistroDTO;
import com.joa.prexixionapi.common.dto.SignerNivelDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosDTO;

public class LoginProcesosDTO {

    private String movimiento;

    private Integer preLiquidacion;

    private String preLiquidacionFecha;

    private String preLiquidacionUsuario;

    private String preLiquidacionHora;

    private Integer confirmacion;

    private String confirmacionFecha;

    private String confirmacionUsuario;

    private String confirmacionHora;

    private String observacion;

    private Integer idPropuestaVentas;

    private Integer idPropuestaCompras;

    private String dniResponsable2RTB;

    private Integer version;

    // --- Transient fields for compatibility and reports ---
    private String ruc;

    private String anio;

    private String mes;

    private String prioridad;

    private String ple;

    private String y;

    private Integer idEstado;

    private String estado;

    private String razonSocial;

    private String nombreCorto;

    private String correos;

    private String periodoInicioCom;

    private String periodoConcatenado;

    private String areaEncargada;

    private Integer idTipoServicio;

    private String tipoServicio;

    private String tipoServicioAbr;

    private String fVencimiento;

    private String diaVencimiento;

    private String mesVencimiento;

    private Integer difference;

    public Integer orden;

    private Integer ventasFilas;

    private Integer comprasFilas;

    private Double ventasTotales;

    private Double comprasTotales;

    private Double igvPorPagar;

    private Double rentaPorPagar;

    private Integer confirmacionVentas;

    private String confirmacionUsuarioVentas;

    private String confirmacionFechaVentas;

    private Integer confirmacionCompras;

    private String confirmacionUsuarioCompras;

    private String confirmacionFechaCompras;

    private List<ServicioRegistroDTO> registros;

    private Integer sireCV;

    private String rucSire;

    private Integer pleCV;

    private String responsable2RTB;

    private GclassDTO propuestaVentas;

    private GclassDTO propuestaCompras;

    private SignerNivelDTO signerNivel;

    private GclassDTO grupoEconomico;

    private Integer gestionRegimenTributario;

    private String abrGestionRegimenTributario;

    private Integer recepcion;

    private String recepcionFecha;

    private String recepcionUsuario;

    private String recepcionHora;

    private GclassDTO tipoDocumento;

    private Integer archivo;

    private String archivoFecha;

    private String archivoUsuario;

    private String archivoHora;

    private Integer registroVentas;

    private String registroUsuarioVentas;

    private String registroFechaVentas;

    private Integer validacionVentas;

    private String validacionUsuarioVentas;

    private String validacionFechaVentas;

    private Integer registroGeneralCompras;

    private String registroGeneralUsuarioCompras;

    private String registroGeneralFechaCompras;

    private Integer validacionCompras;

    private String validacionUsuarioCompras;

    private String validacionFechaCompras;

    private String solU;

    private String solC;

    private String soldierU;

    private String soldierC;

    private String inventario;

    private String descIgvJusto;

    private String descPago;

    private String descBancoPdt621;

    private String descPagarImpuesto;

    @JsonProperty("descRenta3Categoria")
    private String descRenta3Categoria;

    private String descRentaPago;

    private String descRentaBanco;

    private String descRentaPagarImpuesto;

    // --- Constructor ---
    public LoginProcesosDTO() {
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

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
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

    public List<ServicioRegistroDTO> getRegistros() {
        return registros;
    }

    public void setRegistros(List<ServicioRegistroDTO> registros) {
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

    public GclassDTO getPropuestaVentas() {
        return propuestaVentas;
    }

    public void setPropuestaVentas(GclassDTO propuestaVentas) {
        this.propuestaVentas = propuestaVentas;
    }

    public GclassDTO getPropuestaCompras() {
        return propuestaCompras;
    }

    public void setPropuestaCompras(GclassDTO propuestaCompras) {
        this.propuestaCompras = propuestaCompras;
    }

    public SignerNivelDTO getSignerNivel() {
        return signerNivel;
    }

    public void setSignerNivel(SignerNivelDTO signerNivel) {
        this.signerNivel = signerNivel;
    }

    public GclassDTO getGrupoEconomico() {
        return grupoEconomico;
    }

    public void setGrupoEconomico(GclassDTO grupoEconomico) {
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

    public GclassDTO getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(GclassDTO tipoDocumento) {
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

    public String getDescIgvJusto() {
        return descIgvJusto;
    }

    public void setDescIgvJusto(String descIgvJusto) {
        this.descIgvJusto = descIgvJusto;
    }

    public String getDescPago() {
        return descPago;
    }

    public void setDescPago(String descPago) {
        this.descPago = descPago;
    }

    public String getDescBancoPdt621() {
        return descBancoPdt621;
    }

    public void setDescBancoPdt621(String descBancoPdt621) {
        this.descBancoPdt621 = descBancoPdt621;
    }

    public String getDescPagarImpuesto() {
        return descPagarImpuesto;
    }

    public void setDescPagarImpuesto(String descPagarImpuesto) {
        this.descPagarImpuesto = descPagarImpuesto;
    }

    public String getDescRenta3Categoria() {
        return descRenta3Categoria;
    }

    public void setDescRenta3Categoria(String descRenta3Categoria) {
        this.descRenta3Categoria = descRenta3Categoria;
    }

    public String getDescRentaPago() {
        return descRentaPago;
    }

    public void setDescRentaPago(String descRentaPago) {
        this.descRentaPago = descRentaPago;
    }

    public String getDescRentaBanco() {
        return descRentaBanco;
    }

    public void setDescRentaBanco(String descRentaBanco) {
        this.descRentaBanco = descRentaBanco;
    }

    public String getDescRentaPagarImpuesto() {
        return descRentaPagarImpuesto;
    }

    public void setDescRentaPagarImpuesto(String descRentaPagarImpuesto) {
        this.descRentaPagarImpuesto = descRentaPagarImpuesto;
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
            ServicioRegistroDTO reg = this.registros.get(0);
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
