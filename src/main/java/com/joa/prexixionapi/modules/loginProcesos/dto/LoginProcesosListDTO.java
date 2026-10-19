package com.joa.prexixionapi.modules.loginProcesos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginProcesosListDTO {
    private String ruc;
    private String razonSocial;
    private String y;
    private String nombreCorto;
    private Integer idEstado;
    private String estado;
    private String categoriaAbreviatura;
    private Integer categoriaStore;
    private String grupoEconomico;
    private String periodoInicioCom;
    private String periodoConcatenado;
    private String anio;
    private String mes;
    private String abrGestionRegimenTributario;
    private String fechaVencimiento;
    private Integer difference;
    private String movimiento;
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
    private Integer preLiquidacion;
    private String preLiquidacionUsuario;
    private String preLiquidacionFecha;
    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;
    private Integer sireCV;
    private Integer pdtRegistrado;
    private Integer avance;
    private String avanceColorClass;
    private String descIgvJusto;
    private String descPago;
    private String descBancoPdt621;
    private String descPagarImpuesto;
    private String descRenta3Categoria;
    private String descRentaPago;
    private String descRentaBanco;
    private String descRentaPagarImpuesto;

    private Integer pleCV;
    private String observacion;
    private String prioridad;
    private String tipoServicio;
    private String ple;
    private Integer validacionCompras;
    private String validacionFechaCompras;
    private String validacionUsuarioCompras;
    private Integer validacionVentas;
    private String validacionFechaVentas;
    private String validacionUsuarioVentas;

}