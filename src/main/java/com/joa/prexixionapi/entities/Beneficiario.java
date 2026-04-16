package com.joa.prexixionapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "bf3800_beneficiarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String idCliente;
    private String anio;
    private String mes;
    private String fechaFormato;
    private String condicionBeneficiario;
    private String nombresBeneficiario;
    private String paisBeneficiario;
    private String fechaNacimientoBeneficiario;
    private String nacionalidadBeneficiario;
    private String tipoDocumentoBeneficiario;
    private String numeroDocumentoBeneficiario;
    private String nitBeneficiario;
    private String rucBeneficiario;
    private String estadoCivilBeneficiario;
    private String nombresConyugeBeneficiario;
    private String tipoDocumentoConyugeBeneficiario;
    private String numeroDocumentoConyugeBeneficiario;
    private String regimenPatrimonialBeneficiario;
    private String fechaRegimenPatrimonialBeneficiario;
    private String relacionPersonaJuridicaBeneficiario;
    private String correoElectronicoBeneficiario;
    private String numeroTelefonicoBeneficiario;
    private String direccionBeneficiario;
    private String numeroAccionesBeneficiario;
    private String participacionBeneficiario;
    private String valorUnitarioBeneficiario;
    private String tipoAccionesBeneficiario;
    private String fechaCiertaBeneficiario;
    private String lugarDepositoBeneficiario;
    private String beneficiarioIndirecto;
    private String persPropiedadIndirecta;
    private String nombrePersEnteJuridico;
    private String paisPersEnteJuridico;
    private String creacionPersEnteJuridico;
    private String constitucionPersEnteJuridico;
    private String registroPersEnteJuridico;
    private String paisResidenciaPersEnteJuridico;
    private String nitPersEnteJuridico;
    private String rucPersEnteJuridico;
    private String direccionPersEnteJuridico;
    private String beneficiarioControl;
    private String textoEnteJuridico;
    private String calidadEnteJuridico;
    private String fechaBeneficiarioEnteJuridico;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "idBeneficiario")
    private List<PersonaCapitalPPJJ> personaCapitalPPJJ;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "idBeneficiario")
    private List<PersonaCadenaTitularidad> personaCadenaTitularidad;
}
