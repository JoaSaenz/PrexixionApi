package com.joa.prexixionapi.modules.loginCompra.dto;

import lombok.Data;

/**
 * DTO para la gestión individual de Login Compras (consulta por ID / getOne y guardado / insertUpdate).
 * Contiene únicamente los atributos requeridos por el formulario modal de registro y edición.
 */
@Data
public class LoginCompraDTO {

    // Identificadores y datos del cliente en el formulario
    private String ruc;
    private String razonSocial;
    private String anio;
    private String mes;
    private Integer version;

    // Control de Procesos de Compras y Auditoría
    private Integer sireCompras; // Mapea a lc.sire (en front #sire)
    private String sireUsuario;
    private String sireFecha;

    private Integer validacionSunat;
    private String validacionSunatUsuario;
    private String validacionSunatFecha;

    private Integer validacion;
    private String validacionUsuario;
    private String validacionFecha;
    private String validacionHora;

    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;
    private String confirmacionHora;

    // Datos informativos de Login Ventas (solo lectura en modal)
    private Integer ventasConfirmacion;
    private String ventasConfirmacionUsuario;
    private String ventasConfirmacionFecha;

    // Observaciones
    private String observacion;
}
