package com.joa.prexixion.repositories;

import com.joa.prexixion.dto.ClieCuentaBancariaProjection;
import com.joa.prexixion.entities.ClienteExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClieCuentaBancariaRepository extends JpaRepository<ClienteExcelEntity, String> {

    @Query(nativeQuery = true, value = """
        SELECT
            ce.descripcion AS descEstado,
            (SELECT SUBSTRING(
                (SELECT ',' + acInicioCom AS 'data()'
                 FROM clienteAltaCom ac
                 WHERE ac.idCliente = ccb.idCliente
                 FOR XML PATH('')), 2, 9999)
            ) AS acInicioCom,
            ccb.idCliente,
            c.y,
            ctc.abreviatura AS abrContribuyente,
            c.razonSocial,
            ccb.ccbCCI
        FROM clienteCuentasBancarias ccb
        LEFT JOIN cliente c ON ccb.idCliente = c.ruc
        LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
        LEFT JOIN clientesTiposContribuyente ctc ON c.idContribuyente = ctc.id
        WHERE ce.id IN (:estados) AND c.y IN (:grupos)
        """)
    List<ClieCuentaBancariaProjection> getCuentaBancariaData(
            @Param("estados") List<Integer> estados,
            @Param("grupos") List<Integer> grupos
    );
}
