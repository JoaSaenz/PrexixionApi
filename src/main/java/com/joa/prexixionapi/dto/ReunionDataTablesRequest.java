package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class ReunionDataTablesRequest {
    private String search;
    private String sortKey;
    private String sortDir;
    
    // Additional filters
    private String estados;
}
