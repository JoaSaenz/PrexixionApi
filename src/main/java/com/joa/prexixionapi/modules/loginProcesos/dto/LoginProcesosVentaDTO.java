package com.joa.prexixionapi.modules.loginProcesos.dto;

import com.joa.prexixionapi.common.dto.GclassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginProcesosVentaDTO {
    private Integer idVenta;
    private String ruc;
    private String anio;
    private String mes;
    private String serie;
    private String desde;
    private String hasta;
    private String fchUltima;
    private GclassDTO tipoVenta;
}
