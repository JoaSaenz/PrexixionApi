package com.joa.prexixionapi.modules.reunion.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReunionRequest {
    private List<Integer> estadoFiltro;
}
