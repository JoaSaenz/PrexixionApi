package com.joa.prexixion.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;
import com.joa.prexixion.entities.Cliente;

@Data
public class ClienteDataTablesRequest {
    private int draw = 1;
    private int start = 0;
    private int length = 10;
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
