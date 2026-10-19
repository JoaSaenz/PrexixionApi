package com.joa.prexixionapi.modules.xentra.dto;

import lombok.Data;

@Data
public class XentraFechaDTO {
    private Integer id;

    // Relación con XentraData
    private Integer idXentra;

    private String fecha; // varchar(255)
    private Integer idEstado;
    private String fechaEstado; // varchar(10)
    private String horaEstado; // varchar(6)
    private String estadoLogico; // varchar(255)
}
