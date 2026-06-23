package com.joa.prexixionapi.entities;

import lombok.Data;

@Data
public class TradeClienteBudget {
    private int buId;
    private int IdTradeCliente;
    private String buFechaInicio;
    private String buFechaFin;
    private int buIdRespuesta;
    private String buRespuestaDescripcion;
    private String buNumero;
    private int buIdTipoServicio;
    private String buTpServicioDescripcion;
    private int buIdTipoCosto;
    private String buTipoCostoDescripcion;
    private double buImporte;
    private int buIdMoneda;
    private String buMonedaDescripcion;
    private int buIdIgv;
    private String buIgvDescripcion;
    private int buIdSideProduccion;
    private String buSideProduccionDescripcion;
    private String buObservacion;
}
