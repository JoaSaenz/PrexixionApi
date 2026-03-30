package com.joa.prexixion.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;
import com.joa.prexixion.entities.Cliente;

@Data
public class ClienteDataTablesResponse {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<Cliente> data;
    private Map<Integer, Integer> summaryEstados;
    private Map<Integer, Integer> summaryCategorias;
}
