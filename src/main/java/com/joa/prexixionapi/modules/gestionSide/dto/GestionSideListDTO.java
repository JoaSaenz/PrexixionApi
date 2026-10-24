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
public class GestionSideListDTO {
    private Integer id;

    // Grupo Económico
    private Integer idGrupoEconomico;
    private String descGrupoEconomico;

    // Estado Cliente
    private Integer idEstadoCliente;
    private String descEstadoCliente;

    // Cliente
    private String idCliente;
    private String razonSocial;

    // Estado Módulo
    private Integer idEstado;
    private String descEstado;

    // Área
    private Integer idArea;
    private String descArea;

    // Servicio
    private Integer idServicio;
    private String descServicio;

    // Detalle, Fechas e Importe
    private String detalle;
    private String fechaRecibido;
    private String fechaEntrega;
    private BigDecimal importe;
}
