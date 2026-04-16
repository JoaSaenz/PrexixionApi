package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCapitalPPJJDTO {
    private int idBeneficiario;
    private String nroOrden;
    private String fechaCierta;
    private String nombresApellidos;
    private String valorNominal;
    private String participacionDirecta;
    private String beneficiarioSiNo;
    private String observacion;
}
