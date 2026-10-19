package com.joa.prexixionapi.modules.loginCompra.dto;

import lombok.Data;

@Data
public class LoginCompraListDTO {
    private String ruc;
    private String y;
    private String razonSocial;
    private String nombreCorto;
    
    private Integer estadoId;
    private String estadoDescripcion;
    private Integer categoriaStore;
    private Integer grupoEconomicoId;
    private String descGrupoEconomico;
    
    private String regimenTributario;
    private String anio;
    private String mes;
    
    private String fechaVencimiento;
    private String movimiento;
    private Integer comprasFilas;
    
    private Integer ventasConfirmacion;
    private Integer sireCompras;
    private String sireUsuario;
    private String sireFecha;

    private Integer validacionSunat;
    private String validacionSunatUsuario;
    private String validacionSunatFecha;

    private Integer validacion;
    private String validacionUsuario;
    private String validacionFecha;
    private String validacionUsuarioNombreCorto;

    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;
    
    private String observacion;
}
