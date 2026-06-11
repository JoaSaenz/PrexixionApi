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
