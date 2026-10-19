package com.joa.prexixionapi.modules.loginProcesos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginProcesosAvanceDTO {
    private int idProceso;
    private String descProceso;
    private String fechaAvance;
    private int avanceTM;
    private int avanceTT;
}
