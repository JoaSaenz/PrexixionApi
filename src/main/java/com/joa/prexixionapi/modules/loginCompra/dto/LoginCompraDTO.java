package com.joa.prexixionapi.modules.loginCompra.dto;

import lombok.Data;
import com.joa.prexixionapi.modules.loginCompra.dto.LoginCompraDTO;

@Data
public class LoginCompraDTO {
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

    // Login Ventas
    private Integer ventasConfirmacion;
    private String ventasConfirmacionUsuario;
    private String ventasConfirmacionFecha;

    // Login Compras - Campos de Base de Datos
    private Integer sireCompras; // Reemplaza a lc.sire
    private String sireUsuario;
    private String sireFecha;

    private Integer validacionSunat;
    private String validacionSunatUsuario;
    private String validacionSunatFecha;

    private Integer validacion;
    private String validacionUsuario;
    private String validacionUsuarioNombreCorto;
    private String validacionFecha;
    private String validacionHora;

    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;
    private String confirmacionHora;

    private String observacion;
    private Integer version;

    // Login Compras - Campos Calculados / Lógicos
    private String fechaVencimiento;
    private String diaVencimiento;
    private String mesVencimiento;
    private Integer difference;
    private String anio;
    private String mes;
    private String movimiento;
    private Integer comprasFilas;
    private Integer modalidad;
    private String prioridad;
    private String ple;
}
