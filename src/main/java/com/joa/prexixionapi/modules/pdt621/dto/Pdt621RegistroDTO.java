package com.joa.prexixionapi.modules.pdt621.dto;

import com.joa.prexixionapi.common.dto.GclassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdt621RegistroDTO {
    private Integer id;
    private String nroRectificacion;
    private String fecha;
    private String nroOrden;
    private GclassDTO tipo;

    public Integer getTipoId() {
        return tipo != null ? tipo.getId() : null;
    }
}
