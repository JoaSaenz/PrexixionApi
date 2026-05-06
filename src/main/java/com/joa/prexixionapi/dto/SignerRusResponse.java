package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import com.joa.prexixionapi.entities.Cliente;

@Data
public class SignerRusResponse {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<Cliente> data;
    private Map<Integer, Integer> summaryEstados;
}
