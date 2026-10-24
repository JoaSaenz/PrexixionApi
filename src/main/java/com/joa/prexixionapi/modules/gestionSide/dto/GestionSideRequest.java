package com.joa.prexixionapi.modules.gestionSide.dto;

import lombok.Data;
import java.util.List;

@Data
public class GestionSideRequest {
    private List<Integer> grupoEconomicoFiltro;
    private List<Integer> estadoClienteFiltro;
    private List<Integer> estadoModuloFiltro;
    private List<Integer> areaFiltro;
    private List<Integer> servicioFiltro;
}
