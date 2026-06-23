package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import com.joa.prexixionapi.entities.TradeCliente;

@Data
public class TradeClienteDataTablesResponse {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<TradeCliente> data;
    private Map<Integer, Integer> summaryEstados;
}
