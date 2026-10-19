package com.joa.prexixionapi.modules.rus.dto;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.dto.SignerNivelDTO;
import lombok.Data;

@Data
public class SignerRusListDTO {
    private String ruc;
    private String y;
    private String razonSocial;
    private GclassDTO estado;
    private GclassDTO servicio;
    private GclassDTO grupoEconomico;
    private SignerNivelDTO signerNivel;
    private String regimenTributario;
    private String periodoIEnvoyRus;
    private String periodoFEnvoyRus;
}
