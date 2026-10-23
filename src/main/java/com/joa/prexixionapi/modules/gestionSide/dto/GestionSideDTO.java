package com.joa.prexixionapi.modules.gestionSide.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GestionSideDTO {
    private Integer id;
    private String idCliente;
    private String razonSocial;
    private Integer idEstado;
    private String descEstado;
    private Integer idArea;
    private String descArea;
    private Integer idServicio;
    private String descServicio;
    private String detalle;
    private String fechaRecibido;
    private String fechaEntrega;
    private BigDecimal importe;
}
