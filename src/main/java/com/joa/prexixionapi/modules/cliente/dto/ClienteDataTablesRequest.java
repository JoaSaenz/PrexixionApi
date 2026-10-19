package com.joa.prexixionapi.modules.cliente.dto;

import lombok.Data;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesRequest;

@Data
public class ClienteDataTablesRequest {
    private String search;
    private String categoriasString;
    private String categoriaGrupoEString;
    private String categoriaStoreString;
    private String gruposEconomicosString;
    private String estadosString;
    private String gruposString;
    private String taxReviewString;
    private String sortKey;
    private String sortDir;
}
