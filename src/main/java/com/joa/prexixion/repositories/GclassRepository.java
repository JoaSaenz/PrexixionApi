package com.joa.prexixion.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.Gclass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GclassRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Gclass> list(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName + " ORDER BY descripcion ";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    public List<Gclass> listOrderById(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    public List<Gclass> listAbbrPlusEmptyOrderById(String tableName) {
        String sql = "SELECT id, abreviatura, descripcion FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        // Crear objeto vacío
        Gclass gclassVacio = new Gclass();
        gclassVacio.setId(0);
        gclassVacio.setAbreviatura("-");

        gclasses.add(0, gclassVacio);

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setAbreviatura((String) result[1]);
            gclass.setDescripcion((String) result[2]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    public List<Gclass> listPlusEmpty(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName + " ORDER BY descripcion";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        // Crear objeto vacío
        Gclass gclassVacio = new Gclass();
        gclassVacio.setId(0);
        gclassVacio.setDescripcion("-");

        gclasses.add(0, gclassVacio);

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    public List<Gclass> listPlusEmptyOrderById(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        // Crear objeto vacío
        Gclass gclassVacio = new Gclass();
        gclassVacio.setId(0);
        gclassVacio.setDescripcion("-");

        gclasses.add(0, gclassVacio);

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    // LOGIN PROCESOS CUSTOM ORDER
    // 1: SI
    // 0: NO
    // 2: N/A
    public List<Gclass> listLoginOptions(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName
                + " ORDER BY CASE WHEN id = 0 THEN 2 "
                + "               WHEN id = 1 THEN 1 "
                + "               WHEN id = 2 THEN 3 "
                + "          END ";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    // LOGIN PROCESOS CUSTOM ORDER OPTIONS SPECIALS
    // 0: NO
    // 1: SI
    // 2: N/A
    // 3: REG o VAL
    public List<Gclass> listLoginOptionsSpecials(String tableName) {
        String sql = "SELECT id, descripcion FROM " + tableName
                + " ORDER BY CASE WHEN id = 0 THEN 3 "
                + "               WHEN id = 1 THEN 2 "
                + "               WHEN id = 2 THEN 4 "
                + "               WHEN id = 3 THEN 1 "
                + "          END ";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    public List<Gclass> listAbbr(String tableName) {
        String sql = "SELECT id, abreviatura, descripcion FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setAbreviatura((String) result[1]);
            gclass.setDescripcion((String) result[2]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    public List<Gclass> listCodigoDescripcion(String tableName) {
        String sql = "SELECT id, codigo as abreviatura, descripcion FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();

        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setAbreviatura((String) result[1]);
            gclass.setDescripcion((String) result[2]);
            gclasses.add(gclass);
        }

        return gclasses;
    }

    @Transactional
    public int insertUpdate(String tableName, Gclass obj) {
        int msg = 0;
        String sqlCheck = "SELECT count(*) FROM " + tableName + " WHERE id = :id";
        Query queryCheck = entityManager.createNativeQuery(sqlCheck);
        queryCheck.setParameter("id", obj.getId());
        Number count = (Number) queryCheck.getSingleResult();
        
        if (count.intValue() > 0) {
            String sqlUpdate = "UPDATE " + tableName + " SET abreviatura = :abbr, descripcion = :desc WHERE id = :id";
            Query queryUpdate = entityManager.createNativeQuery(sqlUpdate);
            queryUpdate.setParameter("abbr", obj.getAbreviatura());
            queryUpdate.setParameter("desc", obj.getDescripcion());
            queryUpdate.setParameter("id", obj.getId());
            queryUpdate.executeUpdate();
            msg = 2; // Updated
        } else {
            String sqlInsert = "INSERT INTO " + tableName + " (abreviatura, descripcion) VALUES (:abbr, :desc)";
            Query queryInsert = entityManager.createNativeQuery(sqlInsert);
            queryInsert.setParameter("abbr", obj.getAbreviatura());
            queryInsert.setParameter("desc", obj.getDescripcion());
            queryInsert.executeUpdate();
            msg = 1; // Inserted
        }
        return msg;
    }

    @Transactional
    public int insertUpdate2(String tableName, Gclass obj) {
        int rpta = 0;
        String sqlCheck = "SELECT count(*) FROM " + tableName + " WHERE id = :id";
        Query queryCheck = entityManager.createNativeQuery(sqlCheck);
        queryCheck.setParameter("id", obj.getId());
        Number count = (Number) queryCheck.getSingleResult();
        
        if (count.intValue() > 0) {
            String sqlUpdate = "UPDATE " + tableName + " SET descripcion = :desc WHERE id = :id";
            Query queryUpdate = entityManager.createNativeQuery(sqlUpdate);
            queryUpdate.setParameter("desc", obj.getDescripcion());
            queryUpdate.setParameter("id", obj.getId());
            queryUpdate.executeUpdate();
            rpta = 2; // Updated
        } else {
            String sqlInsert = "INSERT INTO " + tableName + " (descripcion) VALUES (:desc)";
            Query queryInsert = entityManager.createNativeQuery(sqlInsert);
            queryInsert.setParameter("desc", obj.getDescripcion());
            queryInsert.executeUpdate();
            rpta = 1; // Inserted
        }
        return rpta;
    }

    @Transactional
    public int delete(String tableName, int id) {
        String sqlDelete = "DELETE FROM " + tableName + " WHERE id = :id";
        Query queryDelete = entityManager.createNativeQuery(sqlDelete);
        queryDelete.setParameter("id", id);
        queryDelete.executeUpdate();
        return 3; // Deleted
    }

    public boolean existe(String tableName, int id) {
        String sql = "SELECT count(*) FROM " + tableName + " WHERE id = :id";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", id);
        Number count = (Number) query.getSingleResult();
        return count.intValue() > 0;
    }

    public Gclass getOne(String tableName, int id) {
        String sql = "SELECT id, descripcion FROM " + tableName + " WHERE id = :id";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", id);
        
        List<Object[]> results = query.getResultList();
        Gclass gclass = new Gclass();
        if(!results.isEmpty()) {
            Object[] result = results.get(0);
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
        }
        return gclass;
    }

    public Gclass getOne2(String tableName, int id) {
        String sql = "SELECT id, abreviatura, descripcion FROM " + tableName + " WHERE id = :id";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", id);
        
        List<Object[]> results = query.getResultList();
        Gclass gclass = new Gclass();
        if(!results.isEmpty()) {
            Object[] result = results.get(0);
            gclass.setId(((Number) result[0]).intValue());
            gclass.setAbreviatura((String) result[1]);
            gclass.setDescripcion((String) result[2]);
        }
        return gclass;
    }

    public List<Gclass> listCredenciales(int id) {
        String sql = "{call credencialesEquipo(:id)}";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", id);
        
        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();
        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setAbreviatura((String) result[1]); // username
            gclass.setDescripcion((String) result[2]); // clave
            gclasses.add(gclass);
        }
        return gclasses;
    }

    public Gclass vlanIp(int ip) {
        String sql = "select v.descripcion as VLAN from EquiposIp i "
                + "inner join EquiposVlan v on i.idVlan = v.id "
                + "where i.id= :ip";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("ip", ip);
        
        List<String> results = query.getResultList();
        Gclass gclass = new Gclass();
        if (!results.isEmpty()) {
            gclass.setDescripcion(results.get(0));
        }
        return gclass;
    }

    public List<Gclass> listFromTwoTables(String tabla1, String tabla2, int id) {
        String sql = "select id, descripcion from " + tabla1 + " where id in (select b from " + tabla2 + " where a = :id) order by descripcion";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", id);
        
        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();
        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }
        return gclasses;
    }

    public List<Gclass> listFromThreeTables(String tabla1, String tabla2, int id1, int id2) {
        String sql = "select id, descripcion from " + tabla1 + " where id in (select z from " + tabla2 + " where x = :id1 and y = :id2) order by descripcion";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id1", id1);
        query.setParameter("id2", id2);
        
        List<Object[]> results = query.getResultList();
        List<Gclass> gclasses = new ArrayList<>();
        for (Object[] result : results) {
            Gclass gclass = new Gclass();
            gclass.setId(((Number) result[0]).intValue());
            gclass.setDescripcion((String) result[1]);
            gclasses.add(gclass);
        }
        return gclasses;
    }

}
