package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class SunatBuzonDataTablesResponse {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<SunatBuzonDTO> data;
}
