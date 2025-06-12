package com.joa.prexixion.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.XentraReporte;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

@Repository
public class XentraReporteRepository {

    @PersistenceContext
    private EntityManager em;

    public List<XentraReporte> list(int idPuesto, int idArea, String dni, String fechaInicial, String fechaFinal) {
        List<XentraReporte> list = new ArrayList<>();
        String sql = """
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
                """;

        if (idPuesto == 2) {
            sql += """
                    WHERE xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal
                    """;
        } else if (idPuesto == 3) {
            if (idArea == 2) {
                sql += """
                        WHERE p.idArea in (2,4) AND xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal
                        """;
            } else {
                sql += """
                        WHERE p.idArea = :idArea AND xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal
                        """;
            }
        } else {
            sql += """
                    WHERE x.responsable = :dni AND xf.fecha >= :fechaInicial AND xf.fecha <= :fechaFinal
                    """;
        }
        sql += """
                ORDER BY x.idArea, x.idSubArea
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        if (idPuesto != 2) {
            if (idPuesto == 3) {
                if (idArea != 2) {
                    query.setParameter("idArea", idArea);
                }
            } else {
                query.setParameter("dni", dni);
            }
        }
        query.setParameter("fechaInicial", fechaInicial);
        query.setParameter("fechaFinal", fechaFinal);
        List<Tuple> retultTuples = query.getResultList();

        for (Tuple tuple : retultTuples) {
            XentraReporte obj = new XentraReporte();
            obj.setId(tuple.get("id", Integer.class));
            obj.setIdXentra(tuple.get("idXentra", Integer.class));
            obj.setIdArea(tuple.get("idArea", Integer.class));
            obj.setDescArea(tuple.get("descArea", String.class));
            obj.setIdSubArea(tuple.get("idSubArea", Integer.class));
            obj.setDescSubArea(tuple.get("descSubArea", String.class));
            obj.setNombreReporte(tuple.get("nombreReporte", String.class));
            obj.setResponsable(tuple.get("responsable", String.class));
            obj.setResponsableNombreApellido(tuple.get("responsableNombreApellido", String.class));
            obj.setFecha(tuple.get("fecha", String.class));
            obj.setIdEstado(tuple.get("idEstado", Integer.class));
            obj.setDescEstado(tuple.get("descEstado", String.class));
            obj.setFechaEstado(tuple.get("fechaEstado", String.class));
            obj.setHoraEstado(tuple.get("horaEstado", String.class));
            obj.setEstadoLogico(tuple.get("estadoLogico", String.class));
            list.add(obj);
        }

        return list;
    }

}
