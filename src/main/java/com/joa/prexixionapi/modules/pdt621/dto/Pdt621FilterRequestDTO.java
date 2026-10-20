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
    private String categoriaFiltro;
    private String categoriaGrupoEFiltro;
    private String categoriaStoreFiltro;
    private String grupoEconomicoFiltro;
    private String estadoFiltro;
    private String grupoFiltro;
    private String sireFiltro;
    private String sireCvFiltro;
    private String confirmacionFiltro;
    private String pdtFiltro;
    private String mailFiltro;
    private String susFiltro;
    private String pagarImpuestosFiltro;
    private String fechaVencimientoMin;
    private String fechaVencimientoMax;
}

