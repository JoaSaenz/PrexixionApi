package com.joa.prexixionapi.modules.pdt621.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdt621DTO {

    // Cliente info
    private String ruc;
    private String razonSocial;
    private String direccion;
    private Integer afectoIvap;
    private String anio;
    private String mes;

    // Compensaciones, detracciones y aprox planilla
    private Double detracciones;
    private Double compensaciones;
    private Double aproxPlanilla;

    // Fechas de vencimiento
    private String fechaVencimiento;
    private String fechaVencimientoBC;
    private String fechaVencimientoPle;
    private String fechaVencimientoSire;

    private String fechaIgvJusto;
    private Integer mail;
    private Integer sus;

    private String fechaMail;
    private String horaMail;
    private String fechaDeclaracion;
    private String horaDeclaracion;

    // IGV Pago, Bancos y Pagar Impuestos
    private Integer igvJusto;
    private Integer pago;
    private Integer idBancoPdt621;
    private String descBancoPdt621;
    private Integer pagarImpuestos;

    // Renta Pago, Bancos y Pagar Impuestos
    private Integer renta3Categoria;
    private Integer rentaPago;
    private Integer rentaBanco;
    private String descRentaBanco;
    private Integer rentaPagarImpuestos;

    private String notaDeDeclaracion;
    private String observacion;
    private String observacionAnterior;

    // Arrastre de Ventas
    private Double saldoAnteriorArrastreVentas;
    private Double periodoArrastreVentas;
    private Double aplicacionArrastreVentas;
    private Double saldoFinalArrastreVentas;

    // Tasa de IGV
    private Double tasa;

    // Ventas
    private Double ventasG;
    private Double ventasIgv;
    private Double ventasNetas10;
    private Double ventasNetasIgv10;
    private Double ventasNg;
    private Integer ventasFilas;

    // Exportacion
    private Double expFactPer;
    private Double expEmbrPer;

    // Compras
    private Double comprasG;
    private Double comprasIgv;
    private Double comprasNetas10;
    private Integer operacionComprasNetasIgv10;
    private Double comprasNetasIgv10;
    private Double comprasMixtas;
    private Double comprasMixtasIgv;
    private Double comprasNgE;
    private Double comprasNg;
    private Double coeficiente;
    private Integer comprasFilas;

    // Importacion
    private Double impComprasG;
    private Double impComprasGigv;

    // IGV
    private Double mesIgv;
    private Double mesAnteriorIgv;
    private Double ajusteIgv;
    private Double resultadoIgv;

    // Percepciones
    private Double mesPer;
    private Double mesAnteriorPer;
    private String codigosTributariosCompensacionPer;
    private Double compensacionPer;
    private Double ajustePer;
    private Double resultadoPer;

    // Retenciones
    private Double mesRet;
    private Double mesAnteriorRet;
    private String codigosTributariosCompensacionRet;
    private Double compensacionRet;
    private Double ajusteRet;
    private Double resultadoRet;

    private Double igvPorPagar;

    // IVAP
    private Double ivapVentasGravadas;
    private Double ivapTributo;

    // Arrastre de Ingresos
    private Double saldoAnteriorArrastreIngresos;
    private Double periodoArrastreIngresos;
    private Double aplicacionArrastreIngresos;
    private Double saldoFinalArrastreIngresos;

    // Renta
    private String superaUit;
    private String tipoFactor;
    private Double impRtaFactorA;
    private Double ingresosFactorA;
    private Double ajusteFactorA;
    private Double coefA;
    private Double coefB;

    private Double baseRenta;
    private Double mesRenta;
    private Double mesAnteriorRenta;
    private Double anualRenta;
    private Double saldoFavorExportador;
    private Double creditoItanRenta;
    private String codigosTributariosAjusteRenta;
    private Double ajusteRenta;
    private Double totalDeudaTributariaRenta;
    private Double rentaPorPagar;

    // Renta mes anterior
    private String superaUitmesA;
    private String tipoFactormesA;
    private Double impRtaFactorAmesA;
    private Double ingresosFactorAmesA;
    private Double ajusteFactorAmesA;
    private Double coefAmesA;
    private Double coefBmesA;

    private Double sumatoriaBaseImponible;
    private Double sumatoriaBaseImponibleMasPeriodoActual;

    private Integer operacion;
    private Integer igvTasaEspecial10;

    // Regimen Tributario
    private Integer gestionRegimenTributarioMesAnterior;
    private Integer gestionRegimenTributario;
    private String abrGestionRegimenTributario;

    // Observaciones relacionadas
    private String obsVentasAnterior;
    private String obsVentas;
    private String obsComprasAnterior;
    private String obsCompras;

    // Registros / Rectificaciones
    @Builder.Default
    private List<Pdt621RegistroDTO> registros = new ArrayList<>();
}
