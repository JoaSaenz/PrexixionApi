package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;
import com.joa.prexixionapi.entities.LoginProcesos;

@Data
public class LoginProcesosDataTablesResponse {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<LoginProcesos> data;

    // V-CON
    private int confirmacionVentasSi;
    private int confirmacionVentasNo;
    private int confirmacionVentasF;

    // C-CON
    private int confirmacionComprasSi;
    private int confirmacionComprasNo;
    private int confirmacionComprasF;

    // TAKE B
    private int preLiquidacionSi;
    private int preLiquidacionNo;
    private int preLiquidacionNa;
    private int preLiquidacionF;

    // TAKE
    private int confirmacionSi;
    private int confirmacionNo;
    private int confirmacionNa;
    private int confirmacionF;

    // ENV SIRE
    private int sireCvSi;
    private int sireCvNo;
    private int sireCvNa;
    private int sireCvF;

    // ENV PDT
    private int pdtSi;
    private int pdtNo;
    private int pdtNa;
    private int pdtF;
}
