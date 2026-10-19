package com.joa.prexixionapi.modules.osce.dto;

import lombok.Data;

import java.util.List;

@Data
public class SignerOsceRequest {
    private List<Integer> grupoEconomicoFiltro;
    private List<Integer> estadoFiltro;
    private List<String> grupoFiltro;
}
