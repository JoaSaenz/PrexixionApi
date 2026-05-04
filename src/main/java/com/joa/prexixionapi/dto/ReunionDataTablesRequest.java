package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class ReunionDataTablesRequest {
    private int draw;
    private int start;
    private int length;
    private String search;
    private String sortKey;
    private String sortDir;
    
    // Additional filters
    private String estados;
}
