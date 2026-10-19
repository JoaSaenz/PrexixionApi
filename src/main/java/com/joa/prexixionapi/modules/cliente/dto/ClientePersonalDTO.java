package com.joa.prexixionapi.modules.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.cliente.dto.ClientePersonalDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePersonalDTO {
    private Integer idEstado;
    private String descEstado;
    private Integer idGrupoEconomico;
    private String descGrupoEconomico;
    private String ruc;
    private String y;
    private String razonSocial;
    private String descTipoCC;
    private String plDni;
    private String plApellido;
    private String plNombre;
    private String descPuesto;
    private String plTelefono;
    private String plCorreo;
    private String plFNacimiento;
    private String plTieneClaveSol;
    private Integer plAdministracion;
}
