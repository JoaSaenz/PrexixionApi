package com.joa.prexixionapi.modules.trade.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TradeClienteDataTablesResponse {
    private List<TradeClienteDTO> data;
    private Map<Integer, Integer> summaryEstados;
}
