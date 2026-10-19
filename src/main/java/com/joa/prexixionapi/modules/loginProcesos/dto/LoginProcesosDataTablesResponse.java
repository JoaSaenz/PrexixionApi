package com.joa.prexixionapi.modules.loginProcesos.dto;

import lombok.Data;
import java.util.List;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosDTO;
import com.joa.prexixionapi.modules.loginProcesos.dto.LoginProcesosDataTablesResponse;

@Data
public class LoginProcesosDataTablesResponse {
    private List<LoginProcesosDTO> data;


    // TAKE
    private int confirmacionSi;
    private int confirmacionNo;
    private int confirmacionNa;


    // ENV SIRE
    private int sireCvSi;
    private int sireCvNo;
    private int sireCvNa;


    // ENV PDT
    private int pdtSi;
    private int pdtNo;
    private int pdtNa;

}
