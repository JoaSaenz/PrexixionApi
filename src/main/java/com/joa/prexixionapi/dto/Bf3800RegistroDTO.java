package com.joa.prexixionapi.dto;

import com.joa.prexixionapi.entities.Gclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bf3800RegistroDTO {
    private int id;
    private Gclass tipo;
    private String fecha;
    private String nroRectificacion;
    private String nroOrden;
}
