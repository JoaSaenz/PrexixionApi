package com.joa.prexixionapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bf3800_personas_titularidad")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCadenaTitularidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idBeneficiario;

    @Column(name = "nroOrdenPersonaCadenaTitularidad")
    private String nroOrden;

    @Column(name = "tipoPersonaCadenaTitularidad")
    private String tipoPersona;

    @Column(name = "nombresApellidosPersonaCadenaTitularidad")
    private String nombresApellidos;

    @Column(name = "perIntermediariaPersonaCadenaTitularidad")
    private String personaIntermediaria;

    @Column(name = "partIntermediariaPersonaCadenaTitularidad")
    private String participacionIntermediaria;

    @Column(name = "partIndirectaPersonaCadenaTitularidad")
    private String participacionIndirecta;

    @Column(name = "beneficiarioSiNoPersonaCadenaTitularidad")
    private String beneficiarioSiNo;

    @Column(name = "obsPersonaCadenaTitularidad")
    private String observacion;
}
