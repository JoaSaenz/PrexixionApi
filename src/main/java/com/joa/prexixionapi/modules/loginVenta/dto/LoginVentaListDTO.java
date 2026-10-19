package com.joa.prexixionapi.modules.loginVenta.dto;

import lombok.Data;

@Data
public class LoginVentaListDTO {
    // Datos del Cliente
    private String ruc;
    private String y;
    private String razonSocial;
    private String nombreCorto;

    // Clasificaciones y Grupos
    private Integer estadoId;
    private String estadoDescripcion;
    private String tipoServicioAbr;
    private String tipoServicioDesc;
    private Integer grupoEconomicoId;
    private String descGrupoEconomico;
    private Integer categoriaStore;
    private String regimenTributario;

    // Sire Global
    private Integer gerenciaSire;
    private String sireGlobal;

    // Login Ventas - Campos List y Popovers
    private String responsable;
    private String descResponsable;
    
    private Integer registro;
    private String registroUsuario;
    private String registroFecha;

    private Integer revisionSunat;
    private String revisionSunatUsuario;
    private String revisionSunatFecha;

    private Integer validacion;
    private String validacionUsuario;
    private String validacionFecha;

    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;

    private Integer seeVentas;
    private Integer extVentas;

    // Login Ventas - Campos Calculados / Lógicos
    private String fechaVencimiento;
    private String movimiento;
    private Integer comprasFilas;
    private String anio;
    private String mes;
    private Integer sunatSire;
    private Integer externoSire;
    private String observacion;
    private String prioridad;

    private String ple;
}
