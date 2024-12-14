package com.joa.prexixion.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.Gclass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

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

}
