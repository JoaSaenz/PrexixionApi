package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasoSunatSeguimientoDTO {
    // Campos del Caso SUNAT (Padre)
    private Integer idCaso;
    private String idEmpresa;
    private String razonSocial;
    private Integer idTipoCaso;
    private String descTipoCaso;
    private Integer idModalidad;
    private String descModalidad;
    private Integer idTributo;
    private String descTributo;
    private Integer idMotivo;
    private String descMotivo;
    private String periodoTexto;
    private Integer coordinacionTax;
    private Integer coordinacionFir;
    private String ultimoAuditor;

    // Campos del Documento SUNAT (Hijo)
    private Integer idDocumento;
    private Integer idTipoDocumento;
    private String descTipoDocumento;
    private String nroDocumento;
    private String fechaRecepcion;
    private String fechaEnvio;
    private String fechaPresentacion;
    private String hora;
    private String fechaResultado;
    private Integer idEstado;
    private String descEstado;
    private BigDecimal importeObservado;
    private Integer rectificatoria;
    private BigDecimal importeRectificado;
}
