package com.joa.prexixionapi.modules.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.cliente.dto.ClienteClavesDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteClavesDTO {
    private String descEstado;
    private String ruc;
    private String y;
    private String abrContribuyente;
    private String razonSocial;
    private String solU;
    private String solC;
    private String upsU;
    private String upsC;
    private String soldierU;
    private String soldierC;
    private String signerU;
    private String signerC;
    private String afpU;
    private String afpC;
    private String sisU;
    private String sisC;
    private String abrCategoria;
    private Integer categoriaGrupoE;
    private Integer categoriaStore;
}
