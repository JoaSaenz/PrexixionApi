package com.joa.prexixionapi.modules.fraccionamiento.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraccionamientoListDTO {
    private Integer idEstadoCliente;
    private String descEstadoCliente;
    private String idCliente;
    private String y;
    private String razonSocial;
    private String codigoFraccionamiento;
    private String nroSolicitud;
    private String fechaSolicitud;
    private Integer idEstadoSolicitud;
    private String estadoSolicitud;
    private String cuotasProgreso;
    private Integer nroCuotas;
    private Integer mesesDeAplazamiento;
    private String nroResolucion;
    private String fechaNotificacion;
    private Integer idEstadoResolucion;
    private String estadoResolucion;

    @JsonProperty("fVencimientoPrimeraCuota")
    private String fVencimientoPrimeraCuota;
}
