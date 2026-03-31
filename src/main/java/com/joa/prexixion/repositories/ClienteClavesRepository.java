package com.joa.prexixion.repositories;

import com.joa.prexixion.dto.ClienteClavesProjection;
import com.joa.prexixion.entities.ClienteExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteClavesRepository extends JpaRepository<ClienteExcelEntity, String> {

    @Query(nativeQuery = true, value = """
        SELECT
            ce.descripcion AS descEstado,
            cl.ruc, cl.y,
            ctc.abreviatura AS abrContribuyente,
            cl.razonSocial,
            cl.solU, cl.solC,
            cl.upsU, cl.upsC,
            cl.soldierU, cl.soldierC,
            cl.signerU, cl.signerC,
            cl.afpU, cl.afpC,
            cl.sisU, cl.sisC,
            nc.abreviatura AS abrCategoria,
            CASE
                WHEN cl.idGrupoEconomico IS NOT NULL AND cl.idGrupoEconomico NOT IN (0, 1) THEN 1
                ELSE 0
            END AS categoriaGrupoE,
            CASE
                WHEN cso.idCliente IS NOT NULL THEN 1
                ELSE 0
            END AS categoriaStore
        FROM cliente cl
        LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id
        LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id
        LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
        LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id
        LEFT JOIN (
            SELECT DISTINCT idCliente FROM clienteServiciosOtros WHERE soIdServicioOtro = 6
        ) cso ON cl.ruc = cso.idCliente
        WHERE ce.id IN (:estados) AND cl.y IN (:grupos)
        ORDER BY cl.y, ce.descripcion, cl.razonSocial
        """)
    List<ClienteClavesProjection> getClavesData(
            @Param("estados") List<Integer> estados,
            @Param("grupos") List<Integer> grupos
    );
}
