package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;
import com.joa.prexixionapi.entities.Cliente;

@Data
public class LoginCompraDataTablesResponse {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<Cliente> data;
    
    // Conteos para la tabla resumen superior
    private int sireSi;
    private int sireNo;
    private int sireNa;
    
    private int validacionSunatSi;
    private int validacionSunatNo;
    private int validacionSunatNa;
    
    private int validacionSi;
    private int validacionNo;
    private int validacionNa;
    private int validacionVal;
    
    private int confirmacionSi;
    private int confirmacionNo;
}
