package com.joa.prexixionapi.modules.beneficiarioFinal.repositories;

import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalReporteDTO;
import com.joa.prexixionapi.modules.beneficiarioFinal.dto.BeneficiarioFinalRegistroDTO;
import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BeneficiarioFinalReporteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<BeneficiarioFinalReporteDTO> list(String anio, String mes, String estados, String grupos) {
        String sql = """
                SELECT c.idEstado, ce.descripcion as estado, c.y, c.ruc, c.razonSocial,
                CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) as fechaI,
                CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) as fechaF,
                pd.observacion, pd.mail,
                c.fPle, c.fPrico,
                c.rtMypeTributario, c.rtRus, c.rtEspecial, c.rtGeneral, c.rtAmazonico, c.rtAgrario,
                c.rt1ra, c.rt2da, c.rt3ra, c.rt4ta, c.rt5ta,
                ts.id as idTipoServicio, ts.abreviatura as tipoServicioAbr, ts.descripcion as tipoServicio,
                pR.id as idRegistroTecnico, pR.idTipo as idTipoRegistro, pR.fecha as fechaRegistro, pR.nroOrden as nroOrdenRegistro, pR.nro_registro as nroRegistroLogico,
                CASE
                    WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2
                    WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5
                    WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8
                    WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab
                END as vencimiento
                FROM cliente c
                INNER JOIN clienteServiciosTributarios ct ON c.ruc = ct.idCliente AND ct.stIdServicioTributario = 10
                LEFT JOIN bf3800_data pd ON c.ruc = pd.idCliente AND pd.anio = :anio AND pd.mes = :mes
                INNER JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN clientsTipoServicio ts ON c.idTipoServicio = ts.id
                LEFT JOIN CRONOGRAMAPDT cr ON cr.anio = :anio AND cr.mes = :mes
                LEFT JOIN bf3800_registros pR ON c.ruc = pR.idCliente AND pR.anio = :anio AND pR.mes = :mes
                AND pR.id = (SELECT MAX(id) FROM bf3800_registros x WHERE x.idCliente = c.ruc AND x.anio = :anio AND x.mes = :mes)
                WHERE CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) <= :periodoDate
                AND (CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) >= :periodoDate OR ct.stAnioHasta IS NULL)
                AND c.idEstado IN (%s) AND c.y IN (%s)
                ORDER BY c.y
                """.formatted(estados, grupos);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("periodoDate", anio + "-" + mes + "-01");

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapRowToReporteDTO(rs, anio, mes));
    }

    public List<BeneficiarioFinalReporteDTO> getOneList(String ruc, String anio, String mes) {
        String sql = """
                SELECT c.idEstado, ce.descripcion as estado, c.ruc, c.y, c.razonSocial, c.direccion,
                c.fPle, c.fPrico, c.rtMypeTributario, c.rtRus, c.rtEspecial, c.rtGeneral, c.rtAmazonico, c.rtAgrario,
                c.rt1ra, c.rt2da, c.rt3ra, c.rt4ta, c.rt5ta,
                ts.id as idTipoServicio, ts.abreviatura as tipoServicioAbr, ts.descripcion as tipoServicio,
                CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) as fechaI,
                CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) as fechaF,
                pd.observacion, pd.mail,
                CASE
                    WHEN c.y = '0' THEN cr.fecha0 WHEN c.y = '1' THEN cr.fecha1 WHEN c.y = '2' THEN cr.fecha2
                    WHEN c.y = '3' THEN cr.fecha3 WHEN c.y = '4' THEN cr.fecha4 WHEN c.y = '5' THEN cr.fecha5
                    WHEN c.y = '6' THEN cr.fecha6 WHEN c.y = '7' THEN cr.fecha7 WHEN c.y = '8' THEN cr.fecha8
                    WHEN c.y = '9' THEN cr.fecha9 WHEN c.y = 'b' THEN cr.fechab
                END as vencimiento
                FROM cliente c
                INNER JOIN clienteServiciosTributarios ct ON c.ruc = ct.idCliente AND ct.stIdServicioTributario = 10
                LEFT JOIN bf3800_data pd ON c.ruc = pd.idCliente AND pd.anio = :anio AND pd.mes = :mes
                INNER JOIN clientsEstados ce ON c.idEstado = ce.id
                LEFT JOIN clientsTipoServicio ts ON c.idTipoServicio = ts.id
                LEFT JOIN CRONOGRAMAPDT cr ON cr.anio = :anio AND cr.mes = :mes
                WHERE c.ruc = :ruc
                AND CAST(ct.stAnioDesde + ct.stMesDesde + '01' as date) <= :periodoDate
                AND (CAST(ct.stAnioHasta + ct.stMesHasta + '01' as date) >= :periodoDate OR ct.stAnioHasta IS NULL)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ruc", ruc)
                .addValue("anio", anio)
                .addValue("mes", mes)
                .addValue("periodoDate", anio + "-" + mes + "-01");

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapRowToReporteDTO(rs, anio, mes));
    }

    private BeneficiarioFinalReporteDTO mapRowToReporteDTO(ResultSet rs, String anio, String mes) throws SQLException {
        String ruc = rs.getString("ruc");
        String y = rs.getString("y");
        String venc = rs.getString("vencimiento");

        BeneficiarioFinalReporteDTO dto = BeneficiarioFinalReporteDTO.builder()
                .idEstado(String.valueOf(rs.getInt("idEstado")))
                .estado(rs.getString("estado"))
                .idCliente(ruc)
                .y(y)
                .razonSocial(rs.getString("razonSocial"))
                .anio(anio)
                .mes(mes)
                .fInicio(rs.getString("fechaI"))
                .fFin(rs.getString("fechaF"))
                .observacion(rs.getString("observacion"))
                .mail(rs.getInt("mail"))
                .idTipoServicio(rs.getInt("idTipoServicio"))
                .tipoServicioAbr(rs.getString("tipoServicioAbr"))
                .tipoServicio(rs.getString("tipoServicio"))
                .fVencimiento(venc)
                .build();
                
        try {
            dto.setDireccion(rs.getString("direccion"));
        } catch(SQLException e) {
            // Ignored, direccion might not be in the query
        }

        dto.setRegimenTributario(calculateRegimenTributarioFromRs(rs));
        dto.setPle(!"".equals(rs.getString("fPle")) && rs.getString("fPle") != null ? "PLE" : "");
        dto.setPrico(!"".equals(rs.getString("fPrico")) && rs.getString("fPrico") != null ? "PRICO" : "");

        try {
            int idTipoReg = rs.getInt("idTipoRegistro");
            if (idTipoReg != 0) {
                BeneficiarioFinalRegistroDTO reg = BeneficiarioFinalRegistroDTO.builder()
                        .id(rs.getInt("idRegistroTecnico"))
                        .nroRegistro(rs.getInt("nroRegistroLogico"))
                        .tipo(new GclassDTO(idTipoReg, ""))
                        .fecha(rs.getString("fechaRegistro"))
                        .nroOrden(rs.getString("nroOrdenRegistro"))
                        .build();
                dto.setRegistros(List.of(reg));
            }
        } catch(SQLException e) {
            // Ignored, registro data might not be in the query
        }

        if (venc != null && venc.length() >= 10) {
            String[] parts = venc.split("-");
            dto.setDiaVencimiento(parts[2]);
            dto.setMesVencimiento(DateUtils.getNameStMonth(parts[1]));
            dto.setDifference((int) DateUtils.getDifferenceInDays(venc));
        }

        return dto;
    }

    private String calculateRegimenTributarioFromRs(ResultSet rs) throws SQLException {
        List<String> rTs = new ArrayList<>();
        if (rs.getInt("rt3ra") != 0) {
            if (rs.getInt("rtMypeTributario") != 0) rTs.add("MYPE Tributario");
            if (rs.getInt("rtRus") != 0) rTs.add("RUS");
            if (rs.getInt("rtEspecial") != 0) rTs.add("Especial");
            if (rs.getInt("rtGeneral") != 0) rTs.add("General");
            if (rs.getInt("rtAmazonico") != 0) rTs.add("Amazónico");
            if (rs.getInt("rtAgrario") != 0) rTs.add("Agrario");
        } else {
            if (rs.getInt("rt1ra") != 0) rTs.add("1ra");
            else if (rs.getInt("rt2da") != 0) rTs.add("2da");
            else if (rs.getInt("rt4ta") != 0) rTs.add("4ta");
            else if (rs.getInt("rt5ta") != 0) rTs.add("5ta");
        }
        return String.join(", ", rTs);
    }
}
