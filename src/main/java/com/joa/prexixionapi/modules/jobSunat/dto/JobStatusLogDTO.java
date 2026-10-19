package com.joa.prexixionapi.modules.jobSunat.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobStatusLogDTO {
    private Long id;

    private String estadoCliente;

    private String razonSocial;

    private Integer idEstado;

    private String ruc;
    private String y;
    private String resultado; // OK, ERROR, CREDENCIALES_INVALIDAS, etc
    private String mensaje;
    private Long duracionMs;
    private LocalDateTime fechaRegistro;
    private Integer nuevasNotificaciones; // cantidad de nuevas notificaciones para ese RUC

    private JobStatusDTO jobStatus;
}
