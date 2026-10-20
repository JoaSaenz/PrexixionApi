package com.joa.prexixionapi.modules.pdt621.repositories;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621RegistroDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ReportAnualDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621ListDTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621FilterRequestDTO;
import com.joa.prexixionapi.common.utils.QueryUtils;
import com.joa.prexixionapi.utils.DateUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621DTO;
import com.joa.prexixionapi.modules.pdt621.dto.Pdt621HistorialDTO;
import com.joa.prexixionapi.common.dto.GclassDTO;

@Repository
public class Pdt621Repository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Pdt621Repository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Pdt621ReportAnualDTO> listTaxReviewPDT621(String ruc, String anio) {
        String sql = """
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

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruc", ruc);
        params.addValue("anio", anio);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> Pdt621ReportAnualDTO.builder()
                .ruc(rs.getString("idCliente"))
                .anio(rs.getString("anio"))
                .mes(rs.getString("mes"))
                .ventasG(toDouble(rs.getObject("ventasGravadas18")))
                .comprasG(toDouble(rs.getObject("comprasGravadas18")))
                .ventasNetas10(toDouble(rs.getObject("ventasGravadas10")))
                .comprasNetas10(toDouble(rs.getObject("comprasGravadas10")))
                .mesIgv(toDouble(rs.getObject("igvMes")))
                .mesAnteriorIgv(toDouble(rs.getObject("igvAFavor")))
                .ajusteIgv(toDouble(rs.getObject("igvAjuste")))
                .resultadoIgv(toDouble(rs.getObject("igvResultado")))
                .mesPer(toDouble(rs.getObject("percepcionesMes")))
                .mesAnteriorPer(toDouble(rs.getObject("percepcionesAFavor")))
                .compensacionPer(toDouble(rs.getObject("percepcionesCompensaciones")))
                .ajustePer(toDouble(rs.getObject("percepcionesAjuste")))
                .resultadoPer(toDouble(rs.getObject("percepcionesResultado")))
                .mesRet(toDouble(rs.getObject("retencionesMes")))
                .mesAnteriorRet(toDouble(rs.getObject("retencionesAFavor")))
                .compensacionRet(toDouble(rs.getObject("retencionesCompensaciones")))
                .ajusteRet(toDouble(rs.getObject("retencionesAjuste")))
                .resultadoRet(toDouble(rs.getObject("retencionesResultado")))
                .igvPorPagar(toDouble(rs.getObject("igvPorPagar")))
                .expFactPer(toDouble(rs.getObject("ventasFacturadas")))
                .ventasNg(toDouble(rs.getObject("ventasNoGravadas")))
                .comprasNgE(toDouble(rs.getObject("comprasNoGravadas")))
                .comprasNg(toDouble(rs.getObject("comprasExoneradas")))
                .ivapVentasGravadas(toDouble(rs.getObject("ivapVentasGravadas")))
                .ivapTributo(toDouble(rs.getObject("ivapTributo")))
                .baseRenta(toDouble(rs.getObject("rentaIngreso")))
                .mesRenta(toDouble(rs.getObject("rentaMes")))
                .mesAnteriorRenta(toDouble(rs.getObject("rentaAFavor")))
                .anualRenta(toDouble(rs.getObject("rentaAnual")))
                .saldoFavorExportador(toDouble(rs.getObject("saldoFavorExportador")))
                .creditoItanRenta(toDouble(rs.getObject("rentaCItan")))
                .ajusteRenta(toDouble(rs.getObject("rentaAjuste")))
                .totalDeudaTributariaRenta(toDouble(rs.getObject("rentaSaldo")))
                .rentaPorPagar(toDouble(rs.getObject("rentaPorPagar")))
                .tasa(18.0)
                .registros(rs.getString("feDeclaracion") != null ? List.of(Pdt621RegistroDTO.builder()
                        .fecha(rs.getString("feDeclaracion"))
                        .nroRectificacion(String.valueOf(
                                rs.getObject("numeroRectificatorias") == null ? "" : rs.getObject("numeroRectificatorias")))
                        .nroOrden(rs.getString("constancia"))
                        .build()) : new ArrayList<>())
                .build());
    }

    public List<Pdt621ReportAnualDTO> listAnualPorAnio(String ruc, String anio) {
        String sql = """
                SELECT p.idCliente, p.anio, p.mes,
                p.ventasG, p.ventasNetas10, p.comprasG, p.comprasNetas10,
                p.expFactPer, p.ventasNg, p.comprasNgE, p.comprasNg,
                p.mesIgv, p.mesAnteriorIgv, p.ajusteIgv, p.resultadoIgv,
                p.mesPer, p.mesAnteriorPer, p.compensacionPer, p.ajustePer, p.resultadoPer, p.codigosTributariosCompensacionPer,
                p.mesRet, p.mesAnteriorRet, p.compensacionRet, p.ajusteRet, p.resultadoRet, p.codigosTributariosCompensacionRet, p.igvPorPagar,
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

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruc", ruc);
        params.addValue("anio", anio);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> Pdt621ReportAnualDTO.builder()
                .ruc(rs.getString("idCliente"))
                .anio(rs.getString("anio"))
                .mes(rs.getString("mes"))
                .ventasG(toDouble(rs.getObject("ventasG")))
                .comprasG(toDouble(rs.getObject("comprasG")))
                .ventasNetas10(toDouble(rs.getObject("ventasNetas10")))
                .comprasNetas10(toDouble(rs.getObject("comprasNetas10")))
                .expFactPer(toDouble(rs.getObject("expFactPer")))
                .ventasNg(toDouble(rs.getObject("ventasNg")))
                .comprasNgE(toDouble(rs.getObject("comprasNgE")))
                .comprasNg(toDouble(rs.getObject("comprasNg")))
                .mesIgv(toDouble(rs.getObject("mesIgv")))
                .mesAnteriorIgv(toDouble(rs.getObject("mesAnteriorIgv")))
                .ajusteIgv(toDouble(rs.getObject("ajusteIgv")))
                .resultadoIgv(toDouble(rs.getObject("resultadoIgv")))
                .mesPer(toDouble(rs.getObject("mesPer")))
                .mesAnteriorPer(toDouble(rs.getObject("mesAnteriorPer")))
                .compensacionPer(toDouble(rs.getObject("compensacionPer")))
                .ajustePer(toDouble(rs.getObject("ajustePer")))
                .resultadoPer(toDouble(rs.getObject("resultadoPer")))
                .codigosTributariosCompensacionPer(rs.getString("codigosTributariosCompensacionPer"))
                .mesRet(toDouble(rs.getObject("mesRet")))
                .mesAnteriorRet(toDouble(rs.getObject("mesAnteriorRet")))
                .compensacionRet(toDouble(rs.getObject("compensacionRet")))
                .ajusteRet(toDouble(rs.getObject("ajusteRet")))
                .resultadoRet(toDouble(rs.getObject("resultadoRet")))
                .codigosTributariosCompensacionRet(rs.getString("codigosTributariosCompensacionRet"))
                .igvPorPagar(toDouble(rs.getObject("igvPorPagar")))
                .ivapVentasGravadas(toDouble(rs.getObject("ivapVentasGravadas")))
                .ivapTributo(toDouble(rs.getObject("ivapTributo")))
                .baseRenta(toDouble(rs.getObject("baseRenta")))
                .mesRenta(toDouble(rs.getObject("mesRenta")))
                .mesAnteriorRenta(toDouble(rs.getObject("mesAnteriorRenta")))
                .anualRenta(toDouble(rs.getObject("anualRenta")))
                .saldoFavorExportador(toDouble(rs.getObject("saldoFavorExportador")))
                .creditoItanRenta(toDouble(rs.getObject("cItanRenta")))
                .ajusteRenta(toDouble(rs.getObject("ajusteRenta")))
                .totalDeudaTributariaRenta(toDouble(rs.getObject("totalDeudaTributariaRenta")))
                .rentaPorPagar(toDouble(rs.getObject("rentaPorPagar")))
                .tasa(toDouble(rs.getObject("tasa")))
                .registros(rs.getString("fechaRegistro") != null ? List.of(Pdt621RegistroDTO.builder()
                        .fecha(rs.getString("fechaRegistro"))
                        .nroRectificacion(rs.getString("nroRectificacion"))
                        .nroOrden(rs.getString("nroOrdenRegistro"))
                        .build()) : new ArrayList<>())
                .abrGestionRegimenTributario(rs.getString("abrGestionRegimenTributario"))
                .build());
    }

    private double toDouble(Object o) {
        if (o == null)
            return 0.0;
        if (o instanceof Number)
            return ((Number) o).doubleValue();
        return 0.0;
    }

    public List<Pdt621ListDTO> listByFilters(String anio, String mes, Pdt621FilterRequestDTO req) {
        int lastAnio = Integer.parseInt(anio) - 1;

        StringBuilder sql = new StringBuilder("""
            SELECT c.ruc, c.razonSocial, c.nombreCorto, c.direccion, c.y,
            nc.abreviatura as abrCategoria, 
            CASE WHEN c.idGrupoEconomico IS NOT NULL AND c.idGrupoEconomico NOT IN (0, 1) THEN 1 ELSE 0 END AS categoriaGrupoE,
            CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END AS categoriaStore, 
            ge.descripcion as descGrupoEconomico, 
            ce.descripcion as estado, 
            tS.abreviatura as tipoServicioAbr, 
            lp.movimiento, lp.confirmacion, 
            CASE WHEN (:anioMes >= 202002) AND (:anioMes <= 202008) 
                 THEN 
                    CASE 
                         WHEN (:anioMes = 202002) 
                            THEN 
                                CASE WHEN (c.y = '0') OR ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = :lastAnio) >= (u.valor * 2300)) 
                                     THEN 
                                        CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                                             WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                                             WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                                             WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
                                        END 
                                     ELSE 
                                        CASE WHEN c.fPrico = '' 
                                             THEN 
                                                CASE WHEN c.y = '0' THEN crC.fecha0 WHEN c.y = '1' THEN crC.fecha1 WHEN c.y = '2' THEN crC.fecha2 
                                                     WHEN c.y = '3' THEN crC.fecha3 WHEN c.y = '4' THEN crC.fecha4 WHEN c.y = '5' THEN crC.fecha5 
                                                     WHEN c.y = '6' THEN crC.fecha6 WHEN c.y = '7' THEN crC.fecha7 WHEN c.y = '8' THEN crC.fecha8 
                                                     WHEN c.y = '9' THEN crC.fecha9 
                                                END 
                                             ELSE 
                                                CASE WHEN c.y = '0' THEN crPricoPDT.fecha0 WHEN c.y = '1' THEN crPricoPDT.fecha1 WHEN c.y = '2' THEN crPricoPDT.fecha2 
                                                     WHEN c.y = '3' THEN crPricoPDT.fecha3 WHEN c.y = '4' THEN crPricoPDT.fecha4 WHEN c.y = '5' THEN crPricoPDT.fecha5 
                                                     WHEN c.y = '6' THEN crPricoPDT.fecha6 WHEN c.y = '7' THEN crPricoPDT.fecha7 WHEN c.y = '8' THEN crPricoPDT.fecha8 
                                                     WHEN c.y = '9' THEN crPricoPDT.fecha9 
                                                END 
                                        END 
                                END 
                         WHEN (:anioMes > 202002) 
                             THEN 
                             CASE WHEN ((SELECT SUM(ventasG)+SUM(ventasNg) FROM pdt621DataNew pdt WHERE pdt.idCliente = c.ruc AND pdt.anio = :lastAnio) >= (u.valor * 5000)) 
                                  THEN 
                                     CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                                          WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                                          WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                                          WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
                                     END 
                                  ELSE 
                                        CASE WHEN c.fPrico = '' 
                                             THEN 
                                                CASE WHEN c.y = '0' THEN crC.fecha0 WHEN c.y = '1' THEN crC.fecha1 WHEN c.y = '2' THEN crC.fecha2 
                                                     WHEN c.y = '3' THEN crC.fecha3 WHEN c.y = '4' THEN crC.fecha4 WHEN c.y = '5' THEN crC.fecha5 
                                                     WHEN c.y = '6' THEN crC.fecha6 WHEN c.y = '7' THEN crC.fecha7 WHEN c.y = '8' THEN crC.fecha8 
                                                     WHEN c.y = '9' THEN crC.fecha9 
                                                END 
                                             ELSE 
                                                CASE WHEN c.y = '0' THEN crPricoPDT.fecha0 WHEN c.y = '1' THEN crPricoPDT.fecha1 WHEN c.y = '2' THEN crPricoPDT.fecha2 
                                                     WHEN c.y = '3' THEN crPricoPDT.fecha3 WHEN c.y = '4' THEN crPricoPDT.fecha4 WHEN c.y = '5' THEN crPricoPDT.fecha5 
                                                     WHEN c.y = '6' THEN crPricoPDT.fecha6 WHEN c.y = '7' THEN crPricoPDT.fecha7 WHEN c.y = '8' THEN crPricoPDT.fecha8 
                                                     WHEN c.y = '9' THEN crPricoPDT.fecha9 
                                                END 
                                        END 
                             END 
                    END 
                 ELSE 
                    CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                         WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                         WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                         WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
                    END 
             END AS fechaVencimiento,
            CASE WHEN si.idCliente IS NULL THEN 2 ELSE COALESCE(siData.registrado, 0) END AS sireCV, 
             t.ventasG, t.ventasNg,
             t.expFactPer, t.comprasG, t.comprasNgE, t.comprasNg,
             t.igvPorPagar, t.resultadoIgv, t.rentaPorPagar, t.totalDeudaTributariaRenta,
             CASE WHEN pleSt.idCliente IS NOT NULL THEN 'SI' ELSE 'NO' END AS ple,
             CASE WHEN si.idCliente IS NOT NULL THEN 'SI' ELSE 'NO' END AS rucSire,
             CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 
                  WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 
                  WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 
                  WHEN c.y = '9' THEN crPle.fecha9 
             END AS fVencimientoPle,
             CASE WHEN c.y = '0' THEN crS.fecha0 WHEN c.y = '1' THEN crS.fecha1 WHEN c.y = '2' THEN crS.fecha2 
                  WHEN c.y = '3' THEN crS.fecha3 WHEN c.y = '4' THEN crS.fecha4 WHEN c.y = '5' THEN crS.fecha5 
                  WHEN c.y = '6' THEN crS.fecha6 WHEN c.y = '7' THEN crS.fecha7 WHEN c.y = '8' THEN crS.fecha8 
                  WHEN c.y = '9' THEN crS.fecha9 
             END AS fVencimientoSire,
             t.igvJusto, t.pago, pb.descripcion as descBancoPdt621, t.pagarImpuestos, 
             t.renta3Categoria, t.rentaPago, pbRenta.descripcion AS descRentaBanco, t.rentaPagarImpuestos,
             pR.idTipo as idTipoRegistro, pR.fecha as fechaRegistro, pR.nroOrden as nroOrdenRegistro, 
             t.mail, t.sus, t.observacion, t.notaDeDeclaracion, 
             l.administracion, 
             rt.abreviatura AS abrGestionRegimenTributario 
             FROM cliente c 
             LEFT JOIN clientsTipoServicio tS ON c.idTipoServicio = tS.id 
             LEFT JOIN signerNiveles s ON c.ruc = s.idCliente 
             LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id 
             LEFT JOIN ( 
                 SELECT DISTINCT idCliente 
                 FROM clienteServiciosOtros 
                 WHERE soIdServicioOtro = 6 
             ) cso ON c.ruc = cso.idCliente 
             LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id 
             LEFT JOIN cronogramaPDT cr ON :anio = cr.anio AND :mes = cr.mes 
             LEFT JOIN cronogramaCovidPDT crC ON :anio = crC.anio AND :mes = crC.mes 
             LEFT JOIN cronogramaPricoPDT crPricoPDT ON :anio = crPricoPDT.anio AND :mes = crPricoPDT.mes 
             LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = :anio AND crPle.mes = :mes
             LEFT JOIN cronogramaSire crS ON crS.anio = :anio AND crS.mes = :mes
             LEFT JOIN uit u ON u.anio = :anio 
             INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 
             LEFT JOIN clienteServiciosTributarios pleSt ON c.ruc = pleSt.idCliente AND pleSt.stIdServicioTributario = 1 
             AND CAST(pleSt.stAnioDesde + pleSt.stMesDesde + '01' as date) <= CAST(:anio + '-' + :mes + '-01' as date) AND 
             ( CAST(pleSt.stAnioHasta + pleSt.stMesHasta + '01' as date) >= CAST(:anio + '-' + :mes + '-01' as date) OR pleSt.stAnioHasta IS NULL )
             LEFT JOIN pdt621DataNew t ON c.ruc = t.idCliente AND :anio = t.anio AND :mes = t.mes 
             LEFT JOIN loginProcesos lp ON c.ruc = lp.ruc AND lp.anio = :anio AND lp.mes = :mes 
             INNER JOIN clientsEstados ce on c.idEstado = ce.id  
             LEFT JOIN Pdt621Bancos pb ON t.idBancoPdt621 = pb.id
             LEFT JOIN Pdt621Bancos pbRenta ON t.rentaBanco = pbRenta.id
             LEFT JOIN pdt621registros pR ON c.ruc = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes 
              AND pR.id = (select MAX(id) from pdt621Registros x where x.idCliente = c.ruc AND x.anio = :anio AND x.mes = :mes AND x.idTipo IN (3,4,5) )  
             LEFT JOIN clienteServiciosTributarios si ON c.ruc = si.idCliente AND si.stIdServicioTributario = 15 
             AND CAST(si.stAnioDesde + si.stMesDesde + '01' as date) <= CAST(:anio + '-' + :mes + '-01' as date) AND 
             ( CAST(si.stAnioHasta + si.stMesHasta + '01' as date) >= CAST(:anio + '-' + :mes + '-01' as date) OR si.stAnioHasta IS NULL ) 
             LEFT JOIN sireData siData ON c.ruc = siData.idCliente AND siData.anio = :anio AND siData.mes = :mes 
             LEFT JOIN loginBalances l ON c.ruc = l.ruc and l.anio =  '2023' AND l.mes = '13'
             LEFT JOIN gestionRegimenesTributarios grt ON c.ruc = grt.idCliente AND grt.anio = :anio AND grt.mes = :mes 
             LEFT JOIN regimenesTributarios rt ON grt.idRegimenTributario = rt.id 
             WHERE 
             CAST(p.stAnioDesde + p.stMesDesde + '01' as date) <= CAST(:anio + '-' + :mes + '-01' as date) AND 
             ( CAST(p.stAnioHasta + p.stMesHasta + '01' as date) >= CAST(:anio + '-' + :mes + '-01' as date) OR p.stAnioHasta IS NULL ) 
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("anio", anio);
        params.addValue("mes", mes);
        params.addValue("lastAnio", String.valueOf(lastAnio));
        params.addValue("anioMes", Integer.parseInt(anio + mes));

        if (req != null) {
            QueryUtils.appendIntFilter(sql, params, "c.idEstado", req.getEstadoFiltro(), "estados");
            QueryUtils.appendStringFilter(sql, params, "c.y", req.getGrupoFiltro(), "grupos");
            QueryUtils.appendIntFilter(sql, params, "s.idCategoria", req.getCategoriaFiltro(), "categorias");
            QueryUtils.appendIntFilter(sql, params, "(CASE WHEN c.idGrupoEconomico IS NOT NULL AND c.idGrupoEconomico NOT IN (0, 1) THEN 1 ELSE 0 END)", req.getCategoriaGrupoEFiltro(), "categoriaGrupoE");
            QueryUtils.appendIntFilter(sql, params, "(CASE WHEN cso.idCliente IS NOT NULL THEN 1 ELSE 0 END)", req.getCategoriaStoreFiltro(), "categoriaStore");
            QueryUtils.appendIntFilter(sql, params, "c.idGrupoEconomico", req.getGrupoEconomicoFiltro(), "grupoEconomico");

            if (req.getSireFiltro() != null && !req.getSireFiltro().trim().isEmpty()) {
                List<String> sireVals = Arrays.stream(req.getSireFiltro().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                if (sireVals.contains("1") && !sireVals.contains("2")) {
                    sql.append(" AND si.idCliente IS NOT NULL");
                } else if (sireVals.contains("2") && !sireVals.contains("1")) {
                    sql.append(" AND si.idCliente IS NULL");
                }
            }

            QueryUtils.appendIntFilter(sql, params, "(CASE WHEN si.idCliente IS NULL THEN 2 ELSE COALESCE(siData.registrado, 0) END)", req.getSireCvFiltro(), "sireCv");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(lp.confirmacion, 0)", req.getConfirmacionFiltro(), "confirmacion");

            if (req.getPdtFiltro() != null && !req.getPdtFiltro().trim().isEmpty()) {
                List<String> list = Arrays.stream(req.getPdtFiltro().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                if (list.contains("1") && !list.contains("0")) {
                    sql.append(" AND (pR.idTipo IS NOT NULL AND pR.nroOrden IS NOT NULL AND TRIM(pR.nroOrden) <> '')");
                } else if (list.contains("0") && !list.contains("1")) {
                    sql.append(" AND (pR.idTipo IS NULL OR pR.nroOrden IS NULL OR TRIM(pR.nroOrden) = '')");
                }
            }

            QueryUtils.appendIntFilter(sql, params, "COALESCE(t.mail, 0)", req.getMailFiltro(), "mail");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(t.sus, 0)", req.getSusFiltro(), "sus");
            QueryUtils.appendIntFilter(sql, params, "COALESCE(t.pagarImpuestos, 0)", req.getPagarImpuestosFiltro(), "pagarImpuestos");

            if (req.getRuc() != null && !req.getRuc().trim().isEmpty()) {
                sql.append(" AND c.ruc = :ruc");
                params.addValue("ruc", req.getRuc().trim());
            }
        }

        sql.append(" ORDER BY c.y");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            Pdt621ListDTO dto = Pdt621ListDTO.builder()
                .ruc(rs.getString("ruc"))
                .razonSocial(rs.getString("razonSocial"))
                .nombreCorto(rs.getString("nombreCorto"))
                .direccion(rs.getString("direccion"))
                .y(rs.getString("y"))
                .abrCategoria(rs.getString("abrCategoria"))
                .categoriaGrupoE(rs.getInt("categoriaGrupoE"))
                .categoriaStore(rs.getInt("categoriaStore"))
                .descGrupoEconomico(rs.getString("descGrupoEconomico"))
                .estado(rs.getString("estado"))
                .tipoServicioAbr(rs.getString("tipoServicioAbr"))
                .movimiento(rs.getString("movimiento"))
                .confirmacion(rs.getInt("confirmacion"))
                .anio(anio)
                .mes(mes)
                .fechaVencimiento(rs.getString("fechaVencimiento"))
                .difference((int) DateUtils.getDifferenceInDays(rs.getString("fechaVencimiento")))
                .sireCV(rs.getInt("sireCV"))
                .mail(rs.getInt("mail"))
                .sus(rs.getInt("sus"))
                .pagarImpuestos(rs.getInt("pagarImpuestos"))
                .ventasG(toDouble(rs.getObject("ventasG")))
                .ventasNg(toDouble(rs.getObject("ventasNg")))
                .expFactPer(toDouble(rs.getObject("expFactPer")))
                .comprasG(toDouble(rs.getObject("comprasG")))
                .comprasNgE(toDouble(rs.getObject("comprasNgE")))
                .comprasNg(toDouble(rs.getObject("comprasNg")))
                .igvPorPagar(toDouble(rs.getObject("igvPorPagar")))
                .resultadoIgv(toDouble(rs.getObject("resultadoIgv")))
                .rentaPorPagar(toDouble(rs.getObject("rentaPorPagar")))
                .totalDeudaTributariaRenta(toDouble(rs.getObject("totalDeudaTributariaRenta")))
                .fechaVencimientoPle(rs.getString("fVencimientoPle"))
                .fechaVencimientoSire(rs.getString("fVencimientoSire"))
                .ple(rs.getString("ple"))
                .rucSire(rs.getString("rucSire"))
                .abrGestionRegimenTributario(rs.getString("abrGestionRegimenTributario"))
                .notaDeDeclaracion(rs.getString("notaDeDeclaracion") == null ? "" : rs.getString("notaDeDeclaracion"))
                .observacion(rs.getString("observacion"))
                .igvJusto(rs.getInt("igvJusto"))
                .pago(rs.getInt("pago"))
                .descBancoPdt621(rs.getString("descBancoPdt621"))
                .renta3Categoria(rs.getInt("renta3Categoria"))
                .rentaPago(rs.getInt("rentaPago"))
                .descRentaBanco(rs.getString("descRentaBanco"))
                .rentaPagarImpuestos(rs.getInt("rentaPagarImpuestos"))
                .administracion(rs.getString("ruc") == null ? 1 : rs.getInt("administracion"))
                .build();
            
            if (rs.getInt("idTipoRegistro") != 0) {
                dto.setRegistros(List.of(
                    Pdt621RegistroDTO.builder()
                        .tipo(new GclassDTO(rs.getInt("idTipoRegistro")))
                        .fecha(rs.getString("fechaRegistro"))
                        .nroOrden(rs.getString("nroOrdenRegistro"))
                        .build()
                ));
            } else {
                dto.setRegistros(java.util.Collections.emptyList());
            }

            return dto;
        });
    }

    private Integer toInteger(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public Pdt621DTO getOne(String ruc, String anio, String mes) {
        int a = Integer.parseInt(anio);
        int m = Integer.parseInt(mes);
        String anioAux = (m == 1) ? String.valueOf(a - 1) : String.valueOf(a);
        String mesAux = (m == 1) ? "12" : (m - 1 < 10 ? "0" + (m - 1) : String.valueOf(m - 1));

        String sql = """
            SELECT c.ruc, c.razonSocial, c.direccion, c.afectoIvap,
            t.detracciones, t.compensaciones, t.aproxPlanilla,
            t.fIgvJusto, t.mail, t.fMail, t.hMail, t.sus, t.fDeclaracion, t.hDeclaracion,
            t.igvJusto, t.pago, t.idBancoPdt621, pb.descripcion as descBancoPdt621, t.pagarImpuestos,
            t.renta3Categoria, t.rentaPago, t.rentaBanco, pbRenta.descripcion as descRentaBanco, t.rentaPagarImpuestos,
            t.notaDeDeclaracion, t.observacion,
            COALESCE((SELECT aux.saldoFinalArrastreVentas FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux), t.saldoAnteriorArrastreVentas) AS saldoAnteriorArrastreVentas,
            t.periodoArrastreVentas, t.aplicacionArrastreVentas, t.saldoFinalArrastreVentas,
            COALESCE(i.valor, 0.18) as valor,
            t.ventasG, t.ventasNetas10, t.ventasNg,
            t.expFactPer, t.expEmbrPer,
            t.comprasG, t.comprasNetas10, t.operacionComprasNetasIgv10, t.comprasNetasIgv10, t.comprasMixtas, t.comprasNgE,
            t.impComprasG, t.impComprasGigv,
            t.comprasNg, t.coeficiente,
            COALESCE(sd.ventasFilas, 0) as ventasFilas, COALESCE(sd.comprasFilas, 0) as comprasFilas,
            t.mesIgv,
            COALESCE((SELECT CASE WHEN aux.resultadoIgv < 0 THEN aux.resultadoIgv ELSE 0 END FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux), t.mesAnteriorIgv) AS mesAnteriorIgv,
            t.ajusteIgv, t.resultadoIgv,
            t.mesPer,
            COALESCE((SELECT CASE WHEN aux.resultadoPer < 0 THEN aux.resultadoPer ELSE 0 END FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux), t.mesAnteriorPer) AS mesAnteriorPer,
            t.codigosTributariosCompensacionPer, t.compensacionPer, t.ajustePer, t.resultadoPer,
            t.mesRet,
            COALESCE((SELECT CASE WHEN aux.resultadoRet < 0 THEN aux.resultadoRet ELSE 0 END FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux), t.mesAnteriorRet) AS mesAnteriorRet,
            t.codigosTributariosCompensacionRet, t.compensacionRet, t.ajusteRet, t.resultadoRet,
            t.igvPorPagar, t.ivapVentasGravadas, t.ivapTributo,
            COALESCE((SELECT aux.saldoFinalArrastreIngresos FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux), t.saldoAnteriorArrastreIngresos) AS saldoAnteriorArrastreIngresos,
            t.periodoArrastreIngresos, t.aplicacionArrastreIngresos, t.saldoFinalArrastreIngresos,
            t.superaUit, t.tipoFactor, t.impRtaFactorA, t.ingresosFactorA, t.ajusteFactorA, t.coefA, t.coefB,
            t.baseRenta, t.mesRenta,
            COALESCE((SELECT CASE WHEN aux.totalDeudaTributariaRenta < 0 THEN aux.totalDeudaTributariaRenta ELSE 0 END FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux), t.mesAnteriorRenta) AS mesAnteriorRenta,
            t.anualRenta, t.saldoFavorExportador, t.cItanRenta, t.codigosTributariosAjusteRenta, t.ajusteRenta, t.totalDeudaTributariaRenta, t.rentaPorPagar, t.operacion,
            (SELECT aux.superaUit FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS superaUitmesA,
            (SELECT aux.tipoFactor FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS tipoFactormesA,
            (SELECT aux.impRtaFactorA FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS impRtaFactorAmesA,
            (SELECT aux.ingresosFactorA FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS ingresosFactorAmesA,
            (SELECT aux.ajusteFactorA FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS ajusteFactorAmesA,
            (SELECT aux.coefA FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS coefAmesA,
            (SELECT aux.coefB FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS coefBmesA,
            COALESCE((
                SELECT SUM(COALESCE(x.baseRenta,0)) FROM (
                    SELECT anio, mes, idCliente, baseRenta FROM pdt621DataNew WHERE anio = :anio AND mes < :mes AND idCliente = :ruc
                    UNION ALL
                    SELECT anio, mes, idCliente, rentaIngreso AS baseRenta FROM taxReviewPDT621 t WHERE anio = :anio AND mes < :mes AND idCliente = :ruc
                    AND NOT EXISTS (SELECT 1 FROM pdt621DataNew p WHERE p.idCliente = t.idCliente AND p.anio = t.anio AND p.mes = t.mes)
                ) x
            ), 0.0) AS sumatoriaBaseImponible,
            (SELECT aux.observacion FROM pdt621DataNew aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS observacionAnterior,
            grtA.idRegimenTributario AS gestionRegimenTributarioMesAnterior,
            COALESCE(grt.idRegimenTributario, grtA.idRegimenTributario) AS gestionRegimenTributario,
            rt.abreviatura AS abrGestionRegimenTributario,
            t.igvTasaEspecial10,
            (SELECT aux.observacion FROM loginVentas aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS obsVentasAnterior,
            (SELECT aux.observacion FROM loginVentas aux WHERE aux.idCliente = c.ruc AND aux.anio = :anio AND aux.mes = :mes) AS obsVentas,
            (SELECT aux.observacion FROM loginCompras aux WHERE aux.idCliente = c.ruc AND aux.anio = :anioAux AND aux.mes = :mesAux) AS obsComprasAnterior,
            (SELECT aux.observacion FROM loginCompras aux WHERE aux.idCliente = c.ruc AND aux.anio = :anio AND aux.mes = :mes) AS obsCompras,
            (
                SELECT cr.fechab 
                FROM cronogramaPDT cr 
                INNER JOIN clienteVigenciaBuenosContribuyentes p ON c.ruc = p.idCliente
                WHERE CAST(p.anioDesde + p.mesDesde + '01' as date) <= CAST(:anio + '-' + :mes + '-01' as date)
                AND ( CAST(p.anioHasta + p.mesHasta + '01' as date) >= CAST(:anio + '-' + :mes + '-01' as date) OR p.anioHasta IS NULL )
                AND cr.anio = :anio AND cr.mes = :mes
            ) AS fVencimientoBC,
            CASE WHEN c.y = '0' THEN crPle.fecha0 WHEN c.y = '1' THEN crPle.fecha1 WHEN c.y = '2' THEN crPle.fecha2 
                 WHEN c.y = '3' THEN crPle.fecha3 WHEN c.y = '4' THEN crPle.fecha4 WHEN c.y = '5' THEN crPle.fecha5 
                 WHEN c.y = '6' THEN crPle.fecha6 WHEN c.y = '7' THEN crPle.fecha7 WHEN c.y = '8' THEN crPle.fecha8 
                 WHEN c.y = '9' THEN crPle.fecha9 
            END AS fVencimientoPle,
            CASE WHEN c.y = '0' THEN crS.fecha0 WHEN c.y = '1' THEN crS.fecha1 WHEN c.y = '2' THEN crS.fecha2 
                 WHEN c.y = '3' THEN crS.fecha3 WHEN c.y = '4' THEN crS.fecha4 WHEN c.y = '5' THEN crS.fecha5 
                 WHEN c.y = '6' THEN crS.fecha6 WHEN c.y = '7' THEN crS.fecha7 WHEN c.y = '8' THEN crS.fecha8 
                 WHEN c.y = '9' THEN crS.fecha9 
            END AS fVencimientoSire,
            CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                 WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                 WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                 WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
            END AS fVencimiento
            FROM cliente c
            LEFT JOIN pdt621DataNew t ON c.ruc = t.idCliente AND t.anio = :anio AND t.mes = :mes
            LEFT JOIN Pdt621Bancos pb ON t.idBancoPdt621 = pb.id
            LEFT JOIN Pdt621Bancos pbRenta ON t.rentaBanco = pbRenta.id
            LEFT JOIN sireData sd ON sd.idCliente = c.ruc AND sd.anio = :anio AND sd.mes = :mes
            LEFT JOIN igv i ON i.anio = :anio AND i.mes = :mes
            LEFT JOIN gestionRegimenesTributarios grtA ON c.ruc = grtA.idCliente AND grtA.anio = :anioAux AND grtA.mes = :mesAux
            LEFT JOIN gestionRegimenesTributarios grt ON c.ruc = grt.idCliente AND grt.anio = :anio AND grt.mes = :mes
            LEFT JOIN regimenesTributarios rt ON COALESCE(grt.idRegimenTributario, grtA.idRegimenTributario) = rt.id
            LEFT JOIN cronogramaPDT cr ON :anio = cr.anio AND :mes = cr.mes
            LEFT JOIN CRONOGRAMAPLE_A crPle ON crPle.anio = :anio AND crPle.mes = :mes
            LEFT JOIN cronogramaSire crS ON :anio = crS.anio AND :mes = crS.mes
            WHERE c.ruc = :ruc
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", ruc)
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("anioAux", anioAux)
                .addValue("mesAux", mesAux);

        List<Pdt621DTO> results = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Pdt621DTO dto = Pdt621DTO.builder()
                    .ruc(rs.getString("ruc"))
                    .razonSocial(rs.getString("razonSocial"))
                    .direccion(rs.getString("direccion"))
                    .afectoIvap(toInteger(rs.getObject("afectoIvap")))
                    .anio(anio)
                    .mes(mes)
                    .detracciones(toDouble(rs.getObject("detracciones")))
                    .compensaciones(toDouble(rs.getObject("compensaciones")))
                    .aproxPlanilla(toDouble(rs.getObject("aproxPlanilla")))
                    .fechaVencimiento(rs.getString("fVencimiento"))
                    .fechaVencimientoBC(rs.getString("fVencimientoBC"))
                    .fechaVencimientoPle(rs.getString("fVencimientoPle"))
                    .fechaVencimientoSire(rs.getString("fVencimientoSire"))
                    .fechaIgvJusto(rs.getString("fIgvJusto"))
                    .mail(toInteger(rs.getObject("mail")))
                    .sus(toInteger(rs.getObject("sus")))
                    .fechaMail(rs.getString("fMail"))
                    .horaMail(rs.getString("hMail"))
                    .fechaDeclaracion(rs.getString("fDeclaracion"))
                    .horaDeclaracion(rs.getString("hDeclaracion"))
                    .igvJusto(toInteger(rs.getObject("igvJusto")))
                    .pago(toInteger(rs.getObject("pago")))
                    .idBancoPdt621(toInteger(rs.getObject("idBancoPdt621")))
                    .descBancoPdt621(rs.getString("descBancoPdt621"))
                    .pagarImpuestos(toInteger(rs.getObject("pagarImpuestos")))
                    .renta3Categoria(toInteger(rs.getObject("renta3Categoria")))
                    .rentaPago(toInteger(rs.getObject("rentaPago")))
                    .rentaBanco(toInteger(rs.getObject("rentaBanco")))
                    .descRentaBanco(rs.getString("descRentaBanco"))
                    .rentaPagarImpuestos(toInteger(rs.getObject("rentaPagarImpuestos")))
                    .notaDeDeclaracion(rs.getString("notaDeDeclaracion"))
                    .observacion(rs.getString("observacion"))
                    .observacionAnterior(rs.getString("observacionAnterior"))
                    .saldoAnteriorArrastreVentas(toDouble(rs.getObject("saldoAnteriorArrastreVentas")))
                    .periodoArrastreVentas(toDouble(rs.getObject("periodoArrastreVentas")))
                    .aplicacionArrastreVentas(toDouble(rs.getObject("aplicacionArrastreVentas")))
                    .saldoFinalArrastreVentas(toDouble(rs.getObject("saldoFinalArrastreVentas")))
                    .tasa(toDouble(rs.getObject("valor")))
                    .ventasG(toDouble(rs.getObject("ventasG")))
                    .ventasNetas10(toDouble(rs.getObject("ventasNetas10")))
                    .ventasNg(toDouble(rs.getObject("ventasNg")))
                    .ventasFilas(toInteger(rs.getObject("ventasFilas")))
                    .expFactPer(toDouble(rs.getObject("expFactPer")))
                    .expEmbrPer(toDouble(rs.getObject("expEmbrPer")))
                    .comprasG(toDouble(rs.getObject("comprasG")))
                    .comprasNetas10(toDouble(rs.getObject("comprasNetas10")))
                    .operacionComprasNetasIgv10(toInteger(rs.getObject("operacionComprasNetasIgv10")))
                    .comprasNetasIgv10(toDouble(rs.getObject("comprasNetasIgv10")))
                    .comprasMixtas(toDouble(rs.getObject("comprasMixtas")))
                    .comprasNgE(toDouble(rs.getObject("comprasNgE")))
                    .impComprasG(toDouble(rs.getObject("impComprasG")))
                    .impComprasGigv(toDouble(rs.getObject("impComprasGigv")))
                    .comprasNg(toDouble(rs.getObject("comprasNg")))
                    .coeficiente(toDouble(rs.getObject("coeficiente")))
                    .comprasFilas(toInteger(rs.getObject("comprasFilas")))
                    .mesIgv(toDouble(rs.getObject("mesIgv")))
                    .mesAnteriorIgv(toDouble(rs.getObject("mesAnteriorIgv")))
                    .ajusteIgv(toDouble(rs.getObject("ajusteIgv")))
                    .resultadoIgv(toDouble(rs.getObject("resultadoIgv")))
                    .mesPer(toDouble(rs.getObject("mesPer")))
                    .mesAnteriorPer(toDouble(rs.getObject("mesAnteriorPer")))
                    .codigosTributariosCompensacionPer(rs.getString("codigosTributariosCompensacionPer"))
                    .compensacionPer(toDouble(rs.getObject("compensacionPer")))
                    .ajustePer(toDouble(rs.getObject("ajustePer")))
                    .resultadoPer(toDouble(rs.getObject("resultadoPer")))
                    .mesRet(toDouble(rs.getObject("mesRet")))
                    .mesAnteriorRet(toDouble(rs.getObject("mesAnteriorRet")))
                    .codigosTributariosCompensacionRet(rs.getString("codigosTributariosCompensacionRet"))
                    .compensacionRet(toDouble(rs.getObject("compensacionRet")))
                    .ajusteRet(toDouble(rs.getObject("ajusteRet")))
                    .resultadoRet(toDouble(rs.getObject("resultadoRet")))
                    .igvPorPagar(toDouble(rs.getObject("igvPorPagar")))
                    .ivapVentasGravadas(toDouble(rs.getObject("ivapVentasGravadas")))
                    .ivapTributo(toDouble(rs.getObject("ivapTributo")))
                    .saldoAnteriorArrastreIngresos(toDouble(rs.getObject("saldoAnteriorArrastreIngresos")))
                    .periodoArrastreIngresos(toDouble(rs.getObject("periodoArrastreIngresos")))
                    .aplicacionArrastreIngresos(toDouble(rs.getObject("aplicacionArrastreIngresos")))
                    .saldoFinalArrastreIngresos(toDouble(rs.getObject("saldoFinalArrastreIngresos")))
                    .superaUit(rs.getString("superaUit"))
                    .tipoFactor(rs.getString("tipoFactor"))
                    .impRtaFactorA(toDouble(rs.getObject("impRtaFactorA")))
                    .ingresosFactorA(toDouble(rs.getObject("ingresosFactorA")))
                    .ajusteFactorA(toDouble(rs.getObject("ajusteFactorA")))
                    .coefA(toDouble(rs.getObject("coefA")))
                    .coefB(toDouble(rs.getObject("coefB")))
                    .baseRenta(toDouble(rs.getObject("baseRenta")))
                    .mesRenta(toDouble(rs.getObject("mesRenta")))
                    .mesAnteriorRenta(toDouble(rs.getObject("mesAnteriorRenta")))
                    .anualRenta(toDouble(rs.getObject("anualRenta")))
                    .saldoFavorExportador(toDouble(rs.getObject("saldoFavorExportador")))
                    .creditoItanRenta(toDouble(rs.getObject("cItanRenta")))
                    .codigosTributariosAjusteRenta(rs.getString("codigosTributariosAjusteRenta"))
                    .ajusteRenta(toDouble(rs.getObject("ajusteRenta")))
                    .totalDeudaTributariaRenta(toDouble(rs.getObject("totalDeudaTributariaRenta")))
                    .rentaPorPagar(toDouble(rs.getObject("rentaPorPagar")))
                    .superaUitmesA(rs.getString("superaUitmesA"))
                    .tipoFactormesA(rs.getString("tipoFactormesA"))
                    .impRtaFactorAmesA(toDouble(rs.getObject("impRtaFactorAmesA")))
                    .ingresosFactorAmesA(toDouble(rs.getObject("ingresosFactorAmesA")))
                    .ajusteFactorAmesA(toDouble(rs.getObject("ajusteFactorAmesA")))
                    .coefAmesA(toDouble(rs.getObject("coefAmesA")))
                    .coefBmesA(toDouble(rs.getObject("coefBmesA")))
                    .sumatoriaBaseImponible(toDouble(rs.getObject("sumatoriaBaseImponible")))
                    .operacion(toInteger(rs.getObject("operacion")))
                    .igvTasaEspecial10(toInteger(rs.getObject("igvTasaEspecial10")))
                    .gestionRegimenTributarioMesAnterior(toInteger(rs.getObject("gestionRegimenTributarioMesAnterior")))
                    .gestionRegimenTributario(toInteger(rs.getObject("gestionRegimenTributario")))
                    .abrGestionRegimenTributario(rs.getString("abrGestionRegimenTributario"))
                    .obsVentasAnterior(rs.getString("obsVentasAnterior"))
                    .obsVentas(rs.getString("obsVentas"))
                    .obsComprasAnterior(rs.getString("obsComprasAnterior"))
                    .obsCompras(rs.getString("obsCompras"))
                    .build();

            // Calculated values
            double tasa10Ventas = (Integer.parseInt(anio + mes) <= 202512) ? 0.10 : 0.105;
            dto.setVentasIgv(dto.getVentasG() * dto.getTasa());
            dto.setVentasNetasIgv10(dto.getVentasNetas10() * tasa10Ventas);
            dto.setComprasIgv(dto.getComprasG() * dto.getTasa());

            if (dto.getOperacionComprasNetasIgv10() == null || dto.getOperacionComprasNetasIgv10() != 2) {
                double tasa10Compras = (Integer.parseInt(anio + mes) <= 202601) ? 0.10 : 0.105;
                dto.setComprasNetasIgv10(dto.getComprasNetas10() * tasa10Compras);
            }
            dto.setComprasMixtasIgv(dto.getComprasMixtas() * dto.getTasa() * dto.getCoeficiente());
            dto.setSumatoriaBaseImponibleMasPeriodoActual(dto.getSumatoriaBaseImponible() + dto.getVentasG() + dto.getVentasNetas10() + dto.getVentasNg() + dto.getExpFactPer());

            return dto;
        });

        if (results.isEmpty()) {
            return null;
        }

        Pdt621DTO finalDto = results.get(0);
        finalDto.setRegistros(getRegistros(ruc, anio, mes));
        return finalDto;
    }

    public List<Pdt621RegistroDTO> getRegistros(String idCliente, String anio, String mes) {
        String sql = "SELECT id, idTipo, fecha, nroRectificacion, nroOrden FROM pdt621Registros WHERE idCliente = :ruc AND anio = :anio AND mes = :mes ORDER BY id ASC";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", idCliente)
                .addValue("anio", anio)
                .addValue("mes", mes);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> Pdt621RegistroDTO.builder()
                .id(rs.getInt("id"))
                .tipo(new GclassDTO(rs.getInt("idTipo")))
                .fecha(rs.getString("fecha"))
                .nroRectificacion(rs.getString("nroRectificacion"))
                .nroOrden(rs.getString("nroOrden"))
                .build());
    }

    public boolean exist(String ruc, String anio, String mes) {
        String sql = "SELECT COUNT(*) FROM pdt621DataNew WHERE idCliente = :ruc AND anio = :anio AND mes = :mes";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", ruc)
                .addValue("anio", anio)
                .addValue("mes", mes);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public void deleteRegistros(String ruc, String anio, String mes) {
        String sql = "DELETE FROM pdt621Registros WHERE idCliente = :ruc AND anio = :anio AND mes = :mes";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", ruc)
                .addValue("anio", anio)
                .addValue("mes", mes);
        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public int insertUpdate(Pdt621DTO dto) {
        String ruc = dto.getRuc();
        String anio = dto.getAnio();
        String mes = dto.getMes();

        boolean isUpdate = exist(ruc, anio, mes);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruc", ruc);
        params.addValue("anio", anio);
        params.addValue("mes", mes);
        params.addValue("detracciones", dto.getDetracciones() != null ? dto.getDetracciones() : 0.0);
        params.addValue("compensaciones", dto.getCompensaciones() != null ? dto.getCompensaciones() : 0.0);
        params.addValue("aproxPlanilla", dto.getAproxPlanilla() != null ? dto.getAproxPlanilla() : 0.0);
        params.addValue("fIgvJusto", dto.getFechaIgvJusto());
        params.addValue("mail", dto.getMail() != null ? dto.getMail() : 0);
        params.addValue("fMail", dto.getFechaMail());
        params.addValue("hMail", dto.getHoraMail());
        params.addValue("sus", dto.getSus() != null ? dto.getSus() : 0);
        params.addValue("fDeclaracion", dto.getFechaDeclaracion());
        params.addValue("hDeclaracion", dto.getHoraDeclaracion());
        params.addValue("notaDeDeclaracion", dto.getNotaDeDeclaracion());
        params.addValue("observacion", dto.getObservacion());
        params.addValue("saldoAnteriorArrastreVentas", dto.getSaldoAnteriorArrastreVentas() != null ? dto.getSaldoAnteriorArrastreVentas() : 0.0);
        params.addValue("periodoArrastreVentas", dto.getPeriodoArrastreVentas() != null ? dto.getPeriodoArrastreVentas() : 0.0);
        params.addValue("aplicacionArrastreVentas", dto.getAplicacionArrastreVentas() != null ? dto.getAplicacionArrastreVentas() : 0.0);
        params.addValue("saldoFinalArrastreVentas", dto.getSaldoFinalArrastreVentas() != null ? dto.getSaldoFinalArrastreVentas() : 0.0);
        params.addValue("ventasG", dto.getVentasG() != null ? dto.getVentasG() : 0.0);
        params.addValue("ventasNetas10", dto.getVentasNetas10() != null ? dto.getVentasNetas10() : 0.0);
        params.addValue("ventasNg", dto.getVentasNg() != null ? dto.getVentasNg() : 0.0);
        params.addValue("expFactPer", dto.getExpFactPer() != null ? dto.getExpFactPer() : 0.0);
        params.addValue("expEmbrPer", dto.getExpEmbrPer() != null ? dto.getExpEmbrPer() : 0.0);
        params.addValue("comprasG", dto.getComprasG() != null ? dto.getComprasG() : 0.0);
        params.addValue("comprasNetas10", dto.getComprasNetas10() != null ? dto.getComprasNetas10() : 0.0);
        params.addValue("operacionComprasNetasIgv10", dto.getOperacionComprasNetasIgv10() != null ? dto.getOperacionComprasNetasIgv10() : 0);
        params.addValue("comprasNetasIgv10", dto.getComprasNetasIgv10() != null ? dto.getComprasNetasIgv10() : 0.0);
        params.addValue("comprasMixtas", dto.getComprasMixtas() != null ? dto.getComprasMixtas() : 0.0);
        params.addValue("comprasNgE", dto.getComprasNgE() != null ? dto.getComprasNgE() : 0.0);
        params.addValue("impComprasG", dto.getImpComprasG() != null ? dto.getImpComprasG() : 0.0);
        params.addValue("impComprasGigv", dto.getImpComprasGigv() != null ? dto.getImpComprasGigv() : 0.0);
        params.addValue("comprasNg", dto.getComprasNg() != null ? dto.getComprasNg() : 0.0);
        params.addValue("coeficiente", dto.getCoeficiente() != null ? dto.getCoeficiente() : 0.0);
        params.addValue("mesIgv", dto.getMesIgv() != null ? dto.getMesIgv() : 0.0);
        params.addValue("mesAnteriorIgv", dto.getMesAnteriorIgv() != null ? dto.getMesAnteriorIgv() : 0.0);
        params.addValue("ajusteIgv", dto.getAjusteIgv() != null ? dto.getAjusteIgv() : 0.0);
        params.addValue("resultadoIgv", dto.getResultadoIgv() != null ? dto.getResultadoIgv() : 0.0);
        params.addValue("mesPer", dto.getMesPer() != null ? dto.getMesPer() : 0.0);
        params.addValue("mesAnteriorPer", dto.getMesAnteriorPer() != null ? dto.getMesAnteriorPer() : 0.0);
        params.addValue("codigosTributariosCompensacionPer", dto.getCodigosTributariosCompensacionPer());
        params.addValue("compensacionPer", dto.getCompensacionPer() != null ? dto.getCompensacionPer() : 0.0);
        params.addValue("ajustePer", dto.getAjustePer() != null ? dto.getAjustePer() : 0.0);
        params.addValue("resultadoPer", dto.getResultadoPer() != null ? dto.getResultadoPer() : 0.0);
        params.addValue("mesRet", dto.getMesRet() != null ? dto.getMesRet() : 0.0);
        params.addValue("mesAnteriorRet", dto.getMesAnteriorRet() != null ? dto.getMesAnteriorRet() : 0.0);
        params.addValue("codigosTributariosCompensacionRet", dto.getCodigosTributariosCompensacionRet());
        params.addValue("compensacionRet", dto.getCompensacionRet() != null ? dto.getCompensacionRet() : 0.0);
        params.addValue("ajusteRet", dto.getAjusteRet() != null ? dto.getAjusteRet() : 0.0);
        params.addValue("resultadoRet", dto.getResultadoRet() != null ? dto.getResultadoRet() : 0.0);
        params.addValue("igvPorPagar", dto.getIgvPorPagar() != null ? dto.getIgvPorPagar() : 0.0);
        params.addValue("ivapVentasGravadas", dto.getIvapVentasGravadas() != null ? dto.getIvapVentasGravadas() : 0.0);
        params.addValue("ivapTributo", dto.getIvapTributo() != null ? dto.getIvapTributo() : 0.0);
        params.addValue("saldoAnteriorArrastreIngresos", dto.getSaldoAnteriorArrastreIngresos() != null ? dto.getSaldoAnteriorArrastreIngresos() : 0.0);
        params.addValue("periodoArrastreIngresos", dto.getPeriodoArrastreIngresos() != null ? dto.getPeriodoArrastreIngresos() : 0.0);
        params.addValue("aplicacionArrastreIngresos", dto.getAplicacionArrastreIngresos() != null ? dto.getAplicacionArrastreIngresos() : 0.0);
        params.addValue("saldoFinalArrastreIngresos", dto.getSaldoFinalArrastreIngresos() != null ? dto.getSaldoFinalArrastreIngresos() : 0.0);
        params.addValue("impRtaFactorA", dto.getImpRtaFactorA() != null ? dto.getImpRtaFactorA() : 0.0);
        params.addValue("ingresosFactorA", dto.getIngresosFactorA() != null ? dto.getIngresosFactorA() : 0.0);
        params.addValue("ajusteFactorA", dto.getAjusteFactorA() != null ? dto.getAjusteFactorA() : 0.0);
        params.addValue("coefA", dto.getCoefA() != null ? dto.getCoefA() : 0.0);
        params.addValue("coefB", dto.getCoefB() != null ? dto.getCoefB() : 0.0);
        params.addValue("baseRenta", dto.getBaseRenta() != null ? dto.getBaseRenta() : 0.0);
        params.addValue("mesRenta", dto.getMesRenta() != null ? dto.getMesRenta() : 0.0);
        params.addValue("mesAnteriorRenta", dto.getMesAnteriorRenta() != null ? dto.getMesAnteriorRenta() : 0.0);
        params.addValue("anualRenta", dto.getAnualRenta() != null ? dto.getAnualRenta() : 0.0);
        params.addValue("saldoFavorExportador", dto.getSaldoFavorExportador() != null ? dto.getSaldoFavorExportador() : 0.0);
        params.addValue("cItanRenta", dto.getCreditoItanRenta() != null ? dto.getCreditoItanRenta() : 0.0);
        params.addValue("codigosTributariosAjusteRenta", dto.getCodigosTributariosAjusteRenta());
        params.addValue("ajusteRenta", dto.getAjusteRenta() != null ? dto.getAjusteRenta() : 0.0);
        params.addValue("totalDeudaTributariaRenta", dto.getTotalDeudaTributariaRenta() != null ? dto.getTotalDeudaTributariaRenta() : 0.0);
        params.addValue("rentaPorPagar", dto.getRentaPorPagar() != null ? dto.getRentaPorPagar() : 0.0);
        params.addValue("superaUit", dto.getSuperaUit());
        params.addValue("tipoFactor", dto.getTipoFactor());
        params.addValue("operacion", dto.getOperacion() != null ? dto.getOperacion() : 0);
        params.addValue("igvJusto", dto.getIgvJusto() != null ? dto.getIgvJusto() : 0);
        params.addValue("pago", dto.getPago() != null ? dto.getPago() : 0);
        params.addValue("idBancoPdt621", dto.getIdBancoPdt621() != null ? dto.getIdBancoPdt621() : 0);
        params.addValue("pagarImpuestos", dto.getPagarImpuestos() != null ? dto.getPagarImpuestos() : 0);
        params.addValue("renta3Categoria", dto.getRenta3Categoria() != null ? dto.getRenta3Categoria() : 0);
        params.addValue("rentaPago", dto.getRentaPago() != null ? dto.getRentaPago() : 0);
        params.addValue("rentaBanco", dto.getRentaBanco() != null ? dto.getRentaBanco() : 0);
        params.addValue("rentaPagarImpuestos", dto.getRentaPagarImpuestos() != null ? dto.getRentaPagarImpuestos() : 0);
        params.addValue("igvTasaEspecial10", dto.getIgvTasaEspecial10() != null ? dto.getIgvTasaEspecial10() : 0);

        if (isUpdate) {
            String updateSql = """
                UPDATE pdt621DataNew SET
                detracciones = :detracciones, compensaciones = :compensaciones, aproxPlanilla = :aproxPlanilla, fIgvJusto = :fIgvJusto,
                mail = :mail, fMail = :fMail, hMail = :hMail, sus = :sus, fDeclaracion = :fDeclaracion, hDeclaracion = :hDeclaracion,
                notaDeDeclaracion = :notaDeDeclaracion, observacion = :observacion,
                saldoAnteriorArrastreVentas = :saldoAnteriorArrastreVentas, periodoArrastreVentas = :periodoArrastreVentas,
                aplicacionArrastreVentas = :aplicacionArrastreVentas, saldoFinalArrastreVentas = :saldoFinalArrastreVentas,
                ventasG = :ventasG, ventasNetas10 = :ventasNetas10, ventasNg = :ventasNg,
                expFactPer = :expFactPer, expEmbrPer = :expEmbrPer,
                comprasG = :comprasG, comprasNetas10 = :comprasNetas10, operacionComprasNetasIgv10 = :operacionComprasNetasIgv10,
                comprasNetasIgv10 = :comprasNetasIgv10, comprasMixtas = :comprasMixtas, comprasNgE = :comprasNgE,
                impComprasG = :impComprasG, impComprasGigv = :impComprasGigv,
                comprasNg = :comprasNg, coeficiente = :coeficiente, mesIgv = :mesIgv,
                mesAnteriorIgv = :mesAnteriorIgv, ajusteIgv = :ajusteIgv, resultadoIgv = :resultadoIgv, mesPer = :mesPer,
                mesAnteriorPer = :mesAnteriorPer, codigosTributariosCompensacionPer = :codigosTributariosCompensacionPer,
                compensacionPer = :compensacionPer, ajustePer = :ajustePer, resultadoPer = :resultadoPer,
                mesRet = :mesRet, mesAnteriorRet = :mesAnteriorRet, codigosTributariosCompensacionRet = :codigosTributariosCompensacionRet,
                compensacionRet = :compensacionRet, ajusteRet = :ajusteRet, resultadoRet = :resultadoRet,
                igvPorPagar = :igvPorPagar, ivapVentasGravadas = :ivapVentasGravadas, ivapTributo = :ivapTributo,
                saldoAnteriorArrastreIngresos = :saldoAnteriorArrastreIngresos, periodoArrastreIngresos = :periodoArrastreIngresos,
                aplicacionArrastreIngresos = :aplicacionArrastreIngresos, saldoFinalArrastreIngresos = :saldoFinalArrastreIngresos,
                impRtaFactorA = :impRtaFactorA, ingresosFactorA = :ingresosFactorA,
                ajusteFactorA = :ajusteFactorA, coefA = :coefA, coefB = :coefB, baseRenta = :baseRenta, mesRenta = :mesRenta,
                mesAnteriorRenta = :mesAnteriorRenta, anualRenta = :anualRenta, saldoFavorExportador = :saldoFavorExportador,
                cItanRenta = :cItanRenta, codigosTributariosAjusteRenta = :codigosTributariosAjusteRenta, ajusteRenta = :ajusteRenta,
                totalDeudaTributariaRenta = :totalDeudaTributariaRenta, rentaPorPagar = :rentaPorPagar, superaUit = :superaUit,
                tipoFactor = :tipoFactor, operacion = :operacion, igvJusto = :igvJusto, pago = :pago,
                idBancoPdt621 = :idBancoPdt621, pagarImpuestos = :pagarImpuestos,
                renta3Categoria = :renta3Categoria, rentaPago = :rentaPago, rentaBanco = :rentaBanco,
                rentaPagarImpuestos = :rentaPagarImpuestos, igvTasaEspecial10 = :igvTasaEspecial10
                WHERE idCliente = :ruc AND anio = :anio AND mes = :mes
            """;
            jdbcTemplate.update(updateSql, params);
        } else {
            String insertSql = """
                INSERT INTO pdt621DataNew (
                    idCliente, anio, mes,
                    detracciones, compensaciones, aproxPlanilla, fIgvJusto,
                    mail, fMail, hMail, sus, fDeclaracion, hDeclaracion,
                    notaDeDeclaracion, observacion,
                    saldoAnteriorArrastreVentas, periodoArrastreVentas, aplicacionArrastreVentas, saldoFinalArrastreVentas,
                    ventasG, ventasNetas10, ventasNg, expFactPer, expEmbrPer,
                    comprasG, comprasNetas10, operacionComprasNetasIgv10, comprasNetasIgv10, comprasMixtas, comprasNgE,
                    impComprasG, impComprasGigv, comprasNg, coeficiente, mesIgv,
                    mesAnteriorIgv, ajusteIgv, resultadoIgv, mesPer, mesAnteriorPer,
                    codigosTributariosCompensacionPer, compensacionPer, ajustePer, resultadoPer,
                    mesRet, mesAnteriorRet, codigosTributariosCompensacionRet, compensacionRet, ajusteRet, resultadoRet,
                    igvPorPagar, ivapVentasGravadas, ivapTributo,
                    saldoAnteriorArrastreIngresos, periodoArrastreIngresos, aplicacionArrastreIngresos, saldoFinalArrastreIngresos,
                    impRtaFactorA, ingresosFactorA, ajusteFactorA, coefA, coefB, baseRenta, mesRenta,
                    mesAnteriorRenta, anualRenta, saldoFavorExportador, cItanRenta, codigosTributariosAjusteRenta, ajusteRenta,
                    totalDeudaTributariaRenta, rentaPorPagar, superaUit, tipoFactor, operacion,
                    igvJusto, pago, idBancoPdt621, pagarImpuestos,
                    renta3Categoria, rentaPago, rentaBanco, rentaPagarImpuestos, igvTasaEspecial10
                ) VALUES (
                    :ruc, :anio, :mes,
                    :detracciones, :compensaciones, :aproxPlanilla, :fIgvJusto,
                    :mail, :fMail, :hMail, :sus, :fDeclaracion, :hDeclaracion,
                    :notaDeDeclaracion, :observacion,
                    :saldoAnteriorArrastreVentas, :periodoArrastreVentas, :aplicacionArrastreVentas, :saldoFinalArrastreVentas,
                    :ventasG, :ventasNetas10, :ventasNg, :expFactPer, :expEmbrPer,
                    :comprasG, :comprasNetas10, :operacionComprasNetasIgv10, :comprasNetasIgv10, :comprasMixtas, :comprasNgE,
                    :impComprasG, :impComprasGigv, :comprasNg, :coeficiente, :mesIgv,
                    :mesAnteriorIgv, :ajusteIgv, :resultadoIgv, :mesPer, :mesAnteriorPer,
                    :codigosTributariosCompensacionPer, :compensacionPer, :ajustePer, :resultadoPer,
                    :mesRet, :mesAnteriorRet, :codigosTributariosCompensacionRet, :compensacionRet, :ajusteRet, :resultadoRet,
                    :igvPorPagar, :ivapVentasGravadas, :ivapTributo,
                    :saldoAnteriorArrastreIngresos, :periodoArrastreIngresos, :aplicacionArrastreIngresos, :saldoFinalArrastreIngresos,
                    :impRtaFactorA, :ingresosFactorA, :ajusteFactorA, :coefA, :coefB, :baseRenta, :mesRenta,
                    :mesAnteriorRenta, :anualRenta, :saldoFavorExportador, :cItanRenta, :codigosTributariosAjusteRenta, :ajusteRenta,
                    :totalDeudaTributariaRenta, :rentaPorPagar, :superaUit, :tipoFactor, :operacion,
                    :igvJusto, :pago, :idBancoPdt621, :pagarImpuestos,
                    :renta3Categoria, :rentaPago, :rentaBanco, :rentaPagarImpuestos, :igvTasaEspecial10
                )
            """;
            jdbcTemplate.update(insertSql, params);
        }

        deleteRegistros(ruc, anio, mes);
        if (dto.getRegistros() != null) {
            int idx = 1;
            for (Pdt621RegistroDTO reg : dto.getRegistros()) {
                if (reg.getFecha() != null && !reg.getFecha().trim().isEmpty()) {
                    String sqlReg = "INSERT INTO pdt621Registros (idCliente, anio, mes, id, idTipo, fecha, nroRectificacion, nroOrden) VALUES (:ruc, :anio, :mes, :id, :idTipo, :fecha, :nroRectificacion, :nroOrden)";
                    MapSqlParameterSource regParams = new MapSqlParameterSource()
                            .addValue("ruc", ruc)
                            .addValue("anio", anio)
                            .addValue("mes", mes)
                            .addValue("id", idx++)
                            .addValue("idTipo", (reg.getTipo() != null && reg.getTipo().getId() > 0) ? reg.getTipo().getId() : 3)
                            .addValue("fecha", reg.getFecha())
                            .addValue("nroRectificacion", reg.getNroRectificacion())
                            .addValue("nroOrden", reg.getNroOrden());
                    jdbcTemplate.update(sqlReg, regParams);
                }
            }
        }

        if (dto.getGestionRegimenTributario() != null && dto.getGestionRegimenTributario() > 0) {
            String checkReg = "SELECT COUNT(*) FROM gestionRegimenesTributarios WHERE idCliente = :ruc AND anio = :anio AND mes = :mes";
            Integer cnt = jdbcTemplate.queryForObject(checkReg, params, Integer.class);
            params.addValue("idRegimenTributario", dto.getGestionRegimenTributario());
            if (cnt != null && cnt > 0) {
                jdbcTemplate.update("UPDATE gestionRegimenesTributarios SET idRegimenTributario = :idRegimenTributario WHERE idCliente = :ruc AND anio = :anio AND mes = :mes", params);
            } else {
                jdbcTemplate.update("INSERT INTO gestionRegimenesTributarios (idCliente, anio, mes, idRegimenTributario) VALUES (:ruc, :anio, :mes, :idRegimenTributario)", params);
            }
        }

        syncDeudas(dto);
        return isUpdate ? 2 : 1;
    }

    private void syncDeudas(Pdt621DTO dto) {
        String ruc = dto.getRuc();
        String anio = dto.getAnio();
        String mes = dto.getMes();

        String fVenc = dto.getFechaVencimientoBC() != null && !dto.getFechaVencimientoBC().trim().isEmpty() 
                ? dto.getFechaVencimientoBC() : dto.getFechaVencimiento();

        if (fVenc == null || fVenc.trim().isEmpty()) {
            try {
                String sqlCronograma = """
                    SELECT 
                    CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                         WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                         WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                         WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
                    END AS fVencimiento
                    FROM cliente c
                    LEFT JOIN cronogramaPDT cr ON :anio = cr.anio AND :mes = cr.mes
                    WHERE c.ruc = :ruc
                """;
                MapSqlParameterSource p = new MapSqlParameterSource()
                        .addValue("ruc", ruc).addValue("anio", anio).addValue("mes", mes);
                fVenc = jdbcTemplate.queryForObject(sqlCronograma, p, String.class);
            } catch (Exception ignored) { }
        }

        String codIgv = (dto.getIgvTasaEspecial10() != null && dto.getIgvTasaEspecial10() == 1) ? "1018" : "1011";
        syncSingleDeuda(ruc, anio, mes, codIgv, dto.getIgvPorPagar(), fVenc);

        syncSingleDeuda(ruc, anio, mes, "1016", dto.getIvapTributo(), fVenc);

        String codRenta = getCodigoRenta(dto.getGestionRegimenTributario());
        if (codRenta != null && !codRenta.isEmpty()) {
            syncSingleDeuda(ruc, anio, mes, codRenta, dto.getRentaPorPagar(), fVenc);
        }
    }

    private void syncSingleDeuda(String ruc, String anio, String mes, String codigo, Double importe, String fVenc) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", ruc)
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("codigo", codigo)
                .addValue("importe", importe != null ? importe : 0.0)
                .addValue("fVenc", fVenc != null && !fVenc.trim().isEmpty() ? fVenc.trim() : null);

        String checkSql = "SELECT COUNT(*) FROM deudasNew WHERE idCliente = :ruc AND anio = :anio AND mes = :mes AND codigo = :codigo";
        Integer exists = jdbcTemplate.queryForObject(checkSql, params, Integer.class);

        if (exists != null && exists > 0) {
            if (importe != null && importe > 0) {
                jdbcTemplate.update("UPDATE deudasNew SET importe = :importe, fVencimiento = :fVenc WHERE idCliente = :ruc AND anio = :anio AND mes = :mes AND codigo = :codigo", params);
            } else {
                jdbcTemplate.update("DELETE FROM deudasNew WHERE idCliente = :ruc AND anio = :anio AND mes = :mes AND codigo = :codigo", params);
            }
        } else {
            if (importe != null && importe > 0) {
                jdbcTemplate.update("INSERT INTO deudasNew (idCliente, anio, mes, codigo, idEstado, idEstadoPago, importe, fVencimiento) VALUES (:ruc, :anio, :mes, :codigo, 6, 1, :importe, :fVenc)", params);
            }
        }
    }

    private String getCodigoRenta(Integer idRegimen) {
        if (idRegimen == null) return "3121";
        return switch (idRegimen) {
            case 2 -> "3111";
            case 3 -> "3121";
            case 4 -> "3031";
            case 5 -> "3311";
            case 6 -> "3811";
            case 7 -> "3412";
            default -> "3121";
        };
    }

    public List<Pdt621HistorialDTO> listHistorial(String idCliente, String periodoInicial, String periodoFinal) {
        int start = parsePeriodo(periodoInicial, 200001);
        int end = parsePeriodo(periodoFinal, 209912);

        String sql = """
            SELECT d.idCliente, d.anio, d.mes,
            (SELECT p.nroOrden FROM pdt621Registros p WHERE p.idCliente = d.idCliente AND p.anio = d.anio AND p.mes = d.mes AND p.id = (SELECT MAX(id) FROM pdt621Registros x WHERE x.idCliente = d.idCliente AND x.anio = d.anio AND x.mes = d.mes AND x.idTipo IN (3,4,5))) AS nroOrden,
            (SELECT p.fecha FROM pdt621Registros p WHERE p.idCliente = d.idCliente AND p.anio = d.anio AND p.mes = d.mes AND p.id = (SELECT MAX(id) FROM pdt621Registros x WHERE x.idCliente = d.idCliente AND x.anio = d.anio AND x.mes = d.mes AND x.idTipo IN (3,4,5))) AS fechaDeclaracion,
            cr.fechab,
            CASE WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2 
                 WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5 
                 WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8 
                 WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab 
            END AS fVencimiento
            FROM pdt621DataNew d
            LEFT JOIN cliente c ON c.ruc = d.idCliente
            LEFT JOIN cronogramaPDT cr ON d.anio = cr.anio AND d.mes = cr.mes
            WHERE d.idCliente = :idCliente
            AND CAST(d.anio + d.mes AS INT) >= :start
            AND CAST(d.anio + d.mes AS INT) <= :end
            ORDER BY d.anio DESC, d.mes DESC
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idCliente", idCliente)
                .addValue("start", start)
                .addValue("end", end);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            String nroOrden = rs.getString("nroOrden");
            int registrado = (nroOrden != null && !nroOrden.trim().isEmpty()) ? 1 : 0;
            return Pdt621HistorialDTO.builder()
                    .idCliente(rs.getString("idCliente"))
                    .ruc(rs.getString("idCliente"))
                    .anio(rs.getString("anio"))
                    .mes(rs.getString("mes"))
                    .registrado(registrado)
                    .fechaVencimiento(rs.getString("fVencimiento"))
                    .fechaDeclaracion(rs.getString("fechaDeclaracion"))
                    .nroOrden(nroOrden)
                    .build();
        });
    }

    public String[] getVencimientoLimits(String anio, String mes) {
        String sql = """
            SELECT MIN(cr.fecha0) as minVenc, MAX(cr.fechab) as maxVenc
            FROM cronogramaPDT cr
            WHERE cr.anio = :anio AND cr.mes = :mes
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("anio", anio)
                .addValue("mes", mes);

        return jdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                String minVenc = rs.getString("minVenc");
                String maxVenc = rs.getString("maxVenc");
                return new String[]{minVenc != null ? minVenc : "", maxVenc != null ? maxVenc : ""};
            }
            return new String[]{"", ""};
        });
    }

    private int parsePeriodo(String periodo, int defaultVal) {
        if (periodo == null || periodo.trim().isEmpty()) {
            return defaultVal;
        }
        String clean = periodo.replace("-", "").trim();
        if (clean.length() >= 6) {
            try {
                return Integer.parseInt(clean.substring(0, 6));
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultVal;
    }
}
