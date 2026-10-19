package com.joa.prexixionapi.modules.loginVenta.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginVentaRequest {
    private String periodoI;
    private String periodoF;
    private List<Integer> estadoFiltro;
    private List<Integer> categoriaStoreFiltro;
    private List<Integer> grupoEconomicoFiltro;
    private List<String> grupoFiltro;
    private List<String> responsableFiltro;
    private List<Integer> registroFiltro;
    private List<Integer> revisionSunatFiltro;
    private List<Integer> validacionFiltro;
    private List<Integer> confirmacionFiltro;
    private List<Integer> nivelFijoFiltro;
    private List<Integer> nivelX3Filtro;
    private List<Integer> seeVentasFiltro;
    private List<Integer> extVentasFiltro;

    private String fechaVencimientoMin;
    private String fechaVencimientoMax;

    private String proceso;
    private String fechaI;
    private String fechaF;
}
