package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDetailDTO {
    private String name;
    private String time;
    private String status; // e.g., "TARDE", "PUNTUAL"
}
