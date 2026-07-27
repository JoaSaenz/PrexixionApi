package com.joa.prexixionapi.dto;

import lombok.Data;

@Data
public class SignerOsceListDTO {
    private String idCliente;
    private String y;
    private String razonSocial;
    private Integer idEstadoCliente;
    private String descEstadoCliente;
    private Integer idGrupoEconomico;
    private String descGrupoEconomico;
    
    private Integer mail;
    private String usuario;
    private String clave;
    private String rnp;
    private String feAlta;
    private Integer idEstadoOsce;
    private String descEstadoOsce;
    private Integer idTramite;
    private String descTramite;
    
    private String tipoByS;
    private String tipoEyC;
    private String categoriaEyC;
    
    private String observacion;
}
