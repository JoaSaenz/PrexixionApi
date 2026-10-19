package com.joa.prexixionapi.modules.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDTO {
    private String dni;
    private String nombreCompleto;
    private String area;
    private String puesto;
    private String avatarColor;
}
