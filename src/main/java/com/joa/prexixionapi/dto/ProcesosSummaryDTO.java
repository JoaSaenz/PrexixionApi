package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcesosSummaryDTO {
    private int recepcionSi;
    private int recepcionNo;
    private int recepcionNa;
    
    private int archivoSi;
    private int archivoNo;
    private int archivoNa;
    
    private int validacionSi;
    private int validacionNo;
    private int validacionNa;
    
    private int pdtSi;
    private int pdtNo;
    
    private int pleSi;
    private int pleNo;
    private int pleNa;
    
    private int total;
}
