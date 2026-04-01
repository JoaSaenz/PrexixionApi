package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class SunatBuzonDTO {
    private String ruc;
    private String y;
    private int idGrupoEconomico;
    private String grupoEconomico;
    private int idEstado;
    private String estado;
    private String razonSocial;
    private String nombreCorto;
    private String notificaciones;

    private Long id;
    private String idSunat;
    private String fecha;
    private boolean tieneAdjuntos;
    private boolean tieneAdjuntosDia;
    private int notificacionesDiaCount;
    private boolean revisadoDia;
    private boolean revisado;
}
