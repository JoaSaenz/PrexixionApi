package com.joa.prexixionapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bf3800_registros")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bf3800Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String idCliente;
    private String anio;
    private String mes;
    private Integer idTipo;
    private String fecha;
    private String nroRectificacion;
    private String nroOrden;
}
