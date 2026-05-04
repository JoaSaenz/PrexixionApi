package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReunionTemaDTO {
    private int idReunion;
    private int id;
    private String tema;
    private String acuerdoTema;
}
