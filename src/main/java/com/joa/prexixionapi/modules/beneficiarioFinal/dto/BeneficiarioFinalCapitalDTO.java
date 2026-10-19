package com.joa.prexixionapi.modules.beneficiarioFinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalCapitalDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioFinalCapitalDTO {
    private int idBeneficiario;
    private String nroOrden;
    private String fechaCierta;
    private String nombresApellidos;
    private String valorNominal;
    private String participacionDirecta;
    private String beneficiarioSiNo;
    private String observacion;
}
