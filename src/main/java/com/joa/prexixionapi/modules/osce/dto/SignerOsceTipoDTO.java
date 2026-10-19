package com.joa.prexixionapi.modules.osce.dto;

import lombok.Data;

@Data
public class SignerOsceTipoDTO {
    private Integer id;
    private String idCliente;
    private String tipo;
    private Integer categoria;
    private String feAlta;
}
