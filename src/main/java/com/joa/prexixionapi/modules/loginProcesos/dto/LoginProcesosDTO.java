package com.joa.prexixionapi.modules.loginProcesos.dto;

import com.joa.prexixionapi.common.dto.GclassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginProcesosDTO {

    // Identificadores & Control de concurrencia
    private String ruc;
    private String anio;
    private String mes;
    private Integer version;

    // Datos del contribuyente / Logística
    private String razonSocial;
    private String correos;
    private String movimiento;
    private String observacion;

    // Estados informativos de Ventas
    private Integer confirmacionVentas;
    private String confirmacionUsuarioVentas;
    private String confirmacionFechaVentas;

    // Estados informativos de Compras
    private Integer confirmacionCompras;
    private String confirmacionUsuarioCompras;
    private String confirmacionFechaCompras;

    // Propuestas de SIRE
    private Integer idPropuestaVentas;
    private GclassDTO propuestaVentas;
    private Integer idPropuestaCompras;
    private GclassDTO propuestaCompras;

    // Procesos Propios (Edición)
    private Integer preLiquidacion;
    private String preLiquidacionUsuario;
    private String preLiquidacionFecha;
    private String preLiquidacionHora;

    private Integer confirmacion;
    private String confirmacionUsuario;
    private String confirmacionFecha;
    private String confirmacionHora;

    // Opcional para compatibilidad con vistas RTB
    private String dniResponsable2RTB;
}
