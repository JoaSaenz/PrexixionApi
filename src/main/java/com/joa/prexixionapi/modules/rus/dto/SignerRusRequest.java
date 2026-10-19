package com.joa.prexixionapi.modules.rus.dto;

import lombok.Data;

import java.util.List;

@Data
public class SignerRusRequest {
    private List<Integer> nivelFijoFiltro;
    private List<Integer> nivelX3Filtro;
    private List<Integer> grupoEconomicoFiltro;
    private List<Integer> estadoFiltro;
    private List<String> grupoFiltro;
}
