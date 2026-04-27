package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationStatsDTO {
    private int inventariosSi;
    private int inventariosNo;
    private int balancesSi;
    private int balancesNo;
    private int pdt621Si;
    private int pdt621No;
    private int pdt601Si;
    private int pdt601No;
}
