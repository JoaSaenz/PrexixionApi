package com.joa.prexixionapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bf3800_personas_capital")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCapitalPPJJ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idBeneficiario;
    
    @Column(name = "nroOrdenPersonaCapitalPPJJ")
    private String nroOrden;
    
    @Column(name = "fCiertaPersonaCapitalPPJJ")
    private String fechaCierta;
    
    @Column(name = "nombresApellidosPersonaCapitalPPJJ")
    private String nombresApellidos;
    
    @Column(name = "valorNominalPersonaCapitalPPJJ")
    private String valorNominal;
    
    @Column(name = "partDirectaPersonaCapitalPPJJ")
    private String participacionDirecta;
    
    @Column(name = "beneficiarioSiNoPersonaCapitalPPJJ")
    private String beneficiarioSiNo;
    
    @Column(name = "obsPersonaCapitalPPJJ")
    private String observacion;
}
