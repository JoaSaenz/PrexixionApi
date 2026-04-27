package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaycomStatsDTO {
    private double tareoAvance;
    private double rxhAvance;
    private double envoyAvance;
    private int totalProcesados;
    private int totalPendientes;
}
