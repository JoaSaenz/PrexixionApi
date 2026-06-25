package com.joa.prexixionapi.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReunionListDTO {
    private int id;
    private int estadoId;
    private String estadoDescripcion;
    private String clienteRuc;
    private String clienteRazonSocial;
    private String tipo;
    private String fecha;
    private String horaI;
    private String horaF;
    private List<String> areas;
    private List<String> temas;
}
