package com.joa.prexixionapi.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReunionDataTablesResponse {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<ReunionDTO> data;
}
