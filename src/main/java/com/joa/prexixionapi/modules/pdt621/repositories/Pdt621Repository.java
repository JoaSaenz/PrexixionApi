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
import java.util.Arrays;
import java.util.stream.Collectors;

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
                .cItanRenta(toDouble(rs.getObject("rentaCItan")))
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
                .cItanRenta(toDouble(rs.getObject("cItanRenta")))
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
             END AS fVencimiento, 
             CASE 
                WHEN c.y = '0' THEN crG.fecha0 
                WHEN c.y = '1' THEN crG.fecha1 
                WHEN c.y = '2' THEN crG.fecha2 
                WHEN c.y = '3' THEN crG.fecha3 
                WHEN c.y = '4' THEN crG.fecha4 
                WHEN c.y = '5' THEN crG.fecha5 
                WHEN c.y = '6' THEN crG.fecha6 
                WHEN c.y = '7' THEN crG.fecha7 
                WHEN c.y = '8' THEN crG.fecha8 
                WHEN c.y = '9' THEN crG.fecha9
            END AS fVencimientoGear, 
            siData.registrado AS sireCV, 
            t.ventasG, t.ventasNg,
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
            LEFT JOIN cronogramaGear crG ON :anio = crG.anio AND :mes = crG.mes
            LEFT JOIN uit u ON u.anio = :anio 
            INNER JOIN clienteServiciosTributarios p ON c.ruc = p.idCliente AND p.stIdServicioTributario = 4 
            LEFT JOIN pdt621DataNew t ON c.ruc = t.idCliente AND :anio = t.anio AND :mes = t.mes 
            LEFT JOIN loginProcesos lp ON t.idCliente = lp.ruc AND lp.anio = :anio AND lp.mes = :mes 
            INNER JOIN clientsEstados ce on c.idEstado = ce.id  
            LEFT JOIN Pdt621Bancos pb ON t.idBancoPdt621 = pb.id
            LEFT JOIN Pdt621Bancos pbRenta ON t.rentaBanco = pbRenta.id
            LEFT JOIN pdt621registros pR ON t.idCliente = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes 
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

        if (req.getEstados() != null && !req.getEstados().isEmpty()) {
            sql.append(" AND c.idEstado IN (:estados)");
            params.addValue("estados", Arrays.stream(req.getEstados().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        if (req.getGrupos() != null && !req.getGrupos().isEmpty()) {
            sql.append(" AND c.y IN (:grupos)");
            params.addValue("grupos", Arrays.asList(req.getGrupos().split(",")));
        }
        
        if (req.getRuc() != null && !req.getRuc().trim().isEmpty()) {
            sql.append(" AND c.ruc = :ruc");
            params.addValue("ruc", req.getRuc().trim());
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
                .fVencimientoGear(rs.getString("fVencimientoGear"))
                .fVencimiento(rs.getString("fVencimiento"))
                .sireCV(rs.getString("ruc") == null ? 2 : rs.getInt("sireCV"))
                .mail(rs.getInt("mail"))
                .sus(rs.getInt("sus"))
                .pagarImpuestos(rs.getInt("pagarImpuestos"))
                .ventasG(toDouble(rs.getObject("ventasG")))
                .ventasNg(toDouble(rs.getObject("ventasNg")))
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
                        .tipo(rs.getInt("idTipoRegistro"))
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
}
