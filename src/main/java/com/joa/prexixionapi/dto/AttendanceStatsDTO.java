package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatsDTO {
    // Campos para el Dashboard Moderno
    private int presentCount;
    private int lateCount;
    private List<AttendanceDetailDTO> lateDetails;

    // Campos requeridos por AttendanceService (Originales)
    private List<PersonLatenessDTO> topTardones;
    private List<AreaLatenessDTO> areasTardonas;
    private int totalTardanzas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonLatenessDTO {
        private String dni;
        private String nombreCompleto;
        private String area;
        private int minutosTardanza;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaLatenessDTO {
        private String nombreArea;
        private int totalMinutos;
        private int cantidadPersonas;
    }
}
