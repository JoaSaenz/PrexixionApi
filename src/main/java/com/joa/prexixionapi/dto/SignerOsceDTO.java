package com.joa.prexixionapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class SignerOsceDTO {
    private String idCliente;
    private String y;
    private String razonSocial;
    private Integer idEstado;
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
    private String observacion;
    
    private List<SignerOsceTipoDTO> listSignerOsceTipo;
}
