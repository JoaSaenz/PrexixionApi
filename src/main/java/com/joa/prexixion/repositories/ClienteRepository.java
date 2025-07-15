package com.joa.prexixion.repositories;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.Cliente;
import com.joa.prexixion.entities.Gclass;
import com.joa.prexixion.entities.SignerNivel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class ClienteRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Cliente> list() {
        List<Cliente> list = new ArrayList<>();
        String sql = """
                SELECT cl.idEstado, ce.descripcion AS descEstado, cl.idTipoServicio, cts.abreviatura AS abrServicio, cts.descripcion AS descServicio,
                cl.codigoCliente, cl.ruc, cl.y,
                cl.idContribuyente, ctc.descripcion AS descContribuyente, ctc.abreviatura AS abrContribuyente, cl.razonSocial, cl.nombreCorto,
                (SELECT TOP 1 CONCAT(stAnioDesde, '-', stMesDesde)periodoI FROM clienteServiciosTributarios y WHERE y.stIdServicioTributario = 4 AND y.idCliente = cl.ruc ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoI,
                (SELECT TOP 1 CONCAT(stAnioHasta, '-', stMesHasta)periodoF FROM clienteServiciosTributarios y WHERE y.stIdServicioTributario = 4 AND y.idCliente = cl.ruc ORDER BY stAnioDesde DESC, stMesDesde DESC) AS periodoF,
                cl.taxReview, cl.fEntregaTaxReview,
                cl.solU, cl.solC, cl.upsU, cl.upsC, cl.soldierU, cl.soldierC,  cl.signerU, cl.signerC,
                (SELECT TOP 1 acInicioCom FROM clienteAltaCom x WHERE x.idCliente = cl.ruc ORDER BY acInicioCom DESC) as altaCom,
                (SELECT TOP 1 CONCAT(acAnioPeriodoInicio, '-', acMesPeriodoInicio)periodoI FROM clienteAltaCom y WHERE y.idCliente = cl.ruc ORDER BY acAnioPeriodoInicio DESC, acMesPeriodoInicio DESC) AS periodoInicioCom,
                cl.fInscripcion, cl.fRetiro, (SELECT SUBSTRING
                ((SELECT ',' + ccbCuenta AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999) AS CbCuenta) AS ccbCuenta,
                (SELECT SUBSTRING
                ((SELECT ',' + ccbUsuario AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999) AS CbUsuario) AS ccbUsuario,
                (SELECT SUBSTRING
                ((SELECT ',' + ccbClave AS 'data()' FROM  clienteCuentasBancarias cb WHERE cb.idCliente = cl.ruc AND cb.ccbIdTipoCtBancaria = 3 FOR XML  PATH('')), 2, 9999) AS CbClave) AS ccbClave,
                (SELECT TOP 1 CONCAT(soAnioDesde, '-', soMesDesde)periodoIActualizacion FROM clienteServiciosOtros x WHERE x.soIdServicioOtro = 4 AND x.idCliente = cl.ruc ORDER BY soAnioDesde DESC, soMesDesde DESC) AS periodoIActualizacion,
                (SELECT TOP 1 CONCAT(soAnioHasta, '-', soMesHasta)periodoFActualizacion FROM clienteServiciosOtros x WHERE x.soIdServicioOtro = 4 AND x.idCliente = cl.ruc ORDER BY soAnioDesde DESC, soMesDesde DESC) AS periodoFActualizacion,
                s.idNivelF, sf.abreviatura AS abrNivelF, sf.descripcion AS descNivelF, s.idNivelX3, st.abreviatura AS abrNivelX3, st.descripcion AS descNivelX3,
                cl.idGrupoEconomico, ge.descripcion as descGrupoEconomico,
                l.administracion, l.ruc as rucBalance
                FROM cliente cl
                LEFT JOIN clientsEstados ce ON cl.idEstado = ce.id
                LEFT JOIN clientsTipoServicio cts ON cl.idTipoServicio = cts.id
                LEFT JOIN clientesTiposContribuyente ctc ON cl.idContribuyente = ctc.id
                LEFT JOIN signerNiveles s ON cl.ruc = s.idCliente
                LEFT JOIN signerNivelesFijos sf ON s.idNivelF = sf.id
                LEFT JOIN signerNivelesTemperatura st ON s.idNivelX3 = st.id
                LEFT JOIN loginBalances l ON cl.ruc = l.ruc and l.anio =  '2023' AND l.mes = '13'
                LEFT JOIN gruposEconomicos ge ON cl.idGrupoEconomico = ge.id
                """;
                Query query = em.createNativeQuery(sql, Tuple.class);
                List<Tuple> resultTuples = query.getResultList();

                for (Tuple tuple : resultTuples) {
                    Cliente obj = new Cliente();
                    obj.setEstado(new Gclass(tuple.get("idEstado", Integer.class),tuple.get("descEstado", String.class)));
                    obj.setServicio(new Gclass(tuple.get("idTipoServicio", Integer.class),tuple.get("abrServicio", String.class),tuple.get("descServicio", String.class)));
                    obj.setCodigoCliente(tuple.get("codigoCliente", String.class));
                    obj.setRuc(tuple.get("ruc", String.class));

                    Character yChar = tuple.get("y", Character.class);
                    obj.setY(yChar != null ? yChar.toString() : null);
                    
                    obj.setContribuyente(new Gclass(tuple.get("idContribuyente", Integer.class),tuple.get("abrContribuyente", String.class),tuple.get("descContribuyente", String.class)));
                    obj.setRazonSocial(tuple.get("razonSocial", String.class));
                    obj.setNombreCorto(tuple.get("nombreCorto", String.class));
                    obj.setPeriodoInicioCom(tuple.get("periodoInicioCom", String.class));
                    obj.setPeriodoI621(tuple.get("periodoI", String.class));
                    obj.setPeriodoF621(tuple.get("periodoF", String.class));
                    obj.setTaxReview(tuple.get("taxReview", Integer.class) == null ? 0 : tuple.get("taxReview", Integer.class));
                    obj.setfEntregaTaxReview(tuple.get("fEntregaTaxReview", String.class));
                    obj.setSolU(tuple.get("solU", String.class));
                    obj.setSolC(tuple.get("solC", String.class));
                    obj.setUpsU(tuple.get("upsU", String.class));
                    obj.setUpsC(tuple.get("upsC", String.class));
                    obj.setSoldierU(tuple.get("soldierU", String.class));
                    obj.setSoldierC(tuple.get("soldierC", String.class));
                    obj.setSignerU(tuple.get("signerU", String.class));
                    obj.setSignerC(tuple.get("signerC", String.class));
                    obj.setfInscripcion(tuple.get("fInscripcion", String.class));
                    obj.setAltaCom(tuple.get("altaCom", String.class));
                    obj.setfRetiro(tuple.get("fRetiro", String.class));
                    obj.setCcbCuenta(tuple.get("ccbCuenta", String.class));
                    obj.setCcbUsuario(tuple.get("ccbUsuario", String.class));
                    obj.setCcbClave(tuple.get("ccbClave", String.class));
                    obj.setPeriodoIActualizacion(tuple.get("periodoIActualizacion", String.class));
                    obj.setPeriodoFActualizacion(tuple.get("periodoFActualizacion", String.class));

                    SignerNivel sn = new SignerNivel();
                    sn.setNivelFijo(new Gclass(tuple.get("idNivelF", Integer.class) == null ? 0 : tuple.get("idNivelF", Integer.class),
                    tuple.get("abrNivelF", String.class),tuple.get("descNivelF", String.class)));
                    sn.setNivelX3(new Gclass(tuple.get("idNivelX3", Integer.class) == null ? 0 : tuple.get("idNivelX3", Integer.class),
                    tuple.get("abrNivelX3", String.class),tuple.get("descNivelX3", String.class)));
                    obj.setSignerNivel(sn);

                    obj.setGrupoEconomico(new Gclass(tuple.get("idGrupoEconomico", Integer.class) == null ? 0 : tuple.get("idGrupoEconomico", Integer.class),
                    tuple.get("descGrupoEconomico", String.class)));
                    obj.setAdministracion(tuple.get("rucBalance", String.class) == null ? 1 : tuple.get("administracion", Integer.class));
                    list.add(obj);
                }

        return list;
    }
}
