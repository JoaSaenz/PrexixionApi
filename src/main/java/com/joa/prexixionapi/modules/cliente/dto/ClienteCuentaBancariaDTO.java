package com.joa.prexixionapi.modules.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.cliente.dto.ClienteCuentaBancariaDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCuentaBancariaDTO {
    private String descEstado;
    private String acInicioCom;
    private String idCliente;
    private String y;
    private String abrContribuyente;
    private String razonSocial;
    private String ccbCCI;
}
