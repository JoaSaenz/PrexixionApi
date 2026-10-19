package com.joa.prexixionapi.common.dto;

import lombok.Data;

@Data
public class SignerNivelDTO {
    private GclassDTO nivelFijo;
    private GclassDTO nivelX3;
    private GclassDTO categoria;
    private int categoriaGrupoE;
    private int categoriaStore;
}
