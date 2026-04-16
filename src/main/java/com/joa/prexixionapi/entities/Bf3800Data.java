package com.joa.prexixionapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

class Bf3800DataId implements Serializable {
    private String idCliente;
    private String anio;
    private String mes;
}

@Entity
@Table(name = "bf3800_data")
@IdClass(Bf3800DataId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bf3800Data {
    @Id
    private String idCliente;
    @Id
    private String anio;
    @Id
    private String mes;

    private String observacion;
    private Integer mail;
}
