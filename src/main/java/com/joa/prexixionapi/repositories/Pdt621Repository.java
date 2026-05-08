package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.dto.Pdt621RegistroDTO;
import com.joa.prexixionapi.dto.Pdt621ReportAnualDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class Pdt621Repository {

    @PersistenceContext
    private EntityManager em;

    public List<Pdt621ReportAnualDTO> listTaxReviewPDT621(String ruc, String anio) {
        String query = """
                SELECT t.idCliente, t.anio, t.mes, t.feDeclaracion, t.numeroRectificatorias, t.constancia,
                t.ventasGravadas18, t.comprasGravadas18, t.ventasGravadas10, t.comprasGravadas10,
                t.igvMes, t.igvAFavor, t.igvAjuste, t.igvResultado,
                t.percepcionesMes, t.percepcionesAFavor, t.percepcionesCompensaciones, t.percepcionesAjuste, t.percepcionesResultado,
                t.retencionesMes, t.retencionesAFavor, t.retencionesCompensaciones, t.retencionesAjuste, t.retencionesResultado,
                t.igvPorPagar,
                t.ventasFacturadas, t.ventasNoGravadas, t.comprasNoGravadas, t.comprasExoneradas,
                t.ivapVentasGravadas, t.ivapTributo,
                t.rentaCoeficienteA, t.rentaCoeficienteB,
                t.rentaIngreso, t.rentaMes, t.rentaAFavor, t.rentaAnual, t.saldoFavorExportador, t.rentaCItan, t.rentaAjuste, t.rentaSaldo, t.rentaPorPagar
                FROM taxReviewPDT621 t
                WHERE t.idCliente = :ruc AND t.anio = :anio
                """;
        
        Query q = em.createNativeQuery(query, Tuple.class);
        q.setParameter("ruc", ruc);
        q.setParameter("anio", anio);
        
        List<Tuple> results = q.getResultList();
        List<Pdt621ReportAnualDTO> list = new ArrayList<>();
        for (Tuple rs : results) {
            Pdt621ReportAnualDTO dto = Pdt621ReportAnualDTO.builder()
                .ruc(rs.get("idCliente", String.class))
                .anio(rs.get("anio", String.class))
                .mes(rs.get("mes", String.class))
                .ventasG(toDouble(rs.get("ventasGravadas18")))
                .comprasG(toDouble(rs.get("comprasGravadas18")))
                .ventasNetas10(toDouble(rs.get("ventasGravadas10")))
                .comprasNetas10(toDouble(rs.get("comprasGravadas10")))
                .mesIgv(toDouble(rs.get("igvMes")))
                .mesAnteriorIgv(toDouble(rs.get("igvAFavor")))
                .ajusteIgv(toDouble(rs.get("igvAjuste")))
                .resultadoIgv(toDouble(rs.get("igvResultado")))
                .mesPer(toDouble(rs.get("percepcionesMes")))
                .mesAnteriorPer(toDouble(rs.get("percepcionesAFavor")))
                .compensacionPer(toDouble(rs.get("percepcionesCompensaciones")))
                .ajustePer(toDouble(rs.get("percepcionesAjuste")))
                .resultadoPer(toDouble(rs.get("percepcionesResultado")))
                .mesRet(toDouble(rs.get("retencionesMes")))
                .mesAnteriorRet(toDouble(rs.get("retencionesAFavor")))
                .compensacionRet(toDouble(rs.get("retencionesCompensaciones")))
                .ajusteRet(toDouble(rs.get("retencionesAjuste")))
                .resultadoRet(toDouble(rs.get("retencionesResultado")))
                .igvPorPagar(toDouble(rs.get("igvPorPagar")))
                .expFactPer(toDouble(rs.get("ventasFacturadas")))
                .ventasNg(toDouble(rs.get("ventasNoGravadas")))
                .comprasNgE(toDouble(rs.get("comprasNoGravadas")))
                .comprasNg(toDouble(rs.get("comprasExoneradas")))
                .ivapVentasGravadas(toDouble(rs.get("ivapVentasGravadas")))
                .ivapTributo(toDouble(rs.get("ivapTributo")))
                .baseRenta(toDouble(rs.get("rentaIngreso")))
                .mesRenta(toDouble(rs.get("rentaMes")))
                .mesAnteriorRenta(toDouble(rs.get("rentaAFavor")))
                .anualRenta(toDouble(rs.get("rentaAnual")))
                .saldoFavorExportador(toDouble(rs.get("saldoFavorExportador")))
                .citanRenta(toDouble(rs.get("rentaCItan")))
                .ajusteRenta(toDouble(rs.get("rentaAjuste")))
                .totalDeudaTributariaRenta(toDouble(rs.get("rentaSaldo")))
                .rentaPorPagar(toDouble(rs.get("rentaPorPagar")))
                .tasa(18.0)
                .registros(rs.get("feDeclaracion") != null ? List.of(Pdt621RegistroDTO.builder()
                    .fecha(rs.get("feDeclaracion", String.class))
                    .nroRectificacion(String.valueOf(rs.get("numeroRectificatorias") == null ? "" : rs.get("numeroRectificatorias")))
                    .nroOrden(rs.get("constancia", String.class))
                    .build()) : new ArrayList<>())
                .build();
            list.add(dto);
        }
        return list;
    }

    public List<Pdt621ReportAnualDTO> listAnualPorAnio(String ruc, String anio) {
        String query = """
                SELECT p.idCliente, p.anio, p.mes,
                p.ventasG, p.ventasNetas10, p.comprasG, p.comprasNetas10,
                p.expFactPer, p.ventasNg, p.comprasNgE, p.comprasNg,
                p.mesIgv, p.mesAnteriorIgv, p.ajusteIgv, p.resultadoIgv,
                p.mesPer, p.mesAnteriorPer, p.compensacionPer, p.ajustePer, p.resultadoPer,
                p.mesRet, p.mesAnteriorRet, p.compensacionRet, p.ajusteRet, p.resultadoRet, p.igvPorPagar,
                p.ivapVentasGravadas, p.ivapTributo,
                p.baseRenta, p.mesRenta, p.mesAnteriorRenta, p.anualRenta, p.saldoFavorExportador, p.cItanRenta, p.ajusteRenta, p.totalDeudaTributariaRenta,
                p.rentaPorPagar,
                pR.fecha as fechaRegistro, pR.nroRectificacion, pR.nroOrden as nroOrdenRegistro,
                rt.abreviatura AS abrGestionRegimenTributario,
                i.valor AS tasa
                FROM pdt621DataNew p
                LEFT JOIN pdt621registros pR ON p.idCliente = pR.idCliente AND pR.anio = p.anio AND pR.mes = p.mes
                AND pR.id = (select MAX(id) from pdt621Registros x where x.idCliente = p.idCliente AND x.anio = :anio AND x.mes = p.mes AND x.idTipo IN (3,4,5) )
                LEFT JOIN gestionRegimenesTributarios grt ON p.idCliente = grt.idCliente AND p.anio = grt.anio AND p.mes = grt.mes
                LEFT JOIN regimenesTributarios rt ON grt.idRegimenTributario = rt.id
                LEFT JOIN igv i ON p.anio = i.anio AND p.mes = i.mes
                WHERE p.idCliente = :ruc AND p.anio = :anio
                """;
        
        Query q = em.createNativeQuery(query, Tuple.class);
        q.setParameter("ruc", ruc);
        q.setParameter("anio", anio);
        
        List<Tuple> results = q.getResultList();
        List<Pdt621ReportAnualDTO> list = new ArrayList<>();
        for (Tuple rs : results) {
            Pdt621ReportAnualDTO dto = Pdt621ReportAnualDTO.builder()
                .ruc(rs.get("idCliente", String.class))
                .anio(rs.get("anio", String.class))
                .mes(rs.get("mes", String.class))
                .ventasG(toDouble(rs.get("ventasG")))
                .comprasG(toDouble(rs.get("comprasG")))
                .ventasNetas10(toDouble(rs.get("ventasNetas10")))
                .comprasNetas10(toDouble(rs.get("comprasNetas10")))
                .expFactPer(toDouble(rs.get("expFactPer")))
                .ventasNg(toDouble(rs.get("ventasNg")))
                .comprasNgE(toDouble(rs.get("comprasNgE")))
                .comprasNg(toDouble(rs.get("comprasNg")))
                .mesIgv(toDouble(rs.get("mesIgv")))
                .mesAnteriorIgv(toDouble(rs.get("mesAnteriorIgv")))
                .ajusteIgv(toDouble(rs.get("ajusteIgv")))
                .resultadoIgv(toDouble(rs.get("resultadoIgv")))
                .mesPer(toDouble(rs.get("mesPer")))
                .mesAnteriorPer(toDouble(rs.get("mesAnteriorPer")))
                .compensacionPer(toDouble(rs.get("compensacionPer")))
                .ajustePer(toDouble(rs.get("ajustePer")))
                .resultadoPer(toDouble(rs.get("resultadoPer")))
                .mesRet(toDouble(rs.get("mesRet")))
                .mesAnteriorRet(toDouble(rs.get("mesAnteriorRet")))
                .compensacionRet(toDouble(rs.get("compensacionRet")))
                .ajusteRet(toDouble(rs.get("ajusteRet")))
                .resultadoRet(toDouble(rs.get("resultadoRet")))
                .igvPorPagar(toDouble(rs.get("igvPorPagar")))
                .ivapVentasGravadas(toDouble(rs.get("ivapVentasGravadas")))
                .ivapTributo(toDouble(rs.get("ivapTributo")))
                .baseRenta(toDouble(rs.get("baseRenta")))
                .mesRenta(toDouble(rs.get("mesRenta")))
                .mesAnteriorRenta(toDouble(rs.get("mesAnteriorRenta")))
                .anualRenta(toDouble(rs.get("anualRenta")))
                .saldoFavorExportador(toDouble(rs.get("saldoFavorExportador")))
                .citanRenta(toDouble(rs.get("cItanRenta")))
                .ajusteRenta(toDouble(rs.get("ajusteRenta")))
                .totalDeudaTributariaRenta(toDouble(rs.get("totalDeudaTributariaRenta")))
                .rentaPorPagar(toDouble(rs.get("rentaPorPagar")))
                .tasa(toDouble(rs.get("tasa")))
                .registros(rs.get("fechaRegistro") != null ? List.of(Pdt621RegistroDTO.builder()
                    .fecha(rs.get("fechaRegistro", String.class))
                    .nroRectificacion(rs.get("nroRectificacion", String.class))
                    .nroOrden(rs.get("nroOrdenRegistro", String.class))
                    .build()) : new ArrayList<>())
                .abrGestionRegimenTributario(rs.get("abrGestionRegimenTributario", String.class))
                .build();
            list.add(dto);
        }
        return list;
    }

    private double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        return 0.0;
    }
}
