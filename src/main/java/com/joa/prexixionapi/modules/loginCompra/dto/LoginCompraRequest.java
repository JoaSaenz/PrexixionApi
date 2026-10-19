package com.joa.prexixionapi.modules.loginCompra.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginCompraRequest {
    private String periodoI;
    private String periodoF;
    private List<Integer> estadoFiltro;
    private List<Integer> categoriaStoreFiltro;
    private List<Integer> grupoEconomicoFiltro;
    private List<String> grupoFiltro;
    private List<Integer> ventasConfirmacionFiltro;
    private List<Integer> sireFiltro;
    private List<Integer> validacionSunatFiltro;
    private List<Integer> validacionFiltro;
    private List<Integer> confirmacionFiltro;

    private String fechaVencimientoMin;
    private String fechaVencimientoMax;

    private String proceso;
    private String fechaI;
    private String fechaF;
}
