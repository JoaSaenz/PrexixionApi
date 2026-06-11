package com.joa.prexixionapi.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.dto.ReunionDTO;
import com.joa.prexixionapi.dto.ReunionDataTablesRequest;
import com.joa.prexixionapi.dto.ReunionParticipanteExternoDTO;
import com.joa.prexixionapi.dto.ReunionParticipanteInternoDTO;
import com.joa.prexixionapi.dto.ReunionTemaDTO;
import com.joa.prexixionapi.dto.ReunionAreaDTO;
import com.joa.prexixionapi.dto.ReunionAcuerdoDTO;
import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.Gclass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class ReunionRepository {

    @PersistenceContext
    private EntityManager em;

    private String getBaseSelect() {
        return """
                    SELECT r.id, r.idCliente, r.razonSocial, r.tipo, r.fecha, r.horaI, r.horaF, r.idEstado, re.descripcion as estado, r.otros
                    FROM reuniones r
                    INNER JOIN reunionesEstados re ON r.idEstado = re.id
                    WHERE 1=1
                """;
    }

    @SuppressWarnings("unchecked")
    public List<ReunionDTO> listServerSide(ReunionDataTablesRequest req) {
        StringBuilder sql = new StringBuilder(getBaseSelect());
        appendFilters(sql, req);

        sql.append(" ORDER BY r.fecha DESC, r.horaI DESC ");

        Query query = em.createNativeQuery(sql.toString(), Tuple.class);

        setFilterParameters(query, req);

        return mapTuples((List<Tuple>) query.getResultList());
    }

    public long countServerSide(ReunionDataTablesRequest req) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM reuniones r WHERE 1=1 ");
        appendFilters(sql, req);
        Query query = em.createNativeQuery(sql.toString());
        setFilterParameters(query, req);
        return ((Number) query.getSingleResult()).longValue();
    }

    public long countTotal() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM reuniones").getSingleResult()).longValue();
    }

    private void appendFilters(StringBuilder sql, ReunionDataTablesRequest req) {
        if (req.getEstados() != null && !req.getEstados().isEmpty()) {
            sql.append(" AND r.idEstado IN (").append(req.getEstados()).append(") ");
        }
        if (req.getSearch() != null && !req.getSearch().isEmpty()) {
            sql.append(" AND (r.razonSocial LIKE :search OR r.idCliente LIKE :search OR r.otros LIKE :search) ");
        }
    }

    private void setFilterParameters(Query query, ReunionDataTablesRequest req) {
        if (req.getSearch() != null && !req.getSearch().isEmpty()) {
            query.setParameter("search", "%" + req.getSearch() + "%");
        }
    }

    private List<ReunionDTO> mapTuples(List<Tuple> tuples) {
        List<ReunionDTO> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Cliente clie = new Cliente();
            clie.setRuc(tuple.get("idCliente", String.class));
            clie.setRazonSocial(tuple.get("razonSocial", String.class));

            ReunionDTO obj = ReunionDTO.builder()
                    .id(tuple.get("id", Integer.class))
                    .cliente(clie)
                    .tipo(tuple.get("tipo", String.class))
                    .fecha(tuple.get("fecha", String.class))
                    .horaI(tuple.get("horaI", String.class))
                    .horaF(tuple.get("horaF", String.class))
                    .estado(new Gclass(tuple.get("idEstado", Integer.class), tuple.get("estado", String.class)))
                    .otros(tuple.get("otros", String.class))
                    .build();
            list.add(obj);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<ReunionTemaDTO> fetchTemas(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT idReunion, idTema, tema, acuerdoTema FROM reunionesTemas WHERE idReunion IN (:ids)";
        return ((List<Tuple>) em.createNativeQuery(sql, Tuple.class)
                .setParameter("ids", ids)
                .getResultList()).stream()
                .map(t -> ReunionTemaDTO.builder()
                        .idReunion(t.get("idReunion", Integer.class))
                        .id(t.get("idTema", Integer.class))
                        .tema(t.get("tema", String.class))
                        .acuerdoTema(t.get("acuerdoTema", String.class))
                        .build())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReunionParticipanteExternoDTO> fetchParticipantesExternos(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT idReunion, nombres, cargo FROM reunionesParticipantesExternos WHERE idReunion IN (:ids)";
        return ((List<Tuple>) em.createNativeQuery(sql, Tuple.class)
                .setParameter("ids", ids)
                .getResultList()).stream()
                .map(t -> ReunionParticipanteExternoDTO.builder()
                        .idReunion(t.get("idReunion", Integer.class))
                        .nombres(t.get("nombres", String.class))
                        .cargo(t.get("cargo", String.class))
                        .build())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReunionParticipanteInternoDTO> fetchParticipantesInternos(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = """
                    SELECT rpi.idReunion, p.dni, p.apellidos, p.nombres, pu.descripcion as puesto
                    FROM reunionesParticipantesInternos rpi
                    INNER JOIN personal p ON rpi.dni = p.dni
                    LEFT JOIN personalPuestos pu ON p.idPuesto = pu.id
                    WHERE rpi.idReunion IN (:ids)
                """;
        return ((List<Tuple>) em.createNativeQuery(sql, Tuple.class)
                .setParameter("ids", ids)
                .getResultList()).stream()
                .map(t -> ReunionParticipanteInternoDTO.builder()
                        .idReunion(t.get("idReunion", Integer.class))
                        .dni(t.get("dni", String.class))
                        .apellidos(t.get("apellidos", String.class))
                        .nombres(t.get("nombres", String.class))
                        .puesto(t.get("puesto", String.class))
                        .build())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReunionAreaDTO> fetchAreas(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = """
                    SELECT ra.idReunion, a.id, a.descripcion
                    FROM reunionesAreas ra
                    INNER JOIN areas a ON ra.idArea = a.id
                    WHERE ra.idReunion IN (:ids)
                """;
        return ((List<Tuple>) em.createNativeQuery(sql, Tuple.class)
                .setParameter("ids", ids)
                .getResultList()).stream()
                .map(t -> ReunionAreaDTO.builder()
                        .idReunion(t.get("idReunion", Integer.class))
                        .id(t.get("id", Integer.class))
                        .descripcion(t.get("descripcion", String.class))
                        .build())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReunionAcuerdoDTO> fetchAcuerdos(List<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();
        String sql = "SELECT idReunion, idAcuerdo, acuerdo FROM reunionesAcuerdos WHERE idReunion IN (:ids)";
        return ((List<Tuple>) em.createNativeQuery(sql, Tuple.class)
                .setParameter("ids", ids)
                .getResultList()).stream()
                .map(t -> ReunionAcuerdoDTO.builder()
                        .idReunion(t.get("idReunion", Integer.class))
                        .id(t.get("idAcuerdo", Integer.class))
                        .acuerdo(t.get("acuerdo", String.class))
                        .build())
                .collect(Collectors.toList());
    }

    public ReunionDTO getReunion(int idReunion) {
        String sql = getBaseSelect() + " AND r.id = :id";
        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("id", idReunion);
        
        @SuppressWarnings("unchecked")
        List<Tuple> result = query.getResultList();
        if (result.isEmpty()) return null;
        
        ReunionDTO r = mapTuples(result).get(0);
        List<Integer> ids = List.of(idReunion);
        
        r.setTemas(fetchTemas(ids));
        r.setParticipantesExternos(fetchParticipantesExternos(ids));
        r.setParticipantesInternos(fetchParticipantesInternos(ids));
        
        var areasMap = fetchAreas(ids);
        r.setAreas(areasMap.stream().map(m -> new Gclass(m.getId(), m.getDescripcion())).collect(Collectors.toList()));
        
        var acuerdosMap = fetchAcuerdos(ids);
        r.setAcuerdos(acuerdosMap.stream().map(m -> new Gclass(m.getId(), m.getAcuerdo())).collect(Collectors.toList()));
        
        return r;
    }

    @Transactional
    public int insertReunion(ReunionDTO r) {
        String query = "INSERT INTO reuniones(idCliente, razonSocial, tipo, fecha, horaI, horaF, idEstado, otros) " +
                       "OUTPUT INSERTED.id " +
                       "VALUES (:idCliente, :razonSocial, :tipo, :fecha, :horaI, :horaF, :idEstado, :otros)";
        
        Integer generatedId = (Integer) em.createNativeQuery(query)
            .setParameter("idCliente", r.getCliente() != null ? r.getCliente().getRuc() : null)
            .setParameter("razonSocial", r.getCliente() != null ? r.getCliente().getRazonSocial() : null)
            .setParameter("tipo", r.getTipo())
            .setParameter("fecha", r.getFecha())
            .setParameter("horaI", r.getHoraI())
            .setParameter("horaF", r.getHoraF())
            .setParameter("idEstado", r.getEstado() != null ? r.getEstado().getId() : null)
            .setParameter("otros", r.getOtros())
            .getSingleResult();
            
        r.setId(generatedId);
        insertSubEntities(r);
        return generatedId;
    }
    
    @Transactional
    public void updateReunion(ReunionDTO r) {
        String query = "UPDATE reuniones SET idCliente = :idCliente, razonSocial = :razonSocial, tipo = :tipo, " +
                       "fecha = :fecha, horaI = :horaI, horaF = :horaF, idEstado = :idEstado, otros = :otros " +
                       "WHERE id = :id";
        em.createNativeQuery(query)
            .setParameter("idCliente", r.getCliente() != null ? r.getCliente().getRuc() : null)
            .setParameter("razonSocial", r.getCliente() != null ? r.getCliente().getRazonSocial() : null)
            .setParameter("tipo", r.getTipo())
            .setParameter("fecha", r.getFecha())
            .setParameter("horaI", r.getHoraI())
            .setParameter("horaF", r.getHoraF())
            .setParameter("idEstado", r.getEstado() != null ? r.getEstado().getId() : null)
            .setParameter("otros", r.getOtros())
            .setParameter("id", r.getId())
            .executeUpdate();
            
        // Delete sub-entities
        int idReunion = r.getId();
        em.createNativeQuery("DELETE FROM reunionesTemas WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesParticipantesExternos WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesParticipantesInternos WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesAreas WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesAcuerdos WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        
        insertSubEntities(r);
    }

    @Transactional
    public void deleteReunion(int idReunion) {
        em.createNativeQuery("DELETE FROM reunionesTemas WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesParticipantesExternos WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesParticipantesInternos WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesAreas WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reunionesAcuerdos WHERE idReunion = :id").setParameter("id", idReunion).executeUpdate();
        em.createNativeQuery("DELETE FROM reuniones WHERE id = :id").setParameter("id", idReunion).executeUpdate();
    }
    
    private void insertSubEntities(ReunionDTO r) {
        int generatedId = r.getId();
        if (r.getTemas() != null) {
            int i = 1;
            for (ReunionTemaDTO tema : r.getTemas()) {
                em.createNativeQuery("INSERT INTO reunionesTemas (idReunion, idTema, tema, acuerdoTema) VALUES (:idR, :idT, :t, :a)")
                    .setParameter("idR", generatedId).setParameter("idT", i++)
                    .setParameter("t", tema.getTema()).setParameter("a", tema.getAcuerdoTema())
                    .executeUpdate();
            }
        }
        if (r.getParticipantesExternos() != null) {
            int i = 1;
            for (ReunionParticipanteExternoDTO p : r.getParticipantesExternos()) {
                em.createNativeQuery("INSERT INTO reunionesParticipantesExternos (idReunion, id, nombres, cargo) VALUES (:idR, :idP, :n, :c)")
                    .setParameter("idR", generatedId).setParameter("idP", i++)
                    .setParameter("n", p.getNombres()).setParameter("c", p.getCargo())
                    .executeUpdate();
            }
        }
        if (r.getParticipantesInternos() != null) {
            for (ReunionParticipanteInternoDTO p : r.getParticipantesInternos()) {
                em.createNativeQuery("INSERT INTO reunionesParticipantesInternos (idReunion, dni) VALUES (:idR, :dni)")
                    .setParameter("idR", generatedId).setParameter("dni", p.getDni())
                    .executeUpdate();
            }
        }
        if (r.getAreas() != null) {
            for (Gclass a : r.getAreas()) {
                em.createNativeQuery("INSERT INTO reunionesAreas (idReunion, idArea) VALUES (:idR, :idA)")
                    .setParameter("idR", generatedId).setParameter("idA", a.getId())
                    .executeUpdate();
            }
        }
        if (r.getAcuerdos() != null) {
            int i = 1;
            for (Gclass a : r.getAcuerdos()) {
                em.createNativeQuery("INSERT INTO reunionesAcuerdos (idReunion, idAcuerdo, acuerdo) VALUES (:idR, :idA, :a)")
                    .setParameter("idR", generatedId).setParameter("idA", i++)
                    .setParameter("a", a.getDescripcion())
                    .executeUpdate();
            }
        }
    }
}
