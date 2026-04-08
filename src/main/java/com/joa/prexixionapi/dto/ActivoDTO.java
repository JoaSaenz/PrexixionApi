package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoDTO {
    private String idCliente;
    private String razonSocial;
    private String id;
    private String idProveedor;
    private String proveedor;
    private String descripcion;
    private String marca;
    private String modelo;
    private String seriePlaca;
    private String idTipo;
    private String abreviaturaTipo;
    private String tipo;
    private Double porcentajeDepreciacionContable;
    private Double porcentajeDepreciacionTributaria;
    private String cuenta;
    private String documento;
    private String fechaInicio;
    private String fechaFinContable;
    private String fechaFinTributaria;
    private String idMoneda;
    private String moneda;
    private Double tipoCambio;
    private Double precioUnitario;
    private Integer cantidad;
    private Double costoInicial;
    private Integer idEstado;
    private String estado;
    private String fechaBaja;
    private String fechaCompra;
    private Integer bloqueado;
    private String bloqueadoDesc;

    @Builder.Default
    private List<ActivoDepreciacionDTO> depreciacionesContables = new ArrayList<>();
    @Builder.Default
    private List<ActivoDepreciacionDTO> depreciacionesTributarias = new ArrayList<>();
}
