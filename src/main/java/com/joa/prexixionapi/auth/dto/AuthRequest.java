package com.joa.prexixionapi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;
    
    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
