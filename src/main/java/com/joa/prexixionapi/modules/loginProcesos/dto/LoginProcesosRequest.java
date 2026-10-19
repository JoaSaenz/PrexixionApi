package com.joa.prexixionapi.modules.loginProcesos.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginProcesosRequest {
    private String periodoI;
    private String periodoF;
    private List<Integer> estadoFiltro;
    private List<Integer> categoriaFiltro;
    private List<Integer> categoriaStoreFiltro;
    private List<Integer> grupoEconomicoFiltro;
    private List<String> grupoFiltro;
    private List<Integer> confirmacionVentaFiltro;
    private List<Integer> confirmacionCompraFiltro;
    private List<Integer> preLiquidacionFiltro;
    private List<Integer> confirmacionFiltro;
    private List<Integer> sireCvFiltro;
    private List<Integer> pdtFiltro;
    private String fechaVencimientoMin;
    private String fechaVencimientoMax;
    private String proceso;
    private String fechaI;
    private String fechaF;
}
