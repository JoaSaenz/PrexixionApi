package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;
import com.joa.prexixionapi.entities.Cliente;

@Data
public class LoginVentaDataTablesResponse {
    private int recordsTotal;
    private int recordsFiltered;
    private List<Cliente> data;
    
    // Conteos para la tabla resumen superior
    private int registroSi;
    private int registroNo;
    private int registroNa;
    
    private int revisionSunatSi;
    private int revisionSunatNo;
    private int revisionSunatNa;
    
    private int validacionSi;
    private int validacionNo;
    private int validacionNa;
    private int validacionVal;
    
    private int confirmacionSi;
    private int confirmacionNo;
}
