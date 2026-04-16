package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCadenaTitularidadDTO {
    private int idBeneficiario;
    private String nroOrden;
    private String tipoPersona;
    private String nombresApellidos;
    private String personaIntermediaria;
    private String participacionIntermediaria;
    private String participacionIndirecta;
    private String beneficiarioSiNo;
    private String observacion;
}
