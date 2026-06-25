package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReunionExcelDTO {
    private int id;
    private String clienteRuc;
    private String clienteRazonSocial;
    private String tipo;
    private String fecha;
    private String horaI;
    private String horaF;
    private int estadoId;
    private String estadoDescripcion;
    private String otros;
    
    // Campos concatenados directamente desde SQL
    private String temas;
    private String areas;
    private String participantesExternos;
    private String participantesInternos;
    private String acuerdos;
}
