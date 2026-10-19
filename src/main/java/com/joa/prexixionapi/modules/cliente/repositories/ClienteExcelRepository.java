package com.joa.prexixionapi.modules.cliente.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.joa.prexixionapi.modules.cliente.repositories.ClienteExcelRepository;
import com.joa.prexixionapi.modules.cliente.dto.ClienteExcelDTO;

@Repository
@RequiredArgsConstructor
public class ClienteExcelRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ClienteExcelDTO> getExcelData(List<Integer> estados, List<Integer> grupos) {
        String sql = """
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
            cl.fEmElec, cl.fPortSunat, cl.fSistCont, cl.iboxSunat, 
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
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("estados", estados)
                .addValue("grupos", grupos);

        return jdbcTemplate.query(sql, params, this::mapRow);
    }

    private ClienteExcelDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ClienteExcelDTO.builder()
                .idEstado(rs.getInt("idEstado"))
                .descEstado(rs.getString("descEstado"))
                .acInicioCom(rs.getString("acInicioCom"))
                .acPeriodoInicioCom(rs.getString("acPeriodoInicioCom"))
                .acPeriodoFinCom(rs.getString("acPeriodoFinCom"))
                .codigoCliente(rs.getString("codigoCliente"))
                .ruc(rs.getString("ruc"))
                .y(rs.getString("y"))
                .idContribuyente(rs.getInt("idContribuyente"))
                .descContribuyente(rs.getString("descContribuyente"))
                .abrContribuyente(rs.getString("abrContribuyente"))
                .razonSocial(rs.getString("razonSocial"))
                .nombreCorto(rs.getString("nombreCorto"))
                .idRubro(rs.getInt("idRubro"))
                .descRubro(rs.getString("descRubro"))
                .idTipoServicio(rs.getInt("idTipoServicio"))
                .abrServicio(rs.getString("abrServicio"))
                .descServicio(rs.getString("descServicio"))
                .nroEmpresa(rs.getString("nroEmpresa"))
                .fInscripcion(rs.getString("fInscripcion"))
                .atInicio(rs.getString("atInicio"))
                .atSuspension(rs.getString("atSuspension"))
                .atBaja(rs.getString("atBaja"))
                .fRetiro(rs.getString("fRetiro"))
                .fPle(rs.getString("fPle"))
                .fPrico(rs.getString("fPrico"))
                .fBuc(rs.getString("fBuc"))
                .fAgentePer(rs.getString("fAgentePer"))
                .fAgenteRet(rs.getString("fAgenteRet"))
                .fEmElec(rs.getString("fEmElec"))
                .fPortSunat(rs.getString("fPortSunat"))
                .fSistCont(rs.getString("fSistCont"))
                .ccbCuenta(rs.getString("ccbCuenta"))
                .ccbUsuario(rs.getString("ccbUsuario"))
                .ccbClave(rs.getString("ccbClave"))
                .solU(rs.getString("solU"))
                .solC(rs.getString("solC"))
                .soldierU(rs.getString("soldierU"))
                .soldierC(rs.getString("soldierC"))
                .afpU(rs.getString("afpU"))
                .afpC(rs.getString("afpC"))
                .sisU(rs.getString("sisU"))
                .sisC(rs.getString("sisC"))
                .upsU(rs.getString("upsU"))
                .upsC(rs.getString("upsC"))
                .signerU(rs.getString("signerU"))
                .signerC(rs.getString("signerC"))
                .rlMicro(rs.getInt("rlMicro"))
                .rlPequenia(rs.getInt("rlPequenia"))
                .rlGeneral(rs.getInt("rlGeneral"))
                .rlConstruccion(rs.getInt("rlConstruccion"))
                .rlAgrario(rs.getInt("rlAgrario"))
                .rlAcreditado(rs.getInt("rlAcreditado"))
                .rlNoAcreditado(rs.getInt("rlNoAcreditado"))
                .rtRus(rs.getInt("rtRus"))
                .rtEspecial(rs.getInt("rtEspecial"))
                .rtGeneral(rs.getInt("rtGeneral"))
                .rtMypeTributario(rs.getInt("rtMypeTributario"))
                .rtAmazonico(rs.getInt("rtAmazonico"))
                .rtAgrario(rs.getInt("rtAgrario"))
                .rt(rs.getString("rt"))
                .rt1ra(rs.getInt("rt1ra"))
                .rt2da(rs.getInt("rt2da"))
                .rt3ra(rs.getInt("rt3ra"))
                .rt4ta(rs.getInt("rt4ta"))
                .rt5ta(rs.getInt("rt5ta"))
                .partidaRRPP(rs.getString("partidaRRPP"))
                .idGrupoEconomico(rs.getInt("idGrupoEconomico"))
                .descGrupoEconomico(rs.getString("descGrupoEconomico"))
                .stDesdePdtAnual(rs.getString("stDesdePdtAnual"))
                .stHastaPdtAnual(rs.getString("stHastaPdtAnual"))
                .stDesdePdt601(rs.getString("stDesdePdt601"))
                .stHastaPdt601(rs.getString("stHastaPdt601"))
                .stDesdePdt617(rs.getString("stDesdePdt617"))
                .stHastaPdt617(rs.getString("stHastaPdt617"))
                .stDesdePdt621(rs.getString("stDesdePdt621"))
                .stHastaPdt621(rs.getString("stHastaPdt621"))
                .stDesdePleCompras(rs.getString("stDesdePleCompras"))
                .stHastaPleCompras(rs.getString("stHastaPleCompras"))
                .stDesdePleDiario(rs.getString("stDesdePleDiario"))
                .stHastaPleDiario(rs.getString("stHastaPleDiario"))
                .idCategoria(rs.getInt("idCategoria"))
                .abrCategoria(rs.getString("abrCategoria"))
                .descCategoria(rs.getString("descCategoria"))
                .categoriaGrupoE(rs.getInt("categoriaGrupoE"))
                .categoriaStore(rs.getInt("categoriaStore"))
                .iboxSunat(rs.getInt("iboxSunat"))
                .build();
    }
}
