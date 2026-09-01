package com.joa.prexixionapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CasoSunatDocumentoDTO {
    private Object id;
    private Integer idCaso;
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
    private List<CasoSunatDocumentoEventoDTO> eventos;
}
