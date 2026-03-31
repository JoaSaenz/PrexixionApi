package com.joa.prexixion.repositories;

import com.joa.prexixion.dto.ClienteExcelProjection;
import com.joa.prexixion.entities.ClienteExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteExcelRepository extends JpaRepository<ClienteExcelEntity, String> {

    @Query(nativeQuery = true, value = """
        SELECT cl.idEstado, ce.descripcion AS descEstado, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(acInicioCom,'') AS 'data()' FROM  clienteAltaCom ac WHERE ac.idCliente = cl.ruc FOR XML  PATH('')), 2, 9999)) AS acInicioCom, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(acAnioPeriodoInicio,'') + ISNULL(acMesPeriodoInicio,'') AS 'data()' 
        FROM clienteAltaCom ac WHERE ac.idCliente = cl.ruc FOR XML PATH('')), 2, 9999)) AS acPeriodoInicioCom, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(acAnioPeriodoFin,'') + ISNULL(acMesPeriodoFin,'') AS 'data()' 
        FROM clienteAltaCom ac WHERE ac.idCliente = cl.ruc FOR XML PATH('')), 2, 9999)) AS acPeriodoFinCom, 
        cl.codigoCliente, cl.ruc, cl.y,
        cl.idContribuyente, ctc.descripcion AS descContribuyente, ctc.abreviatura AS abrContribuyente, 
        cl.razonSocial, cl.nombreCorto, cl.idRubro, ru.descripcion AS descRubro, 
        cl.idTipoServicio, cts.abreviatura AS abrServicio, cts.descripcion AS descServicio, 
        cl.nroEmpresa, cl.fInscripcion, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(atInicio,'') AS 'data()' FROM  clienteActividadesTributarias at WHERE at.idCliente = cl.ruc FOR XML  PATH('')), 2, 9999)) AS atInicio, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(atSuspension,'') AS 'data()' FROM  clienteActividadesTributarias at WHERE at.idCliente = cl.ruc FOR XML  PATH('')), 2, 9999)) AS atSuspension, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(atBaja,'') AS 'data()' FROM  clienteActividadesTributarias at WHERE at.idCliente = cl.ruc FOR XML  PATH('')), 2, 9999)) AS atBaja, 
        cl.fRetiro, cl.fPle, cl.fPrico, cl.fBuc, cl.fAgentePer, cl.fAgenteRet, 
        cl.fEmElec, cl.fPortSunat, cl.fSistCont, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(ccbCuenta,'') AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999)) AS ccbCuenta, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(ccbUsuario,'') AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999)) AS ccbUsuario, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(ccbClave,'') AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999)) AS ccbClave, 
        cl.solU, cl.solC, cl.soldierU, cl.soldierC, cl.afpU, cl.afpC, cl.sisU, cl.sisC, cl.upsU, cl.upsC, cl.signerU, cl.signerC, 
        cl.rlMicro, cl.rlPequenia, cl.rlGeneral, cl.rlConstruccion, cl.rlAgrario, cl.rlAcreditado, cl.rlNoAcreditado, 
        cl.rtRus, cl.rtEspecial, cl.rtGeneral, cl.rtMypeTributario, cl.rtAmazonico, cl.rtAgrario, 
        CASE WHEN cl.rtRus = 1 THEN 'RUS' 
        WHEN cl.rtEspecial = 1 THEN 'ESPECIAL'
        WHEN cl.rtGeneral = 1 THEN 'GENERAL' 
        WHEN cl.rtMypeTributario = 1 THEN 'MYPE T' 
        WHEN cl.rtAmazonico = 1 THEN 'AMAZÓNICO' 
        WHEN cl.rtAgrario = 1 THEN 'AGRARIO' 
        END AS rt, 
        cl.rt1ra, cl.rt2da, cl.rt3ra, cl.rt4ta, cl.rt5ta, 
        cl.partidaRRPP, cl.idGrupoEconomico, ge.descripcion as descGrupoEconomico, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioDesde,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 1 FOR XML  PATH('')), 2, 9999)) AS stDesdePdtAnual, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioHasta,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 1 FOR XML  PATH('')), 2, 9999)) AS stHastaPdtAnual, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioDesde,'') + '-' + ISNULL(stMesDesde,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 2 FOR XML  PATH('')), 2, 9999)) AS stDesdePdt601, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioHasta,'') + '-' + ISNULL(stMesHasta,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 2 FOR XML  PATH('')), 2, 9999)) AS stHastaPdt601, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioDesde,'') + '-' + ISNULL(stMesDesde,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 3 FOR XML  PATH('')), 2, 9999)) AS stDesdePdt617,
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioHasta,'') + '-' + ISNULL(stMesHasta,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 3 FOR XML  PATH('')), 2, 9999)) AS stHastaPdt617, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioDesde,'') + '-' + ISNULL(stMesDesde,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 4 FOR XML  PATH('')), 2, 9999)) AS stDesdePdt621, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioHasta,'') + '-' + ISNULL(stMesHasta,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 4 FOR XML  PATH('')), 2, 9999)) AS stHastaPdt621, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioDesde,'') + '-' + ISNULL(stMesDesde,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 5 FOR XML  PATH('')), 2, 9999)) AS stDesdePleCompras, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioHasta,'') + '-' + ISNULL(stMesHasta,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 5 FOR XML  PATH('')), 2, 9999)) AS stHastaPleCompras, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioDesde,'') + '-' + ISNULL(stMesDesde,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 6 FOR XML  PATH('')), 2, 9999)) AS stDesdePleDiario, 
        (SELECT SUBSTRING 
        ((SELECT ',' + ISNULL(stAnioHasta,'') + '-' + ISNULL(stMesHasta,'') AS 'data()' FROM  clienteServiciosTributarios st WHERE st.idCliente = cl.ruc AND st.stIdServicioTributario = 6 FOR XML  PATH('')), 2, 9999)) AS stHastaPleDiario, 
        s.idCategoria, nc.abreviatura as abrCategoria, nc.descripcion as descCategoria,
        CASE 
                WHEN cl.idGrupoEconomico IS NOT NULL 
                     AND cl.idGrupoEconomico NOT IN (0, 1) 
                THEN 1 
                ELSE 0 
        END AS categoriaGrupoE,
        CASE 
                WHEN cso.idCliente IS NOT NULL THEN 1 
                ELSE 0 
        END AS categoriaStore 
        FROM cliente cl 
        LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id 
        LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id 
        LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id 
        LEFT JOIN rubros ru ON cl.idRubro = ru.id 
        LEFT JOIN gruposEconomicos ge on cl.idGrupoEconomico = ge.id 
        LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente 
        LEFT JOIN signerNivelesCategorias nc ON s.idCategoria = nc.id 
        LEFT JOIN ( 
            SELECT DISTINCT idCliente 
            FROM clienteServiciosOtros 
            WHERE soIdServicioOtro = 6 
         ) cso ON cl.ruc = cso.idCliente 
        WHERE ce.id IN (:estados) AND cl.y IN (:grupos)
    """)
    List<ClienteExcelProjection> getExcelData(@Param("estados") List<Integer> estados, @Param("grupos") List<Integer> grupos);

}
