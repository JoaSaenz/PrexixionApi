package com.joa.prexixionapi.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CasoSunatChecklistDTO {
    private Object id;
    private Integer idCaso;
    private Object idPuntoPadre;
    private Integer idNivel;
    private String codigo;
    private String descripcion;
    private Integer orden;
    private Integer nroDocumentos;
    private BigDecimal importeObservado;
    private BigDecimal importeLevantado;
    private BigDecimal importeReparo;
    private Object idEstado;
    private String descEstado;
    private String observacion;
}
