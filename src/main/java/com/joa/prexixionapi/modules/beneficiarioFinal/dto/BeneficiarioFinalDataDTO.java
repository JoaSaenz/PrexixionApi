package com.joa.prexixionapi.modules.beneficiarioFinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalDataDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioFinalDataDTO {
    private String idCliente;
    private String anio;
    private String mes;

    private String observacion;
    private Integer mail;
}
