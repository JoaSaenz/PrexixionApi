package com.joa.prexixionapi.modules.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.joa.prexixionapi.modules.asistencia.dto.AsistenciaDetailDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaDetailDTO {
    private String name;
    private String time;
    private String status; // e.g., "TARDE", "PUNTUAL"
}
