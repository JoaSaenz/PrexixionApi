package com.joa.prexixionapi.modules.cliente.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDTO;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesResponse;

@Data
public class ClienteDataTablesResponse {
    private List<ClienteDTO> data;
    private Map<Integer, Integer> summaryEstados;
    private Map<Integer, Integer> summaryCategorias;
}
