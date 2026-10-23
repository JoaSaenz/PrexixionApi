package com.joa.prexixionapi.modules.loginVenta.dto;

import lombok.Data;

/**
 * DTO para la gestión individual de Login Ventas (consulta por ID / getOne y guardado / insertUpdate).
 * Contiene únicamente los atributos requeridos por el formulario modal de registro y edición.
 */
@Data
public class LoginVentaDTO {

    // Identificadores y datos del cliente en el formulario
    private String ruc;
    private String razonSocial;
    private String anio;
    private String mes;
    private String sire;
    private Integer version;

    // Asignación de responsable
    private String responsable;

    // Montos
    private Integer seeVentas;
    private Integer extVentas;

    // Control de Procesos y Auditoría
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

    // Observaciones
    private String observacion;
}
