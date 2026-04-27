package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronogramaDetailDTO {
    private String tipo;
    private String periodo;
    private String ruc;
    private String fechaVencimiento;
    private String estado; // Pendiente, Vencido, etc.
}
