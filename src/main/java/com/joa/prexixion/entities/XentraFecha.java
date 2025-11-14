package com.joa.prexixion.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "xentraFechas")
public class XentraFecha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación con XentraData
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idXentra", nullable = false)
    private XentraData xentra;

    private String fecha; // varchar(255)
    private Integer idEstado;
    private String fechaEstado; // varchar(10)
    private String horaEstado; // varchar(6)
    private String estadoLogico; // varchar(255)
}
