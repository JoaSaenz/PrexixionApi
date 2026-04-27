package com.joa.prexixionapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoCambioDTO {
    private String periodo;
    private String fecha;
    
    @JsonProperty("tVenta")
    private Double tVenta;
    
    @JsonProperty("tCompra")
    private Double tCompra;
}
