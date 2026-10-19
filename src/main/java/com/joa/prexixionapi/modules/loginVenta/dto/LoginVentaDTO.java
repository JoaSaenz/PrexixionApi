package com.joa.prexixionapi.modules.loginVenta.dto;

import lombok.Data;

@Data
public class LoginVentaDTO {
    // Datos del ClienteDTO
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
    private Integer sunatSire;
    private Integer externoSire;
    private Integer gerenciaSire;
    private String sireGlobal; // Reemplaza al 'sire' que iba en ClienteDTO

    // Login Ventas - Campos de Base de Datos
    private String responsable;
    private Integer registro;
    private String registroUsuario;
    private String registroFecha;
    private String registroHora;

    private Integer revisionSunat;
    private String revisionSunatUsuario;
    private String revisionSunatFecha;

    private Integer validacion;
    private String validacionUsuario;
    private String validacionFecha;
    private String validacionHora;

    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;
    private String confirmacionHora;

    private Integer seeVentas;
    private Integer extVentas;
    private String observacion;
    private Integer version;

    // Login Ventas - Campos Calculados / Lógicos
    private String fechaVencimiento;
    private String diaVencimiento;
    private String mesVencimiento;
    private Integer difference;
    private String descResponsable;
    private String movimiento;
    private Integer comprasFilas;
    private String modalidad;
    private String anio;
    private String mes;
    private String prioridad;
    private String ple;
}
