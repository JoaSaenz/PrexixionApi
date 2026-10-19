package com.joa.prexixionapi.modules.beneficiarioFinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalCadenaTitularidadDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioFinalCadenaTitularidadDTO {
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
