package com.joa.prexixionapi.modules.pdt621.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pdt621FilterRequestDTO {
    private String periodoI;
    private String periodoF;
    private String ruc;
    private String categorias;
    private String categoriaGrupoE;
    private String categoriaStore;
    private String grupoEconomico;
    private String estados;
    private String grupos;
    private String sire;
    private String sireCv;
    private String confirmacion;
    private String pdt;
    private String mail;
    private String sus;
    private String pagarImpuestos;
}
