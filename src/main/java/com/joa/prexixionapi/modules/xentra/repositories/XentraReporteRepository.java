package com.joa.prexixionapi.modules.xentra.repositories;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.joa.prexixionapi.modules.xentra.dto.XentraReporteDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class XentraReporteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<XentraReporteDTO> list(int idPuesto, int idArea, String dni, String fechaInicial, String fechaFinal) {
        StringBuilder sql = new StringBuilder("""
                SELECT xf.id, xf.idXentra,
                x.idArea, a.descripcion AS descArea, x.idSubArea, ps.descripcion as descSubArea,
                CONCAT (x.abreviatura,' - ', x.nombre) AS nombreReporte, x.responsable,
                CONCAT (
                        SUBSTRING (p.nombres, 1,
                        CASE
                        WHEN CHARINDEX(' ', p.nombres)-1 < 0
                        THEN LEN (p.nombres)
                        ELSE CHARINDEX(' ', p.nombres)-1
                        END) ,
                        ' ',
                        SUBSTRING (p.apellidos, 1,
                        CASE
                        WHEN CHARINDEX(' ', p.apellidos)-1 < 0
                        THEN LEN (p.apellidos)
                        ELSE CHARINDEX(' ', p.apellidos)-1
                        END)
                		)
                AS responsableNombreApellido,
                xf.fecha, xf.idEstado, xfe.descripcion AS descEstado, xf.fechaEstado, xf.horaEstado, xf.estadoLogico
                FROM xentraFechas xf
                LEFT JOIN xentraData x ON xf.idXentra = x.id
                LEFT JOIN areas a ON x.idArea = a.id
                LEFT JOIN personalSubAreas ps ON x.idSubArea = ps.id
                LEFT JOIN personal p ON x.responsable = p.dni
                LEFT JOIN xentraFechasEstados xfe ON xf.idEstado = xfe.id
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicial", fechaInicial);
        params.addValue("fechaFinal", fechaFinal);

        if (idPuesto == 2) {
            sql.append(" WHERE xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal ");
        } else if (idPuesto == 3) {
            if (idArea == 2) {
                sql.append(" WHERE p.idArea in (2,4) AND xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal ");
            } else {
                sql.append(" WHERE p.idArea = :idArea AND xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal ");
                params.addValue("idArea", idArea);
            }
        } else {
            sql.append(" WHERE x.responsable = :dni AND xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal ");
            params.addValue("dni", dni);
        }

        sql.append(" ORDER BY x.idArea, x.idSubArea ");

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            XentraReporteDTO obj = new XentraReporteDTO();
            obj.setId(rs.getInt("id"));
            obj.setIdXentra(rs.getInt("idXentra"));
            obj.setIdArea(rs.getInt("idArea"));
            obj.setDescArea(rs.getString("descArea"));
            obj.setIdSubArea(rs.getInt("idSubArea"));
            obj.setDescSubArea(rs.getString("descSubArea"));
            obj.setNombreReporte(rs.getString("nombreReporte"));
            obj.setResponsable(rs.getString("responsable"));
            obj.setResponsableNombreApellido(rs.getString("responsableNombreApellido"));
            obj.setFecha(rs.getString("fecha"));
            obj.setIdEstado(rs.getInt("idEstado"));
            obj.setDescEstado(rs.getString("descEstado"));
            obj.setFechaEstado(rs.getString("fechaEstado"));
            obj.setHoraEstado(rs.getString("horaEstado"));
            obj.setEstadoLogico(rs.getString("estadoLogico"));
            return obj;
        });
    }
}
