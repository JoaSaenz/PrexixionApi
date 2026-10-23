package com.joa.prexixionapi.modules.fraccionamiento.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraccionamientoFilterRequestDTO {
    private List<Integer> estadosClientes;
    private List<String> clientes;
    private List<String> grupos;
    private List<Integer> estadosSolicitudes;
    private List<Integer> estadosResoluciones;
    private String search;
}
