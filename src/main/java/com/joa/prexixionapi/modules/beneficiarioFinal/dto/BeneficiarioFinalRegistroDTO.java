package com.joa.prexixionapi.modules.beneficiarioFinal.dto;

import com.joa.prexixionapi.common.dto.GclassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalRegistroDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioFinalRegistroDTO {
    private Integer id; // ID Técnico (Primary Key para JPA)
    private int nroRegistro; // ID Lógico (Correlativo de negocio)
    private GclassDTO tipo;
    private String fecha;
    private String nroRectificacion;
    private String nroOrden;
}
