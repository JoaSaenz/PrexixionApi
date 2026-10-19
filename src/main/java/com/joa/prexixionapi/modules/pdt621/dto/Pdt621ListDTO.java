package com.joa.prexixionapi.modules.pdt621.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDTO;

@Data
@Builder
public class Pdt621ListDTO {
    // ClienteDTO basic info
    private String ruc;
    private String razonSocial;
    private String nombreCorto;
    private String direccion;
    private String y;
    
    // Nivel / Signer
    private String abrCategoria;
    private Integer categoriaGrupoE; 
    private Integer categoriaStore; 
    private String descGrupoEconomico;

    // Estado / Servicios
    private String estado;
    private String tipoServicioAbr;

    // Fechas y Vencimiento
    private String anio;
    private String mes;
    private String fVencimientoGear;
    private String fVencimiento;
    
    // Status
    private String movimiento;
    private Integer confirmacion;
    private Integer sireCV;

    // Totales e Impuestos (Solo Visibles)
    private Double ventasG;
    private Double ventasNg;
    
    private Integer igvJusto;
    private Integer pago;
    private String descBancoPdt621;
    private Integer pagarImpuestos;
    
    private Integer renta3Categoria;
    private Integer rentaPago;
    private String descRentaBanco;
    private Integer rentaPagarImpuestos;

    // Registros
    private List<Pdt621RegistroDTO> registros;

    // Indicadores 
    private Integer mail;
    private Integer sus;
    private String observacion;
    private String notaDeDeclaracion;

    private Integer administracion;
    private String abrGestionRegimenTributario;
}
