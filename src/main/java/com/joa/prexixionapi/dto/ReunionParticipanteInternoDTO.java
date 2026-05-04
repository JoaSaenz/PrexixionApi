package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReunionParticipanteInternoDTO {
    private int idReunion;
    private String dni;
    private String apellidos;
    private String nombres;
    private String puesto;
}
